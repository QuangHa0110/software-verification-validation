from types import SimpleNamespace

from tools.svv.pit import mutant_key
from tools.svv.selection import Measurement, gain, select_by_kills, select_greedy


def coverage(lines, branches):
    return Measurement({"statement": {"covered": lines}, "branch": {"covered": branches}}, {})


def test_greedy_remeasures_overlaps_and_breaks_ties_in_response_order():
    cases = {"small": ({"a"}, {"x"}), "largest": ({"a", "b"}, {"x", "y"}),
             "tie": ({"a", "b"}, {"x", "y"}), "line": ({"c"}, set()), "empty": (set(), set())}
    calls = []

    def measure(items):
        calls.append(items)
        lines = set.union(set(), *(cases[i][0] for i in items))
        branches = set.union(set(), *(cases[i][1] for i in items))
        return coverage(len(lines), len(branches))

    kept, final = select_greedy(cases, coverage(0, 0), measure)
    assert kept == ["largest", "line"]
    assert ["largest", "small"] in calls
    assert gain(coverage(0, 0), final) == 5
    assert gain(coverage(2, 2), coverage(1, 4)) == 0


def test_mutation_selection_uses_full_ids_and_rejects_coverage_only():
    rows = [{"source": "pit", "class": "shop.C", "method": "m", "line": 8,
             "mutator": "Math", "index": str(i), "description": "swap"} for i in range(3)]
    keys = [mutant_key("pit", r) for r in rows]

    def measure(items):
        hits = set().union(*[{"already": {0}, "first": {1}, "both": {1, 2}, "coverage": set()}[i]
                             for i in ["already", *items]])
        return Measurement(coverage(len(items), len(items)).coverage, {}, 100 * len(hits) / 3,
                           {keys[i] for i in hits}, [r for i, r in enumerate(rows) if i not in hits])

    kept, final = select_greedy(["coverage", "first", "both"], measure([]), measure)
    assert kept == ["both"]
    assert final.killed == set(keys)
    assert gain(final, measure(["first"])) == 0


def test_matrix_agrees_with_remeasured_greedy_on_overlaps_and_ties():
    rows = [{"source": "custom", "class": "shop.C", "method": "m", "line": 1,
             "mutator": "conditional", "description": "swap", "index": i} for i in range(6)]
    keys = [mutant_key("custom", row) for row in rows]
    candidates = [SimpleNamespace(class_name=name) for name in ("A", "B", "C", "D", "E")]
    matrix = {f"shop.{c.class_name}": {keys[i] for i in hits}
              for c, hits in zip(candidates, ((0, 1, 2), (2, 3), (3, 4), (4, 5), (0,)))}

    def measure(items):
        killed = {keys[0]}.union(*(matrix[f"shop.{item.class_name}"] for item in items))
        live = [row for key, row in zip(keys, rows) if key not in killed]
        return Measurement({}, {}, 100 * len(killed) / len(keys), killed, live)

    baseline = measure([])
    old, final = select_greedy(candidates, baseline, measure)
    selected = select_by_kills(candidates, baseline.killed, matrix)
    assert selected == old == [candidates[0], candidates[2], candidates[3]]
    assert measure(selected).killed == final.killed
    assert baseline.killed == {keys[0]}
