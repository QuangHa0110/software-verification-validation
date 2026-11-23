# Nhật ký quyết định

Quyết định do orchestrator (Claude Code) đưa ra khi phát triển tự động, không chờ nhóm trả lời. Nhóm có thể đảo ngược bất kỳ mục nào; khi đó ghi một mục mới tham chiếu mục cũ, không xóa.

| Mã | Ngày | Quyết định | Lý do | Bằng chứng |
|---|---|---|---|---|
| D-001 | 23/11 | Worker: **codex** (`gpt-6.2-astra`, codex-cli 0.171.0) ưu tiên; **agy** (Antigravity CLI 1.5.14) thứ hai; subagent Claude chỉ khi cả hai hết quota | Yêu cầu của người dùng: dùng hết quota codex trước | Đã chạy: `codex exec` trả `CODEX_OK`; `agy -p` trả `AGY_OK` |
| D-002 | 23/11 | Codex chạy với `-s workspace-write -c windows.sandbox="unelevated" -c sandbox_workspace_write.network_access=true`, Maven dùng local repository riêng qua `MAVEN_OPTS=-Dmaven.repo.local=...` và `--add-dir` | Sandbox `elevated` mặc định làm `javac` gặp `AccessDeniedException` khi mở jar; chạy không sandbox bị chính sách quyền chặn | Đã chạy: với cấu hình này `mvn -q -B test` exit 0, surefire `tests="1" failures="0"` |
| D-003 | 23/11 | Áp dụng mọi mục **[Quyết định đề xuất]** trong `docs/thiet-ke-ky-thuat.md`: công cụ điều phối bằng Python; quy tắc nghiệp vụ mục 3 làm đặc tả; thiết kế ghép cặp P3 → P4 → P4b; giữ P4b | Nhóm chưa xác nhận; người dùng yêu cầu tự chọn phương án hợp lý | – |
| D-004 | 23/11 | JDK 17.0.12 (Oracle), Maven 3.9.15, Python 3.12.16 tại `<CONDA_ENV>` | Môi trường có sẵn và người dùng chỉ định; 3.12 thỏa "3.11 trở lên" | Đã chạy: `java -version`, `mvn -v`, `python --version` |
| D-005 | 23/11 | LLM của thực nghiệm (P3, P4, P4b): **codex CLI** làm provider `codex-cli`, chạy trong thư mục rỗng tạm, sandbox `read-only`, toàn bộ ngữ cảnh nằm trong prompt. `llm_client.py` vẫn có provider `anthropic` để dùng khi có khóa | Máy không có `.env` hay khóa API riêng cho thực nghiệm; người dùng có dư quota codex. Không dùng thông tin xác thực của phiên Claude Code cho thực nghiệm | Chưa chạy (sẽ kiểm ở T9/S3) |
| D-006 | 23/11 | Tính độc lập của oracle: `reference/` do **agy** viết, trong worktree chỉ chứa đặc tả và khung chữ ký; P1 do một phiên codex khác viết trong worktree không có mã `subject/` | Thiết kế 4.1 yêu cầu người/phiên khác, không xem mã `subject/`; khác họ model giảm nguy cơ hiểu sai giống nhau | – |
| D-007 | 23/11 | Git: mỗi gói một nhánh `feature/...` cục bộ; orchestrator review rồi `merge --no-ff` vào `main` cục bộ; **không push** | Push là thao tác ra bên ngoài, người dùng chưa yêu cầu | – |
| D-008 | 23/11 | Commit message theo quy ước README, **không** có trailer Co-authored-by, dòng "Generated with Claude Code" hay `Claude-Session:`; kiểm bằng `git log -1 --format=%B` sau mỗi commit | Yêu cầu của người dùng | – |
| D-009 | 23/11 | Mọi file văn bản UTF-8 không BOM; Python ghi rõ `encoding="utf-8"`; không ghi file bằng `>`/`Out-File` trên PowerShell 5.1 | Yêu cầu của người dùng; BOM làm `javac` lỗi | – |
| D-010 | 23/11 | Brief giao việc lưu trong `docs/orchestration/tasks/`; log thô của worker lưu ngoài repo tại `<ORCH_DIR>\logs\` | Brief là tài liệu tái lập quy trình; log thô lớn, không cần vào git | – |
