# Theo dõi tiến độ phát triển

Mỗi khẳng định được gắn một trong ba nhãn:

- **[ĐÃ CHẠY]**: orchestrator đã chạy lệnh kiểm tra và thấy output thực tế (ghi kèm lệnh).
- **[CHƯA CHẠY]**: đã viết nhưng chưa có lệnh kiểm tra nào được chạy.
- **[GIẢ ĐỊNH]**: điều được giả định, chưa kiểm chứng.

Quyết định: [decisions.md](decisions.md). Luật cho worker: [worker-rules.md](worker-rules.md). Brief: [tasks/](tasks/).

## Cách gọi worker

Codex (bắt buộc dùng cấu hình này, xem D-002):

```
codex exec -C <thư mục làm việc> -s workspace-write \
  -c windows.sandbox='"unelevated"' -c sandbox_workspace_write.network_access=true \
  -c 'shell_environment_policy.set.MAVEN_OPTS="-Dmaven.repo.local=<M2_REPO>"' \
  --add-dir <M2_REPO> --add-dir <CONDA_ENV> \
  -c model_reasoning_effort=<low|medium|high|xhigh> \
  -o <log>\<task>.last.md - < docs/orchestration/tasks/<task>.md
```

Agy: `agy -p "<prompt>" --model <model> --print-timeout <t>` chạy trong thư mục làm việc.

## Giai đoạn

| Giai đoạn | Bước | Trạng thái |
|---|---|---|
| G0 Hạ tầng điều phối | Kiểm tra worker, nhật ký quyết định, luật | Xong. [ĐÃ CHẠY] smoke test codex và agy |
| G1 Nền móng | T00 môi trường/pom/S1/S2 · T01 đặc tả + khung | Xong, tag `m1-spec-frozen`. [ĐÃ CHẠY] `mvn -B -q -f subject/pom.xml compile` exit 0 trên `main` |
| G2 Cài đặt | T02 subject (codex) · T03 reference (agy, `gemini-4-neon-high`) · T05 bộ P1 (codex) | Xong. [ĐÃ CHẠY] P1 181/181 xanh trên subject và reference |
| G3 Đo lường | T06 measure.py · T07 mutation | Xong. [ĐÃ CHẠY] đủ 5 toán tử đột biến |
| G4 Phương pháp | T08 P2 · T09 llm_client + bộ lọc · T10 P3 · T11 P4 · T12 P4b · ~~T13 EvoSuite~~ (D-013) | T08–T12 xong. T18 (bóc tách ngữ cảnh hạn chế, D-024) đang làm |
| G5 Thực nghiệm | T14 run_experiment + analyze, chạy 10 lần | P1, P2 ×10, P3/P4/P4b run-01..05 đã đo; đang chạy run-06..10 |
| G6 Báo cáo | T16 tổng quan tài liệu · T15a chương 1–4 · T15b chương 5–6 | T16, T15a xong |

## Nhật ký

| Thời điểm | Sự kiện |
|---|---|
| 23/11 | G0 xong. Codex: `codex exec` → `CODEX_OK` [ĐÃ CHẠY]. Maven trong sandbox codex: `mvn -q -B test` exit 0, surefire `tests="1" failures="0"` với cấu hình D-002 [ĐÃ CHẠY]. Agy: `agy -p` → `AGY_OK`; chạy `mvn -v` được [ĐÃ CHẠY]. |
| 23/11 | T00 xong (codex, effort high). Worker báo: `mvn -B -q -f subject/pom.xml test` exit 0; spike PIT `ALL` sinh 20 mutant; import 7 gói Python ra `ok`. Orchestrator đã đọc `KET-QUA.md` và diff; ra quyết định D-012 về bộ toán tử đột biến. |
| 23/11 | T01 xong (codex, effort high). Worker báo `javac` exit 0, đối chiếu `api.md` ↔ mã: 14 kiểu, 51 thành viên khớp. Orchestrator đã review đặc tả và tính lại các ví dụ chính bằng tay. Lưu ý: constructor package-private `Order` có kiểm tra phòng thủ chỉ chạm được khi gọi trực tiếp, là nguồn nhánh khó phủ. Gộp vào `main`, tag `m1-spec-frozen`. |
| 23/11 | T02 xong (codex). [ĐÃ CHẠY] orchestrator: `mvn -f subject/pom.xml test -Psuite -Dsuite.dir=.../test-suites/smoke` exit 0, 17 test, 0 lỗi. Worker báo (chưa kiểm lại): JaCoCo LINE 220/235, BRANCH 129/174; PIT DEFAULTS 167 mutant, diệt 137. Commit trên `feature/subject-program`, chưa gộp `main` cho đến khi T03 và T05 (yêu cầu độc lập) xong. |
| 23/11 | T03 xong (agy `gemini-4-neon-high`). [ĐÃ CHẠY] kiểm tra chéo có `clean`: smoke của subject (17 test) xanh trên reference; smoke của reference (18 test) xanh trên subject. Lưu ý: chạy profile `suite` không có `clean` thì Maven chạy cả lớp test cũ trong `target/test-classes`. Reference dùng `build-helper-maven-plugin` 3.6.0, subject dùng 3.6.1 (khác biệt nhỏ, ghi nhận). |
| 23/11 | T05 xong (codex): 181 ca (54 `@Test` + 127 ca tham số), truy vết 32/32 dòng `branches.md`. [ĐÃ CHẠY] trên `main` sau khi gộp: `mvn -f subject/pom.xml clean test -Psuite -Dsuite.dir=test-suites/p1-manual/run-01` → 181 test, 0 lỗi; trên `reference` → 181 test, 0 lỗi. Thiết kế 4.3 (subject ≡ reference trên P1) đạt. [ĐÃ CHẠY] JaCoCo của P1 trên subject: LINE 235/235, BRANCH 174/174, METHOD 55/55. **Rủi ro hiệu ứng trần**: P1 đã phủ 100%, nên chênh lệch coverage giữa các phương pháp có thể nhỏ; mutation score sẽ là thước đo phân biệt chính. Đưa vào 5.6 Threats to Validity. |
| 23/11 | **G2 xong** (tương đương mốc M2 về chương trình và P1). Gộp `feature/subject-program`, `feature/reference`, `feature/manual-tests` vào `main`. |
| 23/11 | T06 xong (codex). [ĐÃ CHẠY] orchestrator: `pytest tools/tests -q` → 12 passed; `measure.py --suite test-suites/p1-manual/run-01 --method p1-manual --run 1 --mutation` → exit 0, 181 pass, LINE/BRANCH/METHOD 100%, PIT 82 mutant, diệt 80, score 97,56% (chỉ là kiểm tra công cụ, đã xóa; số chính thức do T14 tạo); `--program reference` → exit 0, 181 pass. Gộp vào `main`. |
| 23/11 | T13 (EvoSuite) bị chính sách quyền chặn lúc giao việc → bỏ, xem D-013. |
| 23/11 14:11 | **Codex hết quota** (mở lại 16:31) khi T07, T08, T09 đang chạy; cả ba dừng giữa chừng, không có báo cáo. Chuyển sang agy (D-015): T07b, T08b, T09b tiếp tục trên phần việc dở; thêm T16 (tổng quan tài liệu, agy). |
| 23/11 | T08 xong (codex dở → agy hoàn thành). [ĐÃ CHẠY] orchestrator: bộ sinh không tham chiếu `subject` (grep rỗng); sinh lại `run-03` cho cùng SHA-256 `17c7dff1…`; `run-07` 100/100 xanh trên reference; pytest 23 passed. Worker báo (chưa kiểm lại) P2 run-01 trên subject: LINE 217/235, BRANCH 135/174, PIT 51/82. Gộp vào `main`. |
| 23/11 | T16 xong (agy): 17 công trình, DOI cho từng mục, phát hiện đề xuất ghi sai CoverUp (thực tế FSE 2025, tên mới) và tiêu đề ChatTester. Orchestrator kiểm chéo bằng hiểu biết một số mục (TestPilot, ChatTester, MuTAP, CodaMosa): khớp; các mục khác dựa trên nguồn worker ghi. Gộp vào `main`. |
| 23/11 | T07 xong (codex dở → agy T07b), T07c bổ sung theo D-016/D-017. [ĐÃ CHẠY] orchestrator: pytest 21 passed; `measure.py` trên smoke có `--mutation` → 121 mutant, by_operator: relational 28, arithmetic 19, conditional 31, boolean_constant 2, statement_deletion 41 (đủ 5 toán tử); P1 trên reference exit 0. Gộp vào `main`. |
| 23/11 | T09 xong (codex dở → agy T09b, T09c). agy hai lần dừng sớm vì quyền/lệnh nền (D-018, D-019). [ĐÃ CHẠY] orchestrator: pytest 35 passed; test Maven thật của bộ lọc 4 khối PASSED; spike S3 qua `agy-cli` exit 0 (15566 token vào, 64 ra). S3 qua `codex-cli` chờ quota. Trên `main` sau gộp: pytest 55 passed. |
| 23/11 | T10 xong (agy): `prompts/direct/` (system + generate, oracle theo đặc tả, định dạng khối), `generate_p3.py`. [ĐÃ CHẠY] orchestrator: pytest 64 passed; worker báo `--dry-run` → prompt 59.804 ký tự. Chưa chạy LLM thật (D-018). Gộp vào `main`. |
| 23/11 | T14 xong (agy): `run_experiment.py`, `analyze.py`, `tools/svv/stats.py`. [ĐÃ CHẠY] orchestrator: pytest 99 passed; `--dry-run` 81 bước theo thứ tự ghép cặp. Gộp vào `main`. |
| 23/11 | T15a xong (agy): báo cáo chương 1–4 bản nháp, 3 sơ đồ Mermaid; còn các mốc `[CHỜ]` cho P4 và kết quả. Orchestrator kiểm ngẫu nhiên: phiên bản công cụ và số 235/174/55 khớp repo; số liệu % ở chương 2 truy được về `docs/literature/`. Gộp vào `main`. |
| 23/11 15:02 | **agy hết quota** model `gemini-4-neon-high` (T11 lần 1 thất bại); T11 lần 2 bằng `claude-opus-5-5-high` bị từ chối quyền (D-022). T11 chờ codex 16:31. Orchestrator bắt đầu đo chính thức P1 (run-01) và P2 (run-01..10) trên nhánh `experiment/run-20261123`. |
| 23/11 15:20 | **Đo chính thức P1, P2 xong** (orchestrator chạy `run_experiment.py --methods p1-manual,p2-random --runs 1-10 --skip-generation`, exit 0). [ĐÃ CHẠY] P1: 181 test, LINE/BRANCH/METHOD 100%, mutation 119/121 (98,35%). P2 (10 lần): LINE 86,8–94,5%, BRANCH 71,3–81,6%, METHOD 100%, mutation 57,85–70,25%. Gộp vào `main`. |
| 23/11 15:25 | agy hết quota cả `gemini-3.8-flash-high` (D-023). T17 giao subagent Claude. |
| 23/11 15:39 | T17 xong (subagent Claude): khóa mutant có `index`, `mutation.alive`, `analyze.py` tính lại score theo CSV. [ĐÃ CHẠY] orchestrator: pytest 108 passed; `mutation_ids.py` tách được hai mutant dòng 24 (index 80, 85). Rà soát đợt 1 (`results/mutation/equivalent-review.md`): 1 mutant tương đương (`PricingService:24`, index 85); `ShippingCalculator:8` index 10 **không** tương đương, là lỗ hổng của P1 (thiếu ca tiền hàng = 0). [ĐÃ CHẠY] `analyze.py` sau rà soát: P1 mutation 99,1667% (119/120). |
| 23/11 (chiều) | Máy người dùng tắt từ ≈ 16:10 đến 21:50; tiếp tục lúc 21:52. |
| 23/11 21:53 | [ĐÃ CHẠY] S3 qua `codex-cli`: exit 0, 22.587 token vào (15.232 cache), 41 ra, 16,8 s, `tool_events=0`. |
| 23/11 22:00 | T11 + T11d xong (codex `xhigh`, `low`): `generate_p4.py`, `feedback.py`, `selection.py`, prompt coverage-guided và mutation-feedback; schema `iterations`. [ĐÃ CHẠY] orchestrator: pytest 128 passed. Gộp vào `main`. |
| 23/11 22:05 | [ĐÃ CHẠY] P3 run-01..05 thật: mỗi lần 27–30 test, 100% biên dịch và 100% oracle đúng; LINE 235/235, BRANCH 174/174, METHOD 55/55; killed 120/121 (còn lại đúng mutant tương đương). ≈ 40k token vào + 9,7–9,9k ra, 212–250 s mỗi lần. P4 run-01..05 và P4b run-01..05: **0 vòng** (đã đạt ngưỡng). → **Hiệu ứng trần** xác nhận, quyết định D-024 (thêm thí nghiệm ngữ cảnh hạn chế, T18). |
| 23/11 22:26 | Codex hết quota lần 2 (mở lại 22:56) khi T18 đang chạy và thực nghiệm chính tới P3 run-06; `run_experiment.py` dừng sạch, in lệnh chạy tiếp [ĐÃ CHẠY]. agy báo 503 (hết capacity máy chủ) cho việc cập nhật báo cáo, hoãn. Commit P3/P4/P4b run-01..05 trên `experiment/run-20261123`. |
| 23/11 22:57 | Tiếp tục: T18b (codex `medium`) và thực nghiệm chính từ run-06. |
| 23/11 23:10 | T18 xong (codex, 2 phiên). [ĐÃ CHẠY] orchestrator: prompt rút gọn không chứa `{{SOURCE}}`; pytest 133 passed. Gộp vào `main`. Báo cáo chương 3–4 cập nhật theo mã thật (agy, gộp). |
| 23/11 23:48 | **Thực nghiệm chính đủ 10 lần** [ĐÃ CHẠY]: P3 run-01..10 đều LINE 235/235, BRANCH 174/174, METHOD 55/55, killed 120/121 (23–32 test mỗi lần); P4 và P4b run-01..10 đều dừng 0 vòng. Commit trên `experiment/run-20261123`. Bắt đầu chuỗi ngữ cảnh hạn chế 10 lần. |
| 24/11 00:00 | [ĐÃ CHẠY] P3r run-01: cũng LINE/BRANCH 100%, killed 120/121 → hiệu ứng trần không do mã nguồn. Quyết định D-025: thêm yếu tố LLM yếu hơn (T19). agy headless bị từ chối quyền ngay đầu → giao T19 cho codex. |
| 24/11 00:20 | T19 xong (codex `high`). [ĐÃ CHẠY] orchestrator: pytest 142 passed; thử P3w run-01 thật qua `agy-cli` `gemini-3.8-flash-low`: 74 test, 100% hợp lệ, prompt gửi qua file (`prompt_via_file: True`). Gộp vào `main`. Chuỗi LLM yếu sẽ chạy sau chuỗi rút gọn (tránh nhiễu thời gian đo). |
| 24/11 00:51 | [ĐÃ CHẠY] Chuỗi ngữ cảnh hạn chế run-01..06: P3r cả 6 lần LINE/BRANCH 100%, killed 120/121; P4r, P4br 0 vòng. Dừng ở run-07 do codex hết quota lần 3 (mở lại 03:57). Commit. |
| 24/11 01:20 | [ĐÃ CHẠY] Chuỗi LLM yếu run-01: P3w LINE 234, BRANCH 169, killed 108; P4w 4 vòng → BRANCH 173 (dừng vì 2 vòng không tăng). P4bw run-01 quá chậm (bước chọn đo mutation cho k(k+1)/2 tổ hợp) → dừng, D-026, brief T21. Log lời gọi đã thực hiện lưu thành `aborted-20261124-0116-calls.jsonl`. agy không dùng cho việc viết mã nữa (D-027). |
| 24/11 02:00 | Rà soát mutant tương đương đợt 2: không có mutant tương đương mới (mọi mutant còn sống khác index 85 đều đã bị P1 hoặc P3 diệt). |
| 24/11 03:05 | [ĐÃ CHẠY] P3w/P4w run-02..10: P3w BRANCH 174/174 ở 8/10 lần (run-01: 169, run-10: 173); P4w nâng run-01 lên 173, run-10 lên 174. **Mutation** P3w diệt 108–117/120 mutant không tương đương, P4w không đổi → P4bw có chỗ cải thiện. Chờ T21 (codex 03:57). |
