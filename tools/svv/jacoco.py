from bisect import bisect_right
from collections import defaultdict
from pathlib import Path
import xml.etree.ElementTree as ET


def parse_jacoco(path: Path) -> dict:
    """Read exact-package shop totals and source locations for coverage feedback.

    JaCoCo gives method starts, not end lines. Use the nearest preceding start
    in the same source file (including nested classes); an unknown method is "".
    Class names use Java binary names, e.g. shop.Cart$Item.
    """
    counters = {"LINE": "statement", "BRANCH": "branch", "METHOD": "function"}
    coverage = {name: {"covered": 0, "total": 0} for name in counters.values()}
    lines, branches = [], []
    for package in ET.parse(path).findall(".//package[@name='shop']"):
        # Package LINE counters count shared source lines only once.
        for kind, name in counters.items():
            totals = package.findall(f"counter[@type='{kind}']")
            if not totals:
                totals = package.findall(f"class/counter[@type='{kind}']")
            for counter in totals:
                covered = int(counter.attrib["covered"])
                coverage[name]["covered"] += covered
                coverage[name]["total"] += covered + int(counter.attrib["missed"])

        starts = defaultdict(list)
        for cls in package.findall("class"):
            filename = cls.get("sourcefilename")
            for method in cls.findall("method[@line]"):
                starts[filename].append((int(method.attrib["line"]),
                                         cls.attrib["name"].replace("/", "."),
                                         method.attrib["name"]))
        for source in package.findall("sourcefile"):
            filename = source.attrib["name"]
            methods = sorted(starts[filename], key=lambda entry: entry[0])
            numbers = [entry[0] for entry in methods]
            for line in source.findall("line"):
                nr = int(line.attrib["nr"])
                index = bisect_right(numbers, nr) - 1
                cls, method = methods[index][1:] if index >= 0 else (f"shop.{Path(filename).stem}", "")
                location = {"class": cls, "method": method, "line": nr}
                if int(line.get("mi", "0")) and not int(line.get("ci", "0")):
                    lines.append(location)
                missed = int(line.get("mb", "0"))
                if missed:
                    branches.append({**location, "missed": missed, "covered": int(line.get("cb", "0"))})
    for counter in coverage.values():
        counter["pct"] = 100 * counter["covered"] / counter["total"] if counter["total"] else 0.0
    return {"coverage": coverage, "uncovered": {"lines": lines, "branches": branches}}
