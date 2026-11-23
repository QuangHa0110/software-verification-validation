"""Statistical utilities for experiment analysis: descriptive stats, Mann-Whitney U, Vargha-Delaney A12, Spearman."""

from __future__ import annotations

from collections.abc import Iterable, Sequence
from dataclasses import asdict, dataclass
import math
from typing import Literal

import numpy as np
from scipy import stats as scipy_stats

EffectMagnitude = Literal["negligible", "small", "medium", "large"]

# Vargha & Delaney (2000) / Arcuri & Briand (2011) thresholds.
A12_SMALL = 0.56
A12_MEDIUM = 0.64
A12_LARGE = 0.71


@dataclass(frozen=True)
class DescriptiveStats:
    n: int
    mean: float | None
    std: float | None
    median: float | None
    iqr: float | None
    min: float | None
    max: float | None

    def to_dict(self) -> dict[str, int | float | None]:
        return asdict(self)


@dataclass(frozen=True)
class PairwiseResult:
    n_a: int
    n_b: int
    mean_a: float | None
    mean_b: float | None
    median_a: float | None
    median_b: float | None
    u_statistic: float | None
    p_value: float | None
    a12: float | None
    effect_size: EffectMagnitude | None

    def to_dict(self) -> dict[str, int | float | str | None]:
        return asdict(self)


@dataclass(frozen=True)
class CorrelationResult:
    n: int
    rho: float | None
    p_value: float | None

    def to_dict(self) -> dict[str, int | float | None]:
        return asdict(self)


def _clean_values(values: Iterable[float | int | None]) -> list[float]:
    cleaned: list[float] = []
    for item in values:
        if item is None or isinstance(item, bool):
            continue
        val = float(item)
        if math.isfinite(val):
            cleaned.append(val)
    return cleaned


def describe(values: Iterable[float | int | None]) -> DescriptiveStats:
    """Compute descriptive statistics over finite non-None numeric values.

    Sample standard deviation (ddof=1) is returned for n >= 2 and None for n < 2.
    """
    cleaned = _clean_values(values)
    n = len(cleaned)
    if n == 0:
        return DescriptiveStats(n=0, mean=None, std=None, median=None, iqr=None, min=None, max=None)
    arr = np.asarray(cleaned, dtype=float)
    q25, q75 = np.percentile(arr, [25, 75])
    return DescriptiveStats(
        n=n,
        mean=float(np.mean(arr)),
        std=float(np.std(arr, ddof=1)) if n >= 2 else None,
        median=float(np.median(arr)),
        iqr=float(q75 - q25),
        min=float(np.min(arr)),
        max=float(np.max(arr)),
    )


def vargha_delaney_a12(
    sample_a: Sequence[float | int | None],
    sample_b: Sequence[float | int | None],
) -> float:
    """Compute Vargha-Delaney A12 = P(A > B) + 0.5 * P(A == B)."""
    a = _clean_values(sample_a)
    b = _clean_values(sample_b)
    if not a or not b:
        raise ValueError("Both samples must contain at least one finite value")
    arr_a = np.asarray(a, dtype=float)[:, None]
    arr_b = np.asarray(b, dtype=float)[None, :]
    greater = np.sum(arr_a > arr_b)
    ties = np.sum(arr_a == arr_b)
    return float((greater + 0.5 * ties) / (len(a) * len(b)))


def a12_magnitude(a12: float) -> EffectMagnitude:
    """Classify A12 magnitude symmetrically using Vargha-Delaney thresholds (0.56 / 0.64 / 0.71)."""
    if not math.isfinite(a12) or a12 < 0.0 or a12 > 1.0:
        raise ValueError(f"a12 must be in [0, 1], got {a12}")
    distance = round(max(a12, 1.0 - a12), 12)
    if distance >= A12_LARGE:
        return "large"
    if distance >= A12_MEDIUM:
        return "medium"
    if distance >= A12_SMALL:
        return "small"
    return "negligible"


def mann_whitney_u(
    sample_a: Sequence[float | int | None],
    sample_b: Sequence[float | int | None],
) -> tuple[float, float]:
    """Run two-sided Mann-Whitney U test via scipy.stats.mannwhitneyu."""
    a = _clean_values(sample_a)
    b = _clean_values(sample_b)
    if not a or not b:
        raise ValueError("Both samples must contain at least one finite value")
    res = scipy_stats.mannwhitneyu(a, b, alternative="two-sided")
    u_stat = float(res.statistic)
    p_val = float(res.pvalue)
    if math.isnan(p_val):
        p_val = 1.0
    return u_stat, p_val


def compare_samples(
    sample_a: Sequence[float | int | None],
    sample_b: Sequence[float | int | None],
    *,
    min_n: int = 2,
) -> PairwiseResult:
    """Compare two samples with two-sided Mann-Whitney U and Vargha-Delaney A12.

    If either sample has fewer than min_n observations, inferential fields are left None.
    """
    a = _clean_values(sample_a)
    b = _clean_values(sample_b)
    desc_a = describe(a)
    desc_b = describe(b)
    if desc_a.n < min_n or desc_b.n < min_n:
        return PairwiseResult(
            n_a=desc_a.n,
            n_b=desc_b.n,
            mean_a=desc_a.mean,
            mean_b=desc_b.mean,
            median_a=desc_a.median,
            median_b=desc_b.median,
            u_statistic=None,
            p_value=None,
            a12=None,
            effect_size=None,
        )
    u_stat, p_val = mann_whitney_u(a, b)
    a12 = vargha_delaney_a12(a, b)
    return PairwiseResult(
        n_a=desc_a.n,
        n_b=desc_b.n,
        mean_a=desc_a.mean,
        mean_b=desc_b.mean,
        median_a=desc_a.median,
        median_b=desc_b.median,
        u_statistic=u_stat,
        p_value=p_val,
        a12=a12,
        effect_size=a12_magnitude(a12),
    )


def spearman_correlation(
    values_x: Sequence[float | int | None],
    values_y: Sequence[float | int | None],
) -> CorrelationResult:
    """Compute Spearman rank correlation over paired finite observations."""
    if len(values_x) != len(values_y):
        raise ValueError("Input sequences must have equal length")
    xs: list[float] = []
    ys: list[float] = []
    for raw_x, raw_y in zip(values_x, values_y):
        if raw_x is None or raw_y is None or isinstance(raw_x, bool) or isinstance(raw_y, bool):
            continue
        fx, fy = float(raw_x), float(raw_y)
        if math.isfinite(fx) and math.isfinite(fy):
            xs.append(fx)
            ys.append(fy)
    n = len(xs)
    if n < 3:
        return CorrelationResult(n=n, rho=None, p_value=None)
    arr_x = np.asarray(xs, dtype=float)
    arr_y = np.asarray(ys, dtype=float)
    if np.all(arr_x == arr_x[0]) or np.all(arr_y == arr_y[0]):
        return CorrelationResult(n=n, rho=None, p_value=None)
    res = scipy_stats.spearmanr(arr_x, arr_y)
    rho = float(res.statistic)
    p_val = float(res.pvalue)
    if not (math.isfinite(rho) and math.isfinite(p_val)):
        return CorrelationResult(n=n, rho=None, p_value=None)
    return CorrelationResult(n=n, rho=rho, p_value=p_val)
