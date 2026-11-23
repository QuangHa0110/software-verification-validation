import pytest

from tools.svv.test_blocks import (
    STANDARD_IMPORTS,
    TestBlock,
    block_signature,
    class_name,
    extract_blocks,
    normalize_body,
    strip_comments,
    wrap_block,
)


def test_extract_blocks_parses_standard_and_parameterized_and_imports():
    response = """
Here are two tests:

```java
import java.math.BigDecimal;
import static java.lang.Math.max;

@Test
void addsTwoNumbers() {
    assertEquals(2, max(1, 2));
}
```

And a parameterized test wrapped in a class:

```java
package shop;
import java.time.LocalDate;

public class IgnoredWrapperTest {
    @ParameterizedTest
    @ValueSource(ints = {1000, 1001, 1500})
    @DisplayName("Weight boundaries (g)")
    public void handlesWeights(int weight) {
        assertTrue(weight >= 1000);
    }
}
```

A non-test snippet should be skipped:

```java
int helper = 42;
```

A block with two @Test methods violates the single-test rule and is skipped:

```java
@Test
void first() { assertTrue(true); }

@Test
void second() { assertTrue(true); }
```
"""
    blocks = extract_blocks(response)
    assert len(blocks) == 2

    assert blocks[0].method_name == "addsTwoNumbers"
    assert blocks[0].imports == ("import java.math.BigDecimal;", "import static java.lang.Math.max;")
    assert blocks[0].body.startswith("@Test")

    assert blocks[1].method_name == "handlesWeights"
    assert blocks[1].imports == ("import java.time.LocalDate;",)
    assert "@ParameterizedTest" in blocks[1].body
    assert "@ValueSource" in blocks[1].body
    assert "IgnoredWrapperTest" not in blocks[1].body


def test_extract_blocks_ignores_at_test_in_comments_and_strings():
    response = """
```java
@Test
void literalContainsAnnotation() {
    // @Test inside a comment
    String s = "@Test inside a string";
    assertNotNull(s);
}
```
"""
    blocks = extract_blocks(response)
    assert len(blocks) == 1
    assert blocks[0].method_name == "literalContainsAnnotation"


def test_wrap_block_includes_standard_and_extra_imports_without_duplicates():
    block = TestBlock(
        method_name="checksSubtotal",
        body="@Test\nvoid checksSubtotal() {\n    assertEquals(0L, new Cart().getSubtotal());\n}",
        imports=("import java.util.*;", "import java.math.BigDecimal;"),
    )
    cls = class_name("Direct", 7)
    assert cls == "Direct_007Test"
    wrapped = wrap_block(block, cls)

    assert wrapped.startswith("package shop;\n\n")
    for std in STANDARD_IMPORTS:
        assert wrapped.count(std) == 1
    assert "import java.math.BigDecimal;" in wrapped
    assert "public class Direct_007Test {" in wrapped
    assert "    @Test\n    void checksSubtotal()" in wrapped


def test_class_name_validation():
    assert class_name("P3", 1) == "P3_001Test"
    with pytest.raises(ValueError):
        class_name("123Bad", 1)
    with pytest.raises(ValueError):
        class_name("P3", 0)


def test_strip_comments_and_normalize_body_preserve_strings():
    src = '''
    // line comment
    String url = "https://example.com/*keep*/"; /* block
    comment */
    int   x   =   1 + 2;
    '''
    stripped = strip_comments(src)
    assert "line comment" not in stripped
    assert "block" not in stripped
    assert '"https://example.com/*keep*/"' in stripped

    norm = normalize_body(src)
    assert norm == 'String url = "https://example.com/*keep*/"; int x = 1 + 2;'


def test_block_signature_matches_identical_bodies_and_distinguishes_value_sources():
    a = TestBlock("testOne", "@Test\nvoid testOne() {\n    // comment\n    assertEquals(2, 1 + 1);\n}")
    b = TestBlock("testTwo", "@Test\nvoid testTwo() {\n    assertEquals(2,   1 + 1);\n}")
    assert block_signature(a) == block_signature(b)

    p1 = TestBlock(
        "pOne",
        "@ParameterizedTest\n@ValueSource(ints = {1, 2})\nvoid pOne(int x) { assertTrue(x > 0); }",
    )
    p2 = TestBlock(
        "pTwo",
        "@ParameterizedTest\n@ValueSource(ints = {3, 4})\nvoid pTwo(int x) { assertTrue(x > 0); }",
    )
    assert block_signature(p1) != block_signature(p2)


def test_extract_blocks_unwraps_class_with_leading_javadoc():
    response = """
```java
/** Auto-generated test class */
public class SampleTest {
    @Test
    void checksZero() {
        assertEquals(0, 0);
    }
}
```
"""
    blocks = extract_blocks(response)
    assert len(blocks) == 1
    assert blocks[0].method_name == "checksZero"
    assert "SampleTest" not in blocks[0].body
