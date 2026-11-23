from pathlib import Path
import xml.etree.ElementTree as ET


def parse_surefire(directory: Path) -> dict:
    """Read JUnit suite totals and qualified failed/error testcase names."""
    totals = dict.fromkeys(("tests", "failures", "errors", "skipped"), 0)
    failed_tests = []
    for path in sorted(directory.glob("TEST-*.xml")):
        suite = ET.parse(path).getroot()
        for key in totals:
            totals[key] += int(suite.get(key, "0"))
        for test in suite.findall("testcase"):
            if test.find("failure") is not None or test.find("error") is not None:
                failed_tests.append(f"{test.attrib['classname']}.{test.attrib['name']}")
    return {**totals, "failed_tests": failed_tests}
