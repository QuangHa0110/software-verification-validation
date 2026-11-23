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
| G4 Phương pháp | T08 P2 · T09 llm_client + bộ lọc · T10 P3 · T11 P4 · T12 P4b · ~~T13 EvoSuite~~ (D-013) | T08, T09 xong. T10 (agy), T14 (agy) đang làm |
| G5 Thực nghiệm | T14 run_experiment + analyze, chạy 10 lần | Chưa bắt đầu |
| G6 Báo cáo | T16 tổng quan tài liệu · T15 báo cáo | T16 xong |

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
