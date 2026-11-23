"""D-012 source mutants for the flat shop package.

This is a lexical scanner for one top-level class/enum per file. It skips Java
comments, quoted literals and text blocks, and matches method/constructor
bodies by balanced delimiters.

Known limitations:
- Nested types (inner classes/records/enums), static/instance initializer
  blocks, Unicode escape preprocessing (\\uXXXX), and complex annotated
  declarations (e.g. type-use annotations on parameters or annotations with
  boolean arguments) are outside its scope.
- statement_deletion only targets complete, single-line simple-name expression
  statements (x = ..., x <op>= ..., x++, x--, ++x, --x); multi-line
  assignments, array-element assignments, and statements inside a for-loop
  header are not deleted.
- Field vs. local disambiguation collects class-level field declarations before
  scanning methods (including fields declared later) and tracks parameter and
  block-local declarations; for-loop header declarations are not tracked as
  shadowing fields.
"""

from concurrent.futures import ThreadPoolExecutor
from dataclasses import dataclass
import os
import re
import signal
import subprocess
import xml.etree.ElementTree as ET

from .config import ROOT
from .maven import run_maven
from .surefire import parse_surefire
from .workdir import workdir


SWAPS = {"&&": "||", "||": "&&", "true": "false", "false": "true"}
OPERATORS = ("conditional", "boolean_constant", "statement_deletion")
ASSIGN = {"=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>="}
IDENT = r"[^\W\d][\w$]*|\$[\w$]*"
LEX = re.compile(
    r'(?P<skip>\s+|//[^\r\n]*|/\*[\s\S]*?\*/)'
    r'|"""(?:\\[\s\S]|(?!""")[\s\S])*"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\''
    rf'|{IDENT}|>>>=|>>=|<<=|&&|\|\||\+\+|--|==|!=|<=|>=|->|[-+*/%&|^]=|.'
)


@dataclass(frozen=True)
class Token:
    value: str
    offset: int
    line: int


def tokenize(source):
    line = 1
    for match in LEX.finditer(source):
        value = match.group()
        if match.lastgroup != "skip":
            yield Token(value, match.start(), line)
        line += value.count("\n")


def _pairs(tokens):
    stack, pairs = [], {}
    for i, token in enumerate(tokens):
        if token.value in ("(", "{", "["):
            stack.append(i)
        elif token.value in (")", "}", "]"):
            start = stack.pop()
            pairs[start] = i
            pairs[i] = start
    return pairs


def _declarations(values):
    """Names in a field, local declaration or comma-separated parameter list."""
    names = set()
    # Before an initializer the declarator follows its type; after a top-level
    # comma a field/local declarator can omit that type.
    part = []
    depth = 0
    initialized = False
    for value in [*values, ","]:
        if depth == 0 and value == ",":
            while part and part[-1] in ("[", "]"):
                part.pop()
            varargs = len(part) >= 5 and part[-4:-1] == [".", ".", "."]
            typed = len(part) >= 2 and (re.fullmatch(IDENT, part[-2]) or part[-2] in (">", "]") or varargs)
            if part and re.fullmatch(IDENT, part[-1]) and (typed or (len(part) == 1 and names)):
                names.add(part[-1])
            part = []
            initialized = False
        elif depth == 0 and value == "=":
            initialized = True
        else:
            if value in ("(", "[", "{") or (value == "<" and not initialized):
                depth += 1
            elif value in (")", "]", "}") or (value == ">" and not initialized and depth > 0):
                depth -= 1
            if not initialized:
                part.append(value)
    return names


def _members(tokens, pairs, class_name):
    fields, methods = set(), []
    class_open = next(i for i, t in enumerate(tokens) if t.value == "{")
    class_end = pairs[class_open]
    start = class_open + 1
    i = start
    while i < class_end:
        value = tokens[i].value
        if value == "{":
            header = [t.value for t in tokens[start:i]]
            if ")" in header and "=" not in header and not {"class", "enum", "interface", "record"} & set(header):
                end = i - 1 - header[::-1].index(")")
                opening = pairs[end]
                name = tokens[opening - 1].value
                params = _declarations([t.value for t in tokens[opening + 1:end]])
                methods.append((i, pairs[i], "<init>" if name == class_name else name, params))
            i = pairs[i]
            if "=" not in header:
                start = i + 1
        elif value == ";":
            fields.update(_declarations([t.value for t in tokens[start:i]]))
            start = i + 1
        i += 1
    return fields, methods


def _target(values):
    if len(values) == 2 and values[0] in ("++", "--"):
        return values[1]
    if len(values) == 2 and values[1] in ("++", "--"):
        return values[0]
    if len(values) >= 3 and values[1] in ASSIGN:
        return values[0]
    return None


def _statements(tokens, pairs, body, end, params, fields):
    start = body + 1
    scopes = [params.copy()]
    i = start
    empty = ""
    while i < end:
        value = tokens[i].value
        if value == "(":
            control = tokens[i - 1].value in ("if", "for", "while", "synchronized")
            i = pairs[i]  # Semicolons and increments in a for header are not statements.
            if control:
                start, empty = i + 1, ";"
        elif value in ("{", "}", "else", "do"):
            if value == "{":
                if fields and start < i:
                    prefix = [t.value for t in tokens[start:i]]
                    if prefix[0] not in ("return", "throw", "break", "continue"):
                        scopes[-1].update(_declarations(prefix) & fields)
                scopes.append(set())
            elif value == "}":
                scopes.pop()
            start, empty = i + 1, ";" if value in ("else", "do") else ""
        elif value == ":" and start < i and (
            tokens[start].value in ("case", "default")
            or (i == start + 1 and re.fullmatch(IDENT, tokens[start].value))
        ):
            start, empty = i + 1, ";"
        elif value == "->" and start < i and tokens[start].value in ("case", "default"):
            start, empty = i + 1, "{}"
        elif value == ";":
            values = [t.value for t in tokens[start:i]]
            target = _target(values)
            if target and re.fullmatch(IDENT, target):
                if tokens[start].line == tokens[i].line and (
                    target not in fields or any(target in scope for scope in scopes)
                ):
                    yield start, i, empty
            elif fields and values and values[0] not in ("return", "throw", "break", "continue"):
                scopes[-1].update(_declarations(values) & fields)
            start, empty = i + 1, ""
        i += 1


@dataclass(frozen=True)
class Mutant:
    file: str
    class_name: str
    method: str
    line: int
    column: int
    offset: int
    operator: str
    original: str
    mutated: str

    def record(self):
        site = f"{self.file}:{self.line}:{self.column}"
        edit = f"{self.original} -> {self.mutated}" if self.operator != "statement_deletion" else f"delete {self.original}"
        return {"id": f"{site}:{self.operator}", "file": self.file, "class": self.class_name,
                "method": self.method, "line": self.line, "operator": self.operator,
                "mutator": self.operator, "original": self.original, "mutated": self.mutated,
                "description": f"{edit} (column {self.column})"}

    def apply(self, source):
        end = self.offset + len(self.original)
        if source[self.offset:end] != self.original:
            raise ValueError(f"Source changed at {self.record()['id']}")
        return source[:self.offset] + self.mutated + source[end:]


def _read(path):
    with path.open(encoding="utf-8", newline="") as stream:
        return stream.read()


def enumerate_mutants(project):
    mutants = []
    for path in sorted((project / "src/main/java/shop").glob("*.java")):
        source = _read(path)
        tokens = list(tokenize(source))
        if not any(t.value == "{" for t in tokens):
            continue
        pairs = _pairs(tokens)
        fields, methods = _members(tokens, pairs, path.stem)
        enclosing = {}
        edits = []
        for body, end, method, params in methods:
            enclosing.update(dict.fromkeys(range(body + 1, end), method))
            for start, stop, replacement in _statements(tokens, pairs, body, end, params, fields):
                edits.append((start, stop, "statement_deletion", replacement))
        for i, t in enumerate(tokens):
            if t.value in SWAPS:
                operator = "conditional" if t.value in ("&&", "||") else "boolean_constant"
                edits.append((i, i, operator, SWAPS[t.value]))
        for start, stop, operator, replacement in sorted(edits):
            token, last = tokens[start], tokens[stop]
            original = source[token.offset:last.offset + len(last.value)]
            column = token.offset - source.rfind("\n", 0, token.offset)
            mutants.append(Mutant(path.relative_to(project).as_posix(), f"shop.{path.stem}",
                                  enclosing.get(start, ""), token.line, column, token.offset,
                                  operator, original, replacement))
    return mutants


def _run_tests(project, timeout):
    """Time only test execution. Stop Maven and its forked JVM on expiry."""
    command = ["mvn.cmd" if os.name == "nt" else "mvn", "-B", "surefire:test"]
    with subprocess.Popen(command, cwd=project, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                          text=True, encoding="utf-8", errors="replace",
                          start_new_session=os.name != "nt") as process:
        try:
            output, _ = process.communicate(timeout=timeout)
        except subprocess.TimeoutExpired:
            if os.name == "nt":
                subprocess.run(["taskkill", "/T", "/F", "/PID", str(process.pid)],
                               capture_output=True, check=False)
            else:
                try:
                    os.killpg(process.pid, signal.SIGKILL)
                except ProcessLookupError:
                    pass
            process.communicate()
            return "TIMED_OUT"
    report = parse_surefire(project / "target/surefire-reports")
    if report["failures"] or report["errors"]:
        return "KILLED"
    if process.returncode or report["tests"] <= report["skipped"]:
        raise ValueError(f"Missing successful mutant test execution\n{output}")
    return "SURVIVED"


def run_custom(baseline, suite, baseline_seconds, settings, root=ROOT):
    xml = baseline / "target/site/jacoco/jacoco.xml"
    uncovered = {(src.attrib["name"], int(line.attrib["nr"]))
                 for src in ET.parse(xml).findall(".//package[@name='shop']/sourcefile")
                 for line in src.findall("line") if int(line.attrib["ci"]) == 0}
    timeout = settings["timeout_factor"] * baseline_seconds + settings["custom_timeout_constant_s"]

    def evaluate(mutant):
        with workdir(suite, root=root) as project:
            path = project / mutant.file
            path.write_text(mutant.apply(_read(path)), encoding="utf-8", newline="")
            build = run_maven(project, ["-Djacoco.skip=true", "test-compile"])
            if build.returncode:
                if "COMPILATION ERROR" not in build.stdout or "maven-compiler-plugin" not in build.stdout:
                    raise ValueError(f"Mutant build failed\n{build.stdout}\n{build.stderr}")
                return {**mutant.record(), "status": "COMPILE_ERROR", "diagnostic": build.stdout}
            if (path.name, mutant.line) in uncovered:
                status = "NO_COVERAGE"
            else:
                status = _run_tests(project, timeout)
            return {**mutant.record(), "status": status}

    with ThreadPoolExecutor(max_workers=settings["custom_workers"]) as pool:
        return list(pool.map(evaluate, enumerate_mutants(baseline)))
