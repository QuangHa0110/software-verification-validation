from tools.svv.pit import mutant_key
from tools.svv.selection import Measurement, gain, select_greedy


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
