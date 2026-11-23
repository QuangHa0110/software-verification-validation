"""Unit tests for tools.svv.stats."""

import math

import numpy as np
import pytest
from scipy import stats as scipy_stats

from tools.svv.stats import (
    a12_magnitude,
    compare_samples,
    describe,
    mann_whitney_u,
    spearman_correlation,
    vargha_delaney_a12,
)


def test_vargha_delaney_a12_hand_calculated_examples():
    # [1, 2, 3] vs [2, 3, 4]: 9 pairs -> 1 win (3>2), 2 ties (2==2, 3==3) -> (1 + 2*0.5)/9 = 2/9
    assert vargha_delaney_a12([1, 2, 3], [2, 3, 4]) == pytest.approx(2.0 / 9.0)
    assert vargha_delaney_a12([2, 3, 4], [1, 2, 3]) == pytest.approx(7.0 / 9.0)
    assert vargha_delaney_a12([1, 2, 3], [1, 2, 3]) == pytest.approx(0.5)
    assert vargha_delaney_a12([10, 20], [1, 2, 3]) == pytest.approx(1.0)
    assert vargha_delaney_a12([1, 2], [10, 20, 30]) == pytest.approx(0.0)


def test_vargha_delaney_a12_filters_none_and_nan_and_rejects_empty():
    assert vargha_delaney_a12([1, None, 2, math.nan, 3], [2, 3, 4, None]) == pytest.approx(2.0 / 9.0)
    with pytest.raises(ValueError, match="at least one"):
        vargha_delaney_a12([], [1, 2])
    with pytest.raises(ValueError, match="at least one"):
        vargha_delaney_a12([None, math.nan], [1, 2])


@pytest.mark.parametrize(
    "value,expected",
    [
        (0.50, "negligible"),
        (0.559, "negligible"),
        (0.441, "negligible"),
        (0.56, "small"),
        (0.639, "small"),
        (0.44, "small"),
        (0.361, "small"),
        (0.64, "medium"),
        (0.709, "medium"),
        (0.36, "medium"),
        (0.291, "medium"),
        (0.71, "large"),
        (0.95, "large"),
        (0.29, "large"),
        (0.05, "large"),
    ],
)
def test_a12_magnitude_thresholds(value, expected):
    assert a12_magnitude(value) == expected


def test_a12_magnitude_rejects_out_of_range():
    with pytest.raises(ValueError):
        a12_magnitude(-0.01)
    with pytest.raises(ValueError):
        a12_magnitude(1.01)


@pytest.mark.parametrize(
    "sample_a,sample_b",
    [
        ([80.0, 82.5, 85.0, 88.0, 90.0], [70.0, 71.0, 74.0, 78.0, 79.0]),
        ([1, 2, 2, 3, 5], [2, 2, 3, 4, 4]),
        ([100.0, 100.0, 100.0], [100.0, 100.0, 100.0]),
    ],
)
def test_mann_whitney_u_matches_scipy(sample_a, sample_b):
    expected = scipy_stats.mannwhitneyu(sample_a, sample_b, alternative="two-sided")
    u_stat, p_val = mann_whitney_u(sample_a, sample_b)
    assert u_stat == pytest.approx(float(expected.statistic))
    expected_p = 1.0 if math.isnan(float(expected.pvalue)) else float(expected.pvalue)
    assert p_val == pytest.approx(expected_p)


def test_describe_handles_empty_singleton_and_multi():
    empty = describe([None, math.nan])
    assert empty.n == 0
    assert empty.mean is None and empty.std is None and empty.median is None

    single = describe([42.5, None])
    assert single.n == 1
    assert single.mean == pytest.approx(42.5)
    assert single.std is None
    assert single.median == pytest.approx(42.5)
    assert single.iqr == pytest.approx(0.0)
    assert single.min == pytest.approx(42.5)
    assert single.max == pytest.approx(42.5)

    vals = [10.0, 20.0, 30.0, 40.0]
    multi = describe([*vals, None, True])
    assert multi.n == 4
    assert multi.mean == pytest.approx(float(np.mean(vals)))
    assert multi.std == pytest.approx(float(np.std(vals, ddof=1)))
    assert multi.median == pytest.approx(25.0)
    assert multi.min == pytest.approx(10.0)
    assert multi.max == pytest.approx(40.0)


def test_compare_samples_skips_inferential_for_small_n():
    res = compare_samples([95.0], [80.0, 82.0, 85.0])
    assert res.n_a == 1
    assert res.n_b == 3
    assert res.u_statistic is None
    assert res.p_value is None
    assert res.a12 is None
    assert res.effect_size is None

    valid = compare_samples([90.0, 92.0, 95.0], [70.0, 72.0, 75.0])
    assert valid.n_a == 3 and valid.n_b == 3
    assert valid.a12 == pytest.approx(1.0)
    assert valid.effect_size == "large"
    assert valid.p_value is not None


def test_spearman_correlation_matches_scipy_and_handles_edge_cases():
    x = [10, 20, 30, 40, 50]
    y = [55.0, 62.0, 70.0, 81.0, 95.0]
    res = spearman_correlation(x, y)
    expected = scipy_stats.spearmanr(x, y)
    assert res.n == 5
    assert res.rho == pytest.approx(float(expected.statistic))
    assert res.p_value == pytest.approx(float(expected.pvalue))

    # Too few observations
    small = spearman_correlation([1, 2], [3, 4])
    assert small.n == 2 and small.rho is None and small.p_value is None

    # Constant input yields None rather than NaN
    const = spearman_correlation([5, 5, 5, 5], [1, 2, 3, 4])
    assert const.n == 4 and const.rho is None and const.p_value is None
