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
| G2 Cài đặt | T02 subject (codex) · T03 reference (agy, `gemini-4-neon-high`) · T05 bộ P1 (codex) | Đang làm, song song trên 3 worktree |
| G3 Đo lường | T04/T06 measure.py · T07 mutation | Chưa bắt đầu |
| G4 Phương pháp | T08 P2 · T09 llm_client + bộ lọc · T10 P3 · T11 P4 · T12 P4b · T13 EvoSuite | Chưa bắt đầu |
| G5 Thực nghiệm | T14 run_experiment + analyze, chạy 10 lần | Chưa bắt đầu |
| G6 Báo cáo | T15 | Chưa bắt đầu |

## Nhật ký

| Thời điểm | Sự kiện |
|---|---|
| 23/11 | G0 xong. Codex: `codex exec` → `CODEX_OK` [ĐÃ CHẠY]. Maven trong sandbox codex: `mvn -q -B test` exit 0, surefire `tests="1" failures="0"` với cấu hình D-002 [ĐÃ CHẠY]. Agy: `agy -p` → `AGY_OK`; chạy `mvn -v` được [ĐÃ CHẠY]. |
| 23/11 | T00 xong (codex, effort high). Worker báo: `mvn -B -q -f subject/pom.xml test` exit 0; spike PIT `ALL` sinh 20 mutant; import 7 gói Python ra `ok`. Orchestrator đã đọc `KET-QUA.md` và diff; ra quyết định D-012 về bộ toán tử đột biến. |
| 23/11 | T01 xong (codex, effort high). Worker báo `javac` exit 0, đối chiếu `api.md` ↔ mã: 14 kiểu, 51 thành viên khớp. Orchestrator đã review đặc tả và tính lại các ví dụ chính bằng tay. Lưu ý: constructor package-private `Order` có kiểm tra phòng thủ chỉ chạm được khi gọi trực tiếp, là nguồn nhánh khó phủ. Gộp vào `main`, tag `m1-spec-frozen`. |
