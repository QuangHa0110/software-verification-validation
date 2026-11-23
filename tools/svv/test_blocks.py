"""Extract, wrap, and normalize standalone JUnit 5 test blocks from LLM output."""

import re
from dataclasses import dataclass

STANDARD_IMPORTS: tuple[str, ...] = (
    "import org.junit.jupiter.api.*;",
    "import static org.junit.jupiter.api.Assertions.*;",
    "import org.junit.jupiter.params.*;",
    "import org.junit.jupiter.params.provider.*;",
    "import java.time.*;",
    "import java.util.*;",
)

_JAVA_FENCE = re.compile(r"```[ \t]*java[ \t]*\r?\n(.*?)```", re.DOTALL | re.IGNORECASE)
_PACKAGE_LINE = re.compile(r"^\s*package\s+[\w.]+\s*;\s*$")
_IMPORT_LINE = re.compile(r"^\s*import\s+(?:static\s+)?[\w.*]+\s*;\s*$")
_CLASS_HEADER = re.compile(
    r"\s*(?:(?:public|protected|private|abstract|final|static)\s+)*class\s+[A-Za-z_$][\w$]*[^{]*\{",
    re.DOTALL,
)
_TEST_ANNOTATION = re.compile(r"@(?:[\w.]*\.)?(?:Test|ParameterizedTest)\b")
_METHOD_NAME_RE = re.compile(
    r"(?:\b(?:public|protected|private|static|final|synchronized|strictfp)\s+)*"
    r"(?:<[^>]+>\s+)?"
    r"(?:[A-Za-z_$][\w$.<>\[\],\s?]*\s+)?"
    r"([A-Za-z_$][\w$]*)\s*\("
)
_PROVIDER_ANN_NAMES = {
    "ValueSource", "CsvSource", "CsvFileSource", "EnumSource",
    "MethodSource", "ArgumentsSource", "NullSource", "EmptySource", "NullAndEmptySource",
}


@dataclass(frozen=True)
class TestBlock:
    __test__ = False

    method_name: str
    body: str
    imports: tuple[str, ...] = ()


def _iter_code_segments(code: str):
    """Yield (kind, text) where kind is 'code', 'string', or 'comment'."""
    n = len(code)
    i = 0
    buf: list[str] = []

    def flush():
        if buf:
            text = "".join(buf)
            buf.clear()
            return ("code", text)
        return None

    while i < n:
        ch = code[i]
        nxt = code[i + 1] if i + 1 < n else ""
        if ch == "/" and nxt == "/":
            seg = flush()
            if seg:
                yield seg
            start = i
            i += 2
            while i < n and code[i] != "\n":
                i += 1
            yield ("comment", code[start:i])
        elif ch == "/" and nxt == "*":
            seg = flush()
            if seg:
                yield seg
            start = i
            i += 2
            while i + 1 < n and not (code[i] == "*" and code[i + 1] == "/"):
                i += 1
            i = min(n, i + 2)
            yield ("comment", code[start:i])
        elif code.startswith('"""', i):
            seg = flush()
            if seg:
                yield seg
            start = i
            i += 3
            while i < n:
                if code[i] == "\\" and i + 1 < n:
                    i += 2
                    continue
                if code.startswith('"""', i):
                    i += 3
                    break
                i += 1
            yield ("string", code[start:i])
        elif ch in ('"', "'"):
            seg = flush()
            if seg:
                yield seg
            quote = ch
            start = i
            i += 1
            while i < n:
                if code[i] == "\\" and i + 1 < n:
                    i += 2
                    continue
                if code[i] == quote:
                    i += 1
                    break
                i += 1
            yield ("string", code[start:i])
        else:
            buf.append(ch)
            i += 1
    seg = flush()
    if seg:
        yield seg


def strip_comments(code: str) -> str:
    """Remove Java // and /* */ comments while preserving string/char literals."""
    out: list[str] = []
    for kind, text in _iter_code_segments(code):
        if kind == "comment":
            out.append("\n" if "\n" in text else " ")
        else:
            out.append(text)
    return "".join(out)


def normalize_body(code: str) -> str:
    """Strip Java comments and collapse whitespace outside string/char literals."""
    parts: list[str] = []
    for kind, text in _iter_code_segments(code):
        if kind == "comment":
            parts.append(" ")
        elif kind == "string":
            parts.append(text)
        else:
            parts.append(re.sub(r"\s+", " ", text))
    # Collapse adjacent spaces introduced at segment boundaries.
    merged: list[str] = []
    prev_space = False
    for part in parts:
        if not part:
            continue
        if part == " ":
            if not prev_space:
                merged.append(" ")
                prev_space = True
        else:
            if prev_space and part.startswith(" "):
                part = part[1:]
            merged.append(part)
            prev_space = part.endswith(" ")
    return "".join(merged).strip()


def _skip_balanced(code: str, pos: int, open_ch: str, close_ch: str) -> int:
    """Starting at pos (pointing to open_ch), return index right after matching close_ch."""
    n = len(code)
    depth = 0
    i = pos
    while i < n:
        ch = code[i]
        nxt = code[i + 1] if i + 1 < n else ""
        if ch == "/" and nxt == "/":
            i += 2
            while i < n and code[i] != "\n":
                i += 1
            continue
        if ch == "/" and nxt == "*":
            i += 2
            while i + 1 < n and not (code[i] == "*" and code[i + 1] == "/"):
                i += 1
            i = min(n, i + 2)
            continue
        if code.startswith('"""', i):
            i += 3
            while i < n:
                if code[i] == "\\" and i + 1 < n:
                    i += 2
                    continue
                if code.startswith('"""', i):
                    i += 3
                    break
                i += 1
            continue
        if ch in ('"', "'"):
            quote = ch
            i += 1
            while i < n:
                if code[i] == "\\" and i + 1 < n:
                    i += 2
                    continue
                if code[i] == quote:
                    i += 1
                    break
                i += 1
            continue
        if ch == open_ch:
            depth += 1
        elif ch == close_ch:
            depth -= 1
            if depth == 0:
                return i + 1
        i += 1
    return n


def _unwrap_outer_class(code: str) -> str:
    """If code is wrapped in a single outer class declaration, return its inner body."""
    n = len(code)
    i = 0
    while i < n:
        while i < n and code[i].isspace():
            i += 1
        if code.startswith("//", i):
            while i < n and code[i] != "\n":
                i += 1
            continue
        if code.startswith("/*", i):
            end = code.find("*/", i + 2)
            i = n if end == -1 else end + 2
            continue
        break
    orig_match = _CLASS_HEADER.match(code, i)
    if not orig_match:
        return code
    open_brace = orig_match.end() - 1
    close_end = _skip_balanced(code, open_brace, "{", "}")
    if close_end <= open_brace + 1:
        return code
    if strip_comments(code[close_end:]).strip():
        return code
    inner = code[open_brace + 1 : close_end - 1]
    return inner.strip("\r\n")


def _parse_leading_annotations(code: str) -> tuple[list[tuple[str, str]], int]:
    """Parse leading Java annotations, returning ([(simple_name, full_text), ...], next_pos)."""
    n = len(code)
    i = 0
    annotations: list[tuple[str, str]] = []
    while i < n:
        while i < n and code[i].isspace():
            i += 1
        if i >= n:
            break
        if code.startswith("//", i):
            while i < n and code[i] != "\n":
                i += 1
            continue
        if code.startswith("/*", i):
            end = code.find("*/", i + 2)
            i = n if end == -1 else end + 2
            continue
        if code[i] != "@":
            break
        start = i
        i += 1
        name_start = i
        while i < n and (code[i].isalnum() or code[i] in "._$"):
            i += 1
        full_name = code[name_start:i]
        simple_name = full_name.rsplit(".", 1)[-1]
        j = i
        while j < n and code[j].isspace():
            j += 1
        if j < n and code[j] == "(":
            i = _skip_balanced(code, j, "(", ")")
        annotations.append((simple_name, code[start:i]))
    return annotations, i


def _count_top_level_test_annotations(code: str) -> int:
    """Count @Test / @ParameterizedTest occurrences at brace depth 0 outside comments/strings."""
    depth = 0
    top_chunks: list[str] = []
    for kind, text in _iter_code_segments(code):
        if kind != "code":
            continue
        buf: list[str] = []
        for ch in text:
            if ch == "{":
                if depth == 0:
                    buf.append(" ")
                depth += 1
            elif ch == "}":
                depth = max(0, depth - 1)
            elif depth == 0:
                buf.append(ch)
        top_chunks.append("".join(buf))
    return len(_TEST_ANNOTATION.findall("".join(top_chunks)))


def _extract_method_name(body: str) -> str | None:
    _, pos = _parse_leading_annotations(body)
    tail = strip_comments(body[pos:])
    brace = tail.find("{")
    header = tail[:brace] if brace != -1 else tail
    match = _METHOD_NAME_RE.search(header)
    if match:
        return match.group(1)
    return None


def _dedent_block(text: str) -> str:
    lines = text.splitlines()
    while lines and not lines[0].strip():
        lines.pop(0)
    while lines and not lines[-1].strip():
        lines.pop()
    indents = [len(line) - len(line.lstrip(" \t")) for line in lines if line.strip()]
    min_indent = min(indents) if indents else 0
    return "\n".join(line[min_indent:] if len(line) >= min_indent else line for line in lines)


def extract_block_from_java(source: str) -> TestBlock | None:
    """Parse a single standalone Java test block (raw code or wrapped class)."""
    imports: list[str] = []
    seen_imports: set[str] = set()
    kept_lines: list[str] = []

    for line in source.replace("\r\n", "\n").splitlines():
        if _PACKAGE_LINE.match(line):
            continue
        if _IMPORT_LINE.match(line):
            norm_imp = re.sub(r"\s+", " ", line.strip())
            if norm_imp not in seen_imports:
                seen_imports.add(norm_imp)
                imports.append(norm_imp)
            continue
        kept_lines.append(line)

    body = _unwrap_outer_class("\n".join(kept_lines).strip())
    body = _dedent_block(body)
    if not body:
        return None
    if _count_top_level_test_annotations(body) != 1:
        return None
    method_name = _extract_method_name(body)
    if not method_name:
        return None
    return TestBlock(method_name=method_name, body=body, imports=tuple(imports))


def extract_blocks(text: str) -> list[TestBlock]:
    """Extract all single-method JUnit 5 ```java blocks from an LLM response."""
    blocks: list[TestBlock] = []
    for match in _JAVA_FENCE.finditer(text):
        block = extract_block_from_java(match.group(1))
        if block is not None:
            blocks.append(block)
    return blocks


def block_signature(block: TestBlock) -> str:
    """Normalized deduplication key: parameter sources/signature + method body (ignoring name/comments/whitespace)."""
    annotations, pos = _parse_leading_annotations(block.body)
    provider_anns = [normalize_body(raw) for name, raw in annotations if name in _PROVIDER_ANN_NAMES]
    tail = block.body[pos:]
    # Find parameter list (...) and method body {...}
    open_paren = tail.find("(")
    if open_paren != -1:
        after_paren = _skip_balanced(tail, open_paren, "(", ")")
        params = normalize_body(tail[open_paren:after_paren])
        brace = tail.find("{", after_paren)
    else:
        params = ""
        brace = tail.find("{")
    impl = normalize_body(tail[brace:]) if brace != -1 else normalize_body(tail)
    parts = [*provider_anns, params, impl]
    return " ".join(p for p in parts if p)


def class_name(prefix: str, index: int) -> str:
    if not re.fullmatch(r"[A-Za-z_][A-Za-z0-9_]*", prefix) or index < 1:
        raise ValueError("Expected a valid Java identifier prefix and positive index")
    return f"{prefix}_{index:03d}Test"


def wrap_block(block: TestBlock, cls_name: str) -> str:
    """Wrap a single TestBlock into a complete `package shop;` JUnit 5 test class."""
    if not re.fullmatch(r"[A-Za-z_][A-Za-z0-9_]*", cls_name):
        raise ValueError(f"Invalid Java class name: {cls_name}")
    seen = set(STANDARD_IMPORTS)
    imports = list(STANDARD_IMPORTS)
    for imp in block.imports:
        norm = re.sub(r"\s+", " ", imp.strip())
        if not norm.startswith("import "):
            norm = f"import {norm}"
        if not norm.endswith(";"):
            norm += ";"
        if norm not in seen:
            seen.add(norm)
            imports.append(norm)

    indented = "\n".join(
        f"    {line}" if line.strip() else ""
        for line in block.body.splitlines()
    )
    return (
        "package shop;\n\n"
        + "\n".join(imports)
        + f"\n\npublic class {cls_name} {{\n\n"
        + indented
        + "\n}\n"
    )
