# Experiment Summary Statistics

Descriptive statistics computed strictly from `results/comparison/raw/**/run-*.json`. Missing data are reported as `n = 0`. Mutation scores are recomputed with the current equivalent-mutant list (see the last section).

| Method | Metric | Unit | n | Mean | Std (ddof=1) | Median | IQR | Min | Max |
|---|---|---|---:|---:|---:|---:|---:|---:|---:|
| `p1-manual` | Statement/Line coverage (`statement_pct`) | % | 1 | 100.0000 | — | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p1-manual` | Branch coverage (`branch_pct`) | % | 1 | 100.0000 | — | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p1-manual` | Function coverage (`function_pct`) | % | 1 | 100.0000 | — | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p1-manual` | Mutation score (`mutation_score`) | % | 1 | 99.1667 | — | 99.1667 | 0.0000 | 99.1667 | 99.1667 |
| `p1-manual` | Valid tests (`tests_valid`) | count | 1 | 181.0000 | — | 181.0000 | 0.0000 | 181.0000 | 181.0000 |
| `p1-manual` | Generated tests (`tests_generated`) | count | 1 | 181.0000 | — | 181.0000 | 0.0000 | 181.0000 | 181.0000 |
| `p1-manual` | Execution time (`execution_s`) | s | 1 | 95.5742 | — | 95.5742 | 0.0000 | 95.5742 | 95.5742 |
| `p1-manual` | Generation time (`generation_s`) | s | 1 | 0.0000 | — | 0.0000 | 0.0000 | 0.0000 | 0.0000 |
| `p1-manual` | Input tokens (`input_tokens`) | tokens | 1 | 0.0000 | — | 0.0000 | 0.0000 | 0.0000 | 0.0000 |
| `p1-manual` | Output tokens (`output_tokens`) | tokens | 1 | 0.0000 | — | 0.0000 | 0.0000 | 0.0000 | 0.0000 |
| `p1-manual` | LLM cost (`cost_usd`) | USD | 1 | 0.0000 | — | 0.0000 | 0.0000 | 0.0000 | 0.0000 |
| `p1-manual` | Compile rate (`compile_rate`) | ratio | 1 | 1.0000 | — | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p1-manual` | Oracle correctness rate (`oracle_rate`) | ratio | 0 | — | — | — | — | — | — |
| `p2-random` | Statement/Line coverage (`statement_pct`) | % | 10 | 91.4894 | 2.1698 | 91.7021 | 1.9149 | 86.8085 | 94.4681 |
| `p2-random` | Branch coverage (`branch_pct`) | % | 10 | 76.7816 | 3.1502 | 77.0115 | 2.7299 | 71.2644 | 81.6092 |
| `p2-random` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p2-random` | Mutation score (`mutation_score`) | % | 10 | 64.3333 | 4.4061 | 62.5000 | 7.0833 | 58.3333 | 70.8333 |
| `p2-random` | Valid tests (`tests_valid`) | count | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p2-random` | Generated tests (`tests_generated`) | count | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p2-random` | Execution time (`execution_s`) | s | 10 | 90.5910 | 1.3926 | 90.3139 | 2.5293 | 88.6185 | 92.4057 |
| `p2-random` | Generation time (`generation_s`) | s | 10 | 0.1524 | 0.0146 | 0.1507 | 0.0162 | 0.1325 | 0.1843 |
| `p2-random` | Input tokens (`input_tokens`) | tokens | 10 | 0.0000 | 0.0000 | 0.0000 | 0.0000 | 0.0000 | 0.0000 |
| `p2-random` | Output tokens (`output_tokens`) | tokens | 10 | 0.0000 | 0.0000 | 0.0000 | 0.0000 | 0.0000 | 0.0000 |
| `p2-random` | LLM cost (`cost_usd`) | USD | 10 | 0.0000 | 0.0000 | 0.0000 | 0.0000 | 0.0000 | 0.0000 |
| `p2-random` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p2-random` | Oracle correctness rate (`oracle_rate`) | ratio | 0 | — | — | — | — | — | — |
| `p3-direct-llm` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3-direct-llm` | Branch coverage (`branch_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3-direct-llm` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3-direct-llm` | Mutation score (`mutation_score`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3-direct-llm` | Valid tests (`tests_valid`) | count | 10 | 27.6000 | 2.6331 | 28.0000 | 1.7500 | 23.0000 | 32.0000 |
| `p3-direct-llm` | Generated tests (`tests_generated`) | count | 10 | 27.6000 | 2.6331 | 28.0000 | 1.7500 | 23.0000 | 32.0000 |
| `p3-direct-llm` | Execution time (`execution_s`) | s | 10 | 100.0748 | 11.6344 | 98.2700 | 12.9924 | 86.6031 | 119.4988 |
| `p3-direct-llm` | Generation time (`generation_s`) | s | 10 | 221.2685 | 14.0906 | 219.5244 | 10.5018 | 200.6697 | 250.3052 |
| `p3-direct-llm` | Input tokens (`input_tokens`) | tokens | 10 | 40086.4000 | 3.7476 | 40086.0000 | 5.0000 | 40080.0000 | 40092.0000 |
| `p3-direct-llm` | Output tokens (`output_tokens`) | tokens | 10 | 9793.2000 | 418.5615 | 9766.5000 | 268.2500 | 9099.0000 | 10707.0000 |
| `p3-direct-llm` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p3-direct-llm` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p3-direct-llm` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4-coverage-guided` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4-coverage-guided` | Branch coverage (`branch_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4-coverage-guided` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4-coverage-guided` | Mutation score (`mutation_score`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4-coverage-guided` | Valid tests (`tests_valid`) | count | 10 | 27.6000 | 2.6331 | 28.0000 | 1.7500 | 23.0000 | 32.0000 |
| `p4-coverage-guided` | Generated tests (`tests_generated`) | count | 10 | 27.6000 | 2.6331 | 28.0000 | 1.7500 | 23.0000 | 32.0000 |
| `p4-coverage-guided` | Execution time (`execution_s`) | s | 10 | 94.9117 | 11.2492 | 91.6965 | 2.8394 | 87.4130 | 126.0276 |
| `p4-coverage-guided` | Generation time (`generation_s`) | s | 10 | 226.3014 | 14.3938 | 224.2968 | 10.4952 | 205.2722 | 255.5197 |
| `p4-coverage-guided` | Input tokens (`input_tokens`) | tokens | 10 | 40086.4000 | 3.7476 | 40086.0000 | 5.0000 | 40080.0000 | 40092.0000 |
| `p4-coverage-guided` | Output tokens (`output_tokens`) | tokens | 10 | 9793.2000 | 418.5615 | 9766.5000 | 268.2500 | 9099.0000 | 10707.0000 |
| `p4-coverage-guided` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p4-coverage-guided` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4-coverage-guided` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4b-mutation-feedback` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4b-mutation-feedback` | Branch coverage (`branch_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4b-mutation-feedback` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4b-mutation-feedback` | Mutation score (`mutation_score`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4b-mutation-feedback` | Valid tests (`tests_valid`) | count | 10 | 27.6000 | 2.6331 | 28.0000 | 1.7500 | 23.0000 | 32.0000 |
| `p4b-mutation-feedback` | Generated tests (`tests_generated`) | count | 10 | 27.6000 | 2.6331 | 28.0000 | 1.7500 | 23.0000 | 32.0000 |
| `p4b-mutation-feedback` | Execution time (`execution_s`) | s | 10 | 93.9766 | 10.5866 | 90.8997 | 2.9003 | 86.8852 | 123.3557 |
| `p4b-mutation-feedback` | Generation time (`generation_s`) | s | 10 | 318.9779 | 18.0422 | 315.4323 | 12.8646 | 295.0535 | 350.3410 |
| `p4b-mutation-feedback` | Input tokens (`input_tokens`) | tokens | 10 | 40086.4000 | 3.7476 | 40086.0000 | 5.0000 | 40080.0000 | 40092.0000 |
| `p4b-mutation-feedback` | Output tokens (`output_tokens`) | tokens | 10 | 9793.2000 | 418.5615 | 9766.5000 | 268.2500 | 9099.0000 | 10707.0000 |
| `p4b-mutation-feedback` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p4b-mutation-feedback` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4b-mutation-feedback` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p3r-spec-only` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3r-spec-only` | Branch coverage (`branch_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3r-spec-only` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3r-spec-only` | Mutation score (`mutation_score`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3r-spec-only` | Valid tests (`tests_valid`) | count | 10 | 31.3000 | 2.7909 | 31.0000 | 3.5000 | 28.0000 | 36.0000 |
| `p3r-spec-only` | Generated tests (`tests_generated`) | count | 10 | 31.3000 | 2.7909 | 31.0000 | 3.5000 | 28.0000 | 36.0000 |
| `p3r-spec-only` | Execution time (`execution_s`) | s | 10 | 94.6424 | 8.3703 | 92.8762 | 3.6468 | 88.0180 | 117.2862 |
| `p3r-spec-only` | Generation time (`generation_s`) | s | 10 | 231.4254 | 7.0268 | 232.2803 | 10.0214 | 219.3183 | 240.8554 |
| `p3r-spec-only` | Input tokens (`input_tokens`) | tokens | 10 | 36657.4000 | 3.0984 | 36658.0000 | 3.5000 | 36653.0000 | 36663.0000 |
| `p3r-spec-only` | Output tokens (`output_tokens`) | tokens | 10 | 10553.7000 | 340.2620 | 10615.5000 | 391.5000 | 9947.0000 | 11055.0000 |
| `p3r-spec-only` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p3r-spec-only` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p3r-spec-only` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4r-coverage-guided` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4r-coverage-guided` | Branch coverage (`branch_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4r-coverage-guided` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4r-coverage-guided` | Mutation score (`mutation_score`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4r-coverage-guided` | Valid tests (`tests_valid`) | count | 10 | 31.3000 | 2.7909 | 31.0000 | 3.5000 | 28.0000 | 36.0000 |
| `p4r-coverage-guided` | Generated tests (`tests_generated`) | count | 10 | 31.3000 | 2.7909 | 31.0000 | 3.5000 | 28.0000 | 36.0000 |
| `p4r-coverage-guided` | Execution time (`execution_s`) | s | 10 | 94.7263 | 8.9643 | 92.3127 | 3.5104 | 88.3583 | 119.3433 |
| `p4r-coverage-guided` | Generation time (`generation_s`) | s | 10 | 236.5079 | 7.2426 | 237.5025 | 10.3411 | 224.0864 | 246.9376 |
| `p4r-coverage-guided` | Input tokens (`input_tokens`) | tokens | 10 | 36657.4000 | 3.0984 | 36658.0000 | 3.5000 | 36653.0000 | 36663.0000 |
| `p4r-coverage-guided` | Output tokens (`output_tokens`) | tokens | 10 | 10553.7000 | 340.2620 | 10615.5000 | 391.5000 | 9947.0000 | 11055.0000 |
| `p4r-coverage-guided` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p4r-coverage-guided` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4r-coverage-guided` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4br-mutation-feedback` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4br-mutation-feedback` | Branch coverage (`branch_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4br-mutation-feedback` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4br-mutation-feedback` | Mutation score (`mutation_score`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4br-mutation-feedback` | Valid tests (`tests_valid`) | count | 10 | 31.3000 | 2.7909 | 31.0000 | 3.5000 | 28.0000 | 36.0000 |
| `p4br-mutation-feedback` | Generated tests (`tests_generated`) | count | 10 | 31.3000 | 2.7909 | 31.0000 | 3.5000 | 28.0000 | 36.0000 |
| `p4br-mutation-feedback` | Execution time (`execution_s`) | s | 10 | 96.0192 | 10.0393 | 92.4562 | 2.9387 | 89.2772 | 123.3972 |
| `p4br-mutation-feedback` | Generation time (`generation_s`) | s | 10 | 330.7770 | 10.6543 | 332.2954 | 12.9672 | 313.0905 | 350.2436 |
| `p4br-mutation-feedback` | Input tokens (`input_tokens`) | tokens | 10 | 36657.4000 | 3.0984 | 36658.0000 | 3.5000 | 36653.0000 | 36663.0000 |
| `p4br-mutation-feedback` | Output tokens (`output_tokens`) | tokens | 10 | 10553.7000 | 340.2620 | 10615.5000 | 391.5000 | 9947.0000 | 11055.0000 |
| `p4br-mutation-feedback` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p4br-mutation-feedback` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4br-mutation-feedback` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p3w-weak-llm` | Statement/Line coverage (`statement_pct`) | % | 10 | 99.9574 | 0.1346 | 100.0000 | 0.0000 | 99.5745 | 100.0000 |
| `p3w-weak-llm` | Branch coverage (`branch_pct`) | % | 10 | 99.6552 | 0.9067 | 100.0000 | 0.0000 | 97.1264 | 100.0000 |
| `p3w-weak-llm` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p3w-weak-llm` | Mutation score (`mutation_score`) | % | 10 | 95.0000 | 2.6644 | 96.2500 | 3.5417 | 90.0000 | 97.5000 |
| `p3w-weak-llm` | Valid tests (`tests_valid`) | count | 10 | 62.3000 | 22.9688 | 53.0000 | 22.5000 | 37.0000 | 105.0000 |
| `p3w-weak-llm` | Generated tests (`tests_generated`) | count | 10 | 62.4000 | 22.9647 | 53.0000 | 22.7500 | 37.0000 | 105.0000 |
| `p3w-weak-llm` | Execution time (`execution_s`) | s | 10 | 93.3210 | 6.4760 | 90.4126 | 6.5134 | 86.5493 | 104.8150 |
| `p3w-weak-llm` | Generation time (`generation_s`) | s | 10 | 197.3821 | 65.0015 | 194.7079 | 72.4688 | 115.9933 | 346.3041 |
| `p3w-weak-llm` | Input tokens (`input_tokens`) | tokens | 10 | 45102.3000 | 28425.5731 | 40742.5000 | 7560.7500 | 17182.0000 | 122438.0000 |
| `p3w-weak-llm` | Output tokens (`output_tokens`) | tokens | 10 | 11563.4000 | 1273.2070 | 11377.5000 | 481.0000 | 9413.0000 | 13918.0000 |
| `p3w-weak-llm` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p3w-weak-llm` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p3w-weak-llm` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 0.9984 | 0.0051 | 1.0000 | 0.0000 | 0.9839 | 1.0000 |
| `p4w-coverage-guided` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4w-coverage-guided` | Branch coverage (`branch_pct`) | % | 10 | 99.9425 | 0.1817 | 100.0000 | 0.0000 | 99.4253 | 100.0000 |
| `p4w-coverage-guided` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4w-coverage-guided` | Mutation score (`mutation_score`) | % | 10 | 95.0000 | 2.6644 | 96.2500 | 3.5417 | 90.0000 | 97.5000 |
| `p4w-coverage-guided` | Valid tests (`tests_valid`) | count | 10 | 63.2000 | 23.5457 | 53.0000 | 28.7500 | 37.0000 | 105.0000 |
| `p4w-coverage-guided` | Generated tests (`tests_generated`) | count | 10 | 63.3000 | 23.5422 | 53.0000 | 29.0000 | 37.0000 | 105.0000 |
| `p4w-coverage-guided` | Execution time (`execution_s`) | s | 10 | 93.9536 | 6.7212 | 91.5186 | 6.9793 | 87.1164 | 105.8132 |
| `p4w-coverage-guided` | Generation time (`generation_s`) | s | 10 | 304.0885 | 304.8079 | 213.7384 | 56.8140 | 120.8239 | 1153.3620 |
| `p4w-coverage-guided` | Input tokens (`input_tokens`) | tokens | 10 | 69811.3000 | 61352.8673 | 40758.5000 | 31040.5000 | 30689.0000 | 224749.0000 |
| `p4w-coverage-guided` | Output tokens (`output_tokens`) | tokens | 10 | 11819.6000 | 1219.8148 | 11710.0000 | 998.5000 | 9413.0000 | 13918.0000 |
| `p4w-coverage-guided` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p4w-coverage-guided` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4w-coverage-guided` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 0.9984 | 0.0050 | 1.0000 | 0.0000 | 0.9841 | 1.0000 |
| `p4bw-mutation-feedback` | Statement/Line coverage (`statement_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4bw-mutation-feedback` | Branch coverage (`branch_pct`) | % | 10 | 99.9425 | 0.1817 | 100.0000 | 0.0000 | 99.4253 | 100.0000 |
| `p4bw-mutation-feedback` | Function coverage (`function_pct`) | % | 10 | 100.0000 | 0.0000 | 100.0000 | 0.0000 | 100.0000 | 100.0000 |
| `p4bw-mutation-feedback` | Mutation score (`mutation_score`) | % | 10 | 98.5000 | 0.3514 | 98.3333 | 0.0000 | 98.3333 | 99.1667 |
| `p4bw-mutation-feedback` | Valid tests (`tests_valid`) | count | 10 | 83.5000 | 32.5141 | 74.5000 | 44.5000 | 44.0000 | 147.0000 |
| `p4bw-mutation-feedback` | Generated tests (`tests_generated`) | count | 10 | 83.8000 | 32.6796 | 75.0000 | 44.7500 | 44.0000 | 147.0000 |
| `p4bw-mutation-feedback` | Execution time (`execution_s`) | s | 10 | 95.4141 | 6.8725 | 92.6309 | 7.3152 | 86.0390 | 107.2808 |
| `p4bw-mutation-feedback` | Generation time (`generation_s`) | s | 10 | 1097.8555 | 349.3503 | 978.9383 | 100.6914 | 864.8961 | 2058.7715 |
| `p4bw-mutation-feedback` | Input tokens (`input_tokens`) | tokens | 10 | 181762.7000 | 62216.2430 | 151247.0000 | 33167.2500 | 142575.0000 | 335894.0000 |
| `p4bw-mutation-feedback` | Output tokens (`output_tokens`) | tokens | 10 | 15820.6000 | 2905.6501 | 14877.5000 | 4878.7500 | 12328.0000 | 20251.0000 |
| `p4bw-mutation-feedback` | LLM cost (`cost_usd`) | USD | 0 | — | — | — | — | — | — |
| `p4bw-mutation-feedback` | Compile rate (`compile_rate`) | ratio | 10 | 1.0000 | 0.0000 | 1.0000 | 0.0000 | 1.0000 | 1.0000 |
| `p4bw-mutation-feedback` | Oracle correctness rate (`oracle_rate`) | ratio | 10 | 0.9970 | 0.0049 | 1.0000 | 0.0066 | 0.9884 | 1.0000 |

## Equivalent mutants excluded

Mutation scores in this report are recomputed from the undetected mutants of each run with the current `results/mutation/equivalent-mutants.csv` (1 rows): `score = killed / (total - equivalent) * 100`. Runs that cannot be recomputed keep the score stored in their JSON.

Equivalent mutants excluded in total: 101 across 101 recomputed runs (of 101 runs with mutation data).

| Method | Run | Total | Equivalent excluded | Stored equivalent | Stored score (%) | Score used (%) | Source |
|---|---|---:|---:|---:|---:|---:|---|
| `p1-manual` | `run-01` | 121 | 1 | 0 | 98.3471 | 99.1667 | backfilled from saved reports |
| `p2-random` | `run-01` | 121 | 1 | 0 | 60.3306 | 60.8333 | backfilled from saved reports |
| `p2-random` | `run-02` | 121 | 1 | 0 | 57.8512 | 58.3333 | backfilled from saved reports |
| `p2-random` | `run-03` | 121 | 1 | 0 | 66.9421 | 67.5000 | backfilled from saved reports |
| `p2-random` | `run-04` | 121 | 1 | 0 | 61.9835 | 62.5000 | backfilled from saved reports |
| `p2-random` | `run-05` | 121 | 1 | 0 | 70.2479 | 70.8333 | backfilled from saved reports |
| `p2-random` | `run-06` | 121 | 1 | 0 | 67.7686 | 68.3333 | backfilled from saved reports |
| `p2-random` | `run-07` | 121 | 1 | 0 | 60.3306 | 60.8333 | backfilled from saved reports |
| `p2-random` | `run-08` | 121 | 1 | 0 | 61.1570 | 61.6667 | backfilled from saved reports |
| `p2-random` | `run-09` | 121 | 1 | 0 | 69.4215 | 70.0000 | backfilled from saved reports |
| `p2-random` | `run-10` | 121 | 1 | 0 | 61.9835 | 62.5000 | backfilled from saved reports |
| `p3-direct-llm` | `run-01` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-02` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-03` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-04` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-05` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-06` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-07` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-08` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-09` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3-direct-llm` | `run-10` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-01` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-02` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-03` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-04` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-05` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-06` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-07` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-08` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-09` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4-coverage-guided` | `run-10` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-01` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-02` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-03` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-04` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-05` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-06` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-07` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-08` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-09` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4b-mutation-feedback` | `run-10` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-01` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-02` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-03` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-04` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-05` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-06` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-07` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-08` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-09` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3r-spec-only` | `run-10` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-01` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-02` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-03` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-04` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-05` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-06` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-07` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-08` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-09` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4r-coverage-guided` | `run-10` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-01` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-02` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-03` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-04` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-05` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-06` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-07` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-08` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-09` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p4br-mutation-feedback` | `run-10` | 121 | 1 | 1 | 100.0000 | 100.0000 | stored alive list |
| `p3w-weak-llm` | `run-01` | 121 | 1 | 1 | 90.0000 | 90.0000 | stored alive list |
| `p3w-weak-llm` | `run-02` | 121 | 1 | 1 | 95.8333 | 95.8333 | stored alive list |
| `p3w-weak-llm` | `run-03` | 121 | 1 | 1 | 96.6667 | 96.6667 | stored alive list |
| `p3w-weak-llm` | `run-04` | 121 | 1 | 1 | 97.5000 | 97.5000 | stored alive list |
| `p3w-weak-llm` | `run-05` | 121 | 1 | 1 | 91.6667 | 91.6667 | stored alive list |
| `p3w-weak-llm` | `run-06` | 121 | 1 | 1 | 95.0000 | 95.0000 | stored alive list |
| `p3w-weak-llm` | `run-07` | 121 | 1 | 1 | 92.5000 | 92.5000 | stored alive list |
| `p3w-weak-llm` | `run-08` | 121 | 1 | 1 | 96.6667 | 96.6667 | stored alive list |
| `p3w-weak-llm` | `run-09` | 121 | 1 | 1 | 97.5000 | 97.5000 | stored alive list |
| `p3w-weak-llm` | `run-10` | 121 | 1 | 1 | 96.6667 | 96.6667 | stored alive list |
| `p4w-coverage-guided` | `run-01` | 121 | 1 | 1 | 90.0000 | 90.0000 | stored alive list |
| `p4w-coverage-guided` | `run-02` | 121 | 1 | 1 | 95.8333 | 95.8333 | stored alive list |
| `p4w-coverage-guided` | `run-03` | 121 | 1 | 1 | 96.6667 | 96.6667 | stored alive list |
| `p4w-coverage-guided` | `run-04` | 121 | 1 | 1 | 97.5000 | 97.5000 | stored alive list |
| `p4w-coverage-guided` | `run-05` | 121 | 1 | 1 | 91.6667 | 91.6667 | stored alive list |
| `p4w-coverage-guided` | `run-06` | 121 | 1 | 1 | 95.0000 | 95.0000 | stored alive list |
| `p4w-coverage-guided` | `run-07` | 121 | 1 | 1 | 92.5000 | 92.5000 | stored alive list |
| `p4w-coverage-guided` | `run-08` | 121 | 1 | 1 | 96.6667 | 96.6667 | stored alive list |
| `p4w-coverage-guided` | `run-09` | 121 | 1 | 1 | 97.5000 | 97.5000 | stored alive list |
| `p4w-coverage-guided` | `run-10` | 121 | 1 | 1 | 96.6667 | 96.6667 | stored alive list |
| `p4bw-mutation-feedback` | `run-01` | 121 | 1 | 1 | 99.1667 | 99.1667 | stored alive list |
| `p4bw-mutation-feedback` | `run-02` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-03` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-04` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-05` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-06` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-07` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-08` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-09` | 121 | 1 | 1 | 98.3333 | 98.3333 | stored alive list |
| `p4bw-mutation-feedback` | `run-10` | 121 | 1 | 1 | 99.1667 | 99.1667 | stored alive list |
