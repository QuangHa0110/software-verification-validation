"""Greedy selection using combined coverage measurements or per-test mutant kills."""

from dataclasses import dataclass, field
from heapq import heappop, heappush
from math import inf

from .pit import mutant_key


@dataclass
class Measurement:
    coverage: dict
    uncovered: dict
    mutation_score: float | None = None
    killed: set = field(default_factory=set)
    live: list = field(default_factory=list)
    kills_by_class: dict = field(default_factory=dict)

    def metrics(self):
        result = {"statement_pct": self.coverage["statement"]["pct"],
                  "branch_pct": self.coverage["branch"]["pct"]}
        if self.mutation_score is not None:
            result["mutation_score"] = self.mutation_score
        return result


def gain(before: Measurement, after: Measurement) -> int:
    if before.mutation_score is not None:
        if not before.killed <= after.killed or after.mutation_score < before.mutation_score:
            return 0
        targets = {mutant_key(row["source"], row) for row in before.live}
        return len(targets & after.killed)
    deltas = [after.coverage[k]["covered"] - before.coverage[k]["covered"]
              for k in ("statement", "branch")]
    return sum(deltas) if min(deltas) >= 0 else 0


def select_greedy(candidates, current: Measurement, evaluate):
    """evaluate(selected + [candidate]) measures that list on top of the inherited suite.

    For deterministic independent tests, the previous gain is an upper bound as
    the suite grows. Refresh the heap leader until it has been measured against
    the current suite. This avoids remeasuring candidates that cannot win.
    At most k(k+1)/2 evaluations, with generation order breaking ties.
    """
    candidates = list(candidates)
    # Entries sort by descending gain, then original index. -1 marks unmeasured tests.
    pending = [(-inf, index, -1, None) for index in range(len(candidates))]
    selected = []
    while pending:
        _, index, measured_at, measurement = heappop(pending)
        candidate = candidates[index]
        if measured_at == len(selected):
            selected.append(candidate)
            current = measurement
            continue
        measurement = evaluate([*selected, candidate])
        contribution = gain(current, measurement)
        if contribution > 0:
            heappush(pending, (-contribution, index, len(selected), measurement))
    return selected, current


def kills_by_test_class(pit, custom, killed):
    """Index only scored kills. Both JUnit Platform IDs and Surefire names start with shop.Class."""
    matrix = {}
    for source, records in (("pit", pit), ("custom", custom)):
        for row in records:
            key = mutant_key(source, row)
            if key in killed:
                for name in row["killing_tests"]:
                    package, cls, _ = name.split(".", 2)
                    matrix.setdefault(f"{package}.{cls}", set()).add(key)
    return matrix


def select_by_kills(candidates, baseline_kills, kills_by_class):
    """Choose the largest marginal kill set. max keeps the earliest item on ties."""
    covered = set(baseline_kills)
    remaining = [(item, kills_by_class.get(f"shop.{item.class_name}", set())) for item in candidates]
    selected = []
    while remaining:
        best = max(range(len(remaining)), key=lambda i: len(remaining[i][1] - covered))
        candidate, kills = remaining.pop(best)
        if kills <= covered:
            break
        selected.append(candidate)
        covered.update(kills)
    return selected
