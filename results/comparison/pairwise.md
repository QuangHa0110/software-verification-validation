# Pairwise Statistical Comparisons

Two-sided Mann-Whitney U test and Vargha-Delaney A12 effect size (`A12 = P(A > B) + 0.5 * P(A == B)`; magnitude thresholds: 0.56 small, 0.64 medium, 0.71 large). Methods with `n < 2` (including single-run `p1-manual`) are excluded from inferential tests.

| Pair (A vs B) | RQ | Metric | n_A | n_B | Median A | Median B | Mann-Whitney U | p-value | A12 | Magnitude |
|---|---|---|---:|---:|---:|---:|---:|---:|---:|---|
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `mutation_score` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `tests_valid` | 10 | 10 | 28.0000 | 28.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `tests_generated` | 10 | 10 | 28.0000 | 28.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `execution_s` | 10 | 10 | 91.6965 | 98.2700 | 35.0000 | 0.2730 | 0.3500 | medium |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `generation_s` | 10 | 10 | 224.2968 | 219.5244 | 64.0000 | 0.3075 | 0.6400 | medium |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `input_tokens` | 10 | 10 | 40086.0000 | 40086.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `output_tokens` | 10 | 10 | 9766.5000 | 9766.5000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p3-direct-llm` | RQ2, RQ4 | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3-direct-llm` vs `p2-random` | RQ3 | `statement_pct` | 10 | 10 | 100.0000 | 91.7021 | 100.0000 | 0.0001 | 1.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `branch_pct` | 10 | 10 | 100.0000 | 77.0115 | 100.0000 | 0.0001 | 1.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3-direct-llm` vs `p2-random` | RQ3 | `mutation_score` | 10 | 10 | 100.0000 | 62.5000 | 100.0000 | 0.0001 | 1.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `tests_valid` | 10 | 10 | 28.0000 | 100.0000 | 0.0000 | 0.0001 | 0.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `tests_generated` | 10 | 10 | 28.0000 | 100.0000 | 0.0000 | 0.0001 | 0.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `execution_s` | 10 | 10 | 98.2700 | 90.3139 | 75.0000 | 0.0640 | 0.7500 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `generation_s` | 10 | 10 | 219.5244 | 0.1507 | 100.0000 | 0.0002 | 1.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `input_tokens` | 10 | 10 | 40086.0000 | 0.0000 | 100.0000 | 0.0001 | 1.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `output_tokens` | 10 | 10 | 9766.5000 | 0.0000 | 100.0000 | 0.0001 | 1.0000 | large |
| `p3-direct-llm` vs `p2-random` | RQ3 | `cost_usd` | 0 | 10 | — | 0.0000 | — | — | — | — |
| `p3-direct-llm` vs `p2-random` | RQ3 | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3-direct-llm` vs `p2-random` | RQ3 | `oracle_rate` | 10 | 0 | 1.0000 | — | — | — | — | — |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `statement_pct` | 10 | 10 | 100.0000 | 91.7021 | 100.0000 | 0.0001 | 1.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `branch_pct` | 10 | 10 | 100.0000 | 77.0115 | 100.0000 | 0.0001 | 1.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `mutation_score` | 10 | 10 | 100.0000 | 62.5000 | 100.0000 | 0.0001 | 1.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `tests_valid` | 10 | 10 | 28.0000 | 100.0000 | 0.0000 | 0.0001 | 0.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `tests_generated` | 10 | 10 | 28.0000 | 100.0000 | 0.0000 | 0.0001 | 0.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `execution_s` | 10 | 10 | 91.6965 | 90.3139 | 66.0000 | 0.2413 | 0.6600 | medium |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `generation_s` | 10 | 10 | 224.2968 | 0.1507 | 100.0000 | 0.0002 | 1.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `input_tokens` | 10 | 10 | 40086.0000 | 0.0000 | 100.0000 | 0.0001 | 1.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `output_tokens` | 10 | 10 | 9766.5000 | 0.0000 | 100.0000 | 0.0001 | 1.0000 | large |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `cost_usd` | 0 | 10 | — | 0.0000 | — | — | — | — |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4-coverage-guided` vs `p2-random` | RQ3 | `oracle_rate` | 10 | 0 | 1.0000 | — | — | — | — | — |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `mutation_score` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `tests_valid` | 10 | 10 | 28.0000 | 28.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `tests_generated` | 10 | 10 | 28.0000 | 28.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `execution_s` | 10 | 10 | 90.8997 | 91.6965 | 44.0000 | 0.6776 | 0.4400 | small |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `generation_s` | 10 | 10 | 315.4323 | 224.2968 | 100.0000 | 0.0002 | 1.0000 | large |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `input_tokens` | 10 | 10 | 40086.0000 | 40086.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `output_tokens` | 10 | 10 | 9766.5000 | 9766.5000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4b-mutation-feedback` vs `p4-coverage-guided` | P4b vs P4 | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `mutation_score` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `tests_valid` | 10 | 10 | 31.0000 | 31.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `tests_generated` | 10 | 10 | 31.0000 | 31.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `execution_s` | 10 | 10 | 92.3127 | 92.8762 | 46.0000 | 0.7913 | 0.4600 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `generation_s` | 10 | 10 | 237.5025 | 232.2803 | 69.0000 | 0.1620 | 0.6900 | medium |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `input_tokens` | 10 | 10 | 36658.0000 | 36658.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `output_tokens` | 10 | 10 | 10615.5000 | 10615.5000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4r-coverage-guided` vs `p3r-spec-only` | P4r vs P3r | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `mutation_score` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `tests_valid` | 10 | 10 | 31.0000 | 31.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `tests_generated` | 10 | 10 | 31.0000 | 31.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `execution_s` | 10 | 10 | 92.4562 | 92.3127 | 59.0000 | 0.5205 | 0.5900 | small |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `generation_s` | 10 | 10 | 332.2954 | 237.5025 | 100.0000 | 0.0002 | 1.0000 | large |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `input_tokens` | 10 | 10 | 36658.0000 | 36658.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `output_tokens` | 10 | 10 | 10615.5000 | 10615.5000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4br-mutation-feedback` vs `p4r-coverage-guided` | P4br vs P4r | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `mutation_score` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `tests_valid` | 10 | 10 | 31.0000 | 28.0000 | 84.5000 | 0.0093 | 0.8450 | large |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `tests_generated` | 10 | 10 | 31.0000 | 28.0000 | 84.5000 | 0.0093 | 0.8450 | large |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `execution_s` | 10 | 10 | 92.8762 | 98.2700 | 37.0000 | 0.3447 | 0.3700 | small |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `generation_s` | 10 | 10 | 232.2803 | 219.5244 | 79.0000 | 0.0312 | 0.7900 | large |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `input_tokens` | 10 | 10 | 36658.0000 | 40086.0000 | 0.0000 | 0.0002 | 0.0000 | large |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `output_tokens` | 10 | 10 | 10615.5000 | 9766.5000 | 93.0000 | 0.0013 | 0.9300 | large |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3r-spec-only` vs `p3-direct-llm` | P3r vs P3 | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 55.0000 | 0.3681 | 0.5500 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 55.5000 | 0.5428 | 0.5550 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `mutation_score` | 10 | 10 | 96.2500 | 96.2500 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `tests_valid` | 10 | 10 | 53.0000 | 53.0000 | 51.0000 | 0.9698 | 0.5100 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `tests_generated` | 10 | 10 | 53.0000 | 53.0000 | 51.0000 | 0.9698 | 0.5100 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `execution_s` | 10 | 10 | 91.5186 | 90.4126 | 57.0000 | 0.6232 | 0.5700 | small |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `generation_s` | 10 | 10 | 213.7384 | 194.7079 | 67.0000 | 0.2123 | 0.6700 | medium |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `input_tokens` | 10 | 10 | 40758.5000 | 40742.5000 | 64.0000 | 0.3060 | 0.6400 | medium |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `output_tokens` | 10 | 10 | 11710.0000 | 11377.5000 | 59.0000 | 0.5193 | 0.5900 | small |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4w-coverage-guided` vs `p3w-weak-llm` | P4w vs P3w | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.5000 | 1.0000 | 0.5050 | negligible |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `mutation_score` | 10 | 10 | 98.3333 | 96.2500 | 100.0000 | 0.0001 | 1.0000 | large |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `tests_valid` | 10 | 10 | 74.5000 | 53.0000 | 73.0000 | 0.0890 | 0.7300 | large |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `tests_generated` | 10 | 10 | 75.0000 | 53.0000 | 72.5000 | 0.0962 | 0.7250 | large |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `execution_s` | 10 | 10 | 92.6309 | 91.5186 | 61.0000 | 0.4274 | 0.6100 | small |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `generation_s` | 10 | 10 | 978.9383 | 213.7384 | 92.0000 | 0.0017 | 0.9200 | large |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `input_tokens` | 10 | 10 | 151247.0000 | 40758.5000 | 92.0000 | 0.0017 | 0.9200 | large |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `output_tokens` | 10 | 10 | 14877.5000 | 11710.0000 | 93.0000 | 0.0013 | 0.9300 | large |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p4bw-mutation-feedback` vs `p4w-coverage-guided` | P4bw vs P4w | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 41.5000 | 0.3870 | 0.4150 | small |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `statement_pct` | 10 | 10 | 100.0000 | 100.0000 | 45.0000 | 0.3681 | 0.4500 | negligible |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `branch_pct` | 10 | 10 | 100.0000 | 100.0000 | 40.0000 | 0.1681 | 0.4000 | small |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `function_pct` | 10 | 10 | 100.0000 | 100.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `mutation_score` | 10 | 10 | 96.2500 | 100.0000 | 0.0000 | 0.0001 | 0.0000 | large |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `tests_valid` | 10 | 10 | 53.0000 | 28.0000 | 100.0000 | 0.0002 | 1.0000 | large |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `tests_generated` | 10 | 10 | 53.0000 | 28.0000 | 100.0000 | 0.0002 | 1.0000 | large |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `execution_s` | 10 | 10 | 90.4126 | 98.2700 | 34.0000 | 0.2413 | 0.3400 | medium |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `generation_s` | 10 | 10 | 194.7079 | 219.5244 | 32.0000 | 0.1859 | 0.3200 | medium |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `input_tokens` | 10 | 10 | 40742.5000 | 40086.0000 | 70.0000 | 0.1397 | 0.7000 | medium |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `output_tokens` | 10 | 10 | 11377.5000 | 9766.5000 | 91.0000 | 0.0022 | 0.9100 | large |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `cost_usd` | 0 | 0 | — | — | — | — | — | — |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `compile_rate` | 10 | 10 | 1.0000 | 1.0000 | 50.0000 | 1.0000 | 0.5000 | negligible |
| `p3w-weak-llm` vs `p3-direct-llm` | P3w vs P3 | `oracle_rate` | 10 | 10 | 1.0000 | 1.0000 | 45.0000 | 0.3681 | 0.4500 | negligible |
