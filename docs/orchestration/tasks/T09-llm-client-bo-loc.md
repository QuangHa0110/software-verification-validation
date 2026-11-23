# T09 – `llm_client.py` và bộ lọc test `test_filter.py`

**Worker:** codex, effort `high` · **Nhánh:** `feature/llm-direct` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/orchestration/decisions.md` (D-005), `docs/thiet-ke-ky-thuat.md` mục 4.2, 5.1, 6.2, 6.3, 9; thư viện `tools/svv/` có sẵn (đặc biệt `workdir.py`, `maven.py`, `surefire.py`, `config.py`).

## Phạm vi

- `tools/svv/llm_client.py`, `tools/svv/test_filter.py`, `tools/svv/test_blocks.py`
- `tools/tests/test_llm_client.py`, `tools/tests/test_test_filter.py`, `tools/tests/test_test_blocks.py`, `tools/tests/fixtures/**`
- `experiments/config/default.yaml`: chỉ thêm khóa mới cần cho phần này, không đổi khóa cũ
- `experiments/spikes/s3/KET-QUA.md`: kết quả spike S3

## 1. `llm_client.py`

Giao diện: `complete(prompt: str, *, method: str, run: int, purpose: str) -> LlmResult`, với `LlmResult` gồm `text`, `input_tokens`, `cached_input_tokens`, `output_tokens`, `reasoning_output_tokens`, `duration_s`, `model`, `effort`, `provider`, `tool_events` (số sự kiện công cụ mà agent đã chạy). Provider chọn theo `llm.provider` trong cấu hình.

- **`codex-cli`** (mặc định): chạy `codex exec --skip-git-repo-check --ephemeral --json -s read-only -c model_reasoning_effort=<effort> -m <model> -` trong **một thư mục tạm rỗng mới** (ngoài repo), prompt đưa qua stdin, `subprocess` không dùng shell, có timeout lấy từ cấu hình. Đọc luồng JSONL: văn bản từ các `item.completed` có `item.type == "agent_message"` (nối theo thứ tự); token từ `turn.completed.usage` (`input_tokens`, `cached_input_tokens`, `output_tokens`, `reasoning_output_tokens`). Đếm mọi `item` có `type` khác `agent_message` và `reasoning` vào `tool_events`. Lỗi (exit khác 0, không có `turn.completed`, timeout) → ném ngoại lệ có thông điệp rõ, **ghi log cả lần gọi lỗi**. Nhận biết hết quota: nếu stderr/JSON chứa dấu hiệu giới hạn (rate limit, usage limit, quota) thì ném `LlmQuotaExceeded`.
- **`anthropic`**: dùng SDK `anthropic`, khóa `ANTHROPIC_API_KEY` đọc từ môi trường hoặc `.env` ở gốc repo (tự đọc file, không thêm thư viện). Không cần chạy thật (máy không có khóa); chỉ cần test bằng mock.
- **Log**: mỗi lần gọi ghi **một dòng JSON** (UTF-8, `ensure_ascii=False`) vào `results/llm-logs/<method>/run-XX/calls.jsonl`: thời điểm bắt đầu/kết thúc (ISO 8601), provider, model, effort, purpose, prompt đầy đủ, phản hồi đầy đủ, token, `duration_s`, `tool_events`, `error` (nếu có). Không ghi khóa API hay biến môi trường.
- `cost_usd` = (`input_tokens` × `price_per_mtok_input` + `output_tokens` × `price_per_mtok_output`) / 10^6; giá `null` trong cấu hình → `cost_usd = null`.

## 2. `test_blocks.py`

Theo thiết kế 6.2: tách phản hồi LLM thành các khối test. Quy ước định dạng phản hồi (prompt sẽ yêu cầu): mỗi test là một khối mã ```` ```java ```` chứa **đúng một** phương thức `@Test` (hoặc `@ParameterizedTest` kèm annotation nguồn) tự chứa. Hàm `extract_blocks(text) -> list[TestBlock]` (tên phương thức, thân, các dòng `import` nếu khối có). Hàm `wrap_block(block, class_name) -> str` bọc thành lớp `package shop;` với import chuẩn (`org.junit.jupiter.api.*`, `static org.junit.jupiter.api.Assertions.*`, `org.junit.jupiter.params.*`, `org.junit.jupiter.params.provider.*`, `java.time.*`, `java.util.*`) cộng import của khối. Tên lớp `<Prefix>_NNNTest`. Hàm chuẩn hóa thân (bỏ chú thích, gộp khoảng trắng) để so trùng.

## 3. `test_filter.py`

Đầu vào: danh sách khối (hoặc thư mục file test), bộ đã giữ (để loại trùng). Đầu ra: danh sách test giữ lại và danh sách bị loại kèm lý do, theo đúng quy tắc thiết kế 4.2:

1. Biên dịch được với `subject/` → nếu không: `compile_error`. Biên dịch **cả lô một lần**; nếu lô lỗi, xác định khối lỗi bằng đường dẫn file trong thông báo lỗi `javac`/Maven, loại các khối đó rồi biên dịch lại (lặp đến khi sạch).
2. Chạy trên `reference/` → test fail/error: `fails_on_reference` (oracle sai).
3. Chạy trên `subject/` → test xanh trên reference mà fail trên subject: `fails_on_subject_only`.
4. Trùng thân (sau chuẩn hóa) với test đã giữ hoặc với khối trước trong lô: `duplicate`.

Dùng `workdir.py` với `program=reference` / `subject`; chạy surefire một lần cho cả lô và đọc kết quả từng phương thức. Trả về số liệu: `generated`, `compiled`, `passed_reference`, `valid`, và hai tỷ lệ thiết kế 4.2 (tỷ lệ biên dịch được, tỷ lệ oracle đúng). Có hàm ghi bộ test giữ lại ra thư mục `test-suites/<method>/run-XX/` (mỗi khối một file `.java`).

## 4. Spike S3 (chạy thật, một lần)

Gọi `complete()` với provider `codex-cli`, prompt ngắn (ví dụ: "Viết một phương thức JUnit 5 @Test kiểm tra 1+1==2, trả về trong một khối ```java"). Ghi vào `experiments/spikes/s3/KET-QUA.md`: lệnh, model, effort, token, thời gian, `tool_events`, phản hồi, và nhận xét: **token đầu vào gồm cả phần nền của codex** (khoảng 22 nghìn với prompt rất ngắn, đo ngày 23/11), phải nêu rõ khi so sánh chi phí. Log của lần gọi này ghi vào `results/llm-logs/spike-s3/run-01/calls.jsonl`.

## 5. Kiểm tra (chạy thật, ghi output vào báo cáo)

- `python -m pytest tools/tests -q` xanh, gồm:
  - parser JSONL của codex với fixture (gồm trường hợp lỗi và hết quota), provider anthropic với mock;
  - `extract_blocks`/`wrap_block`;
  - **bộ lọc chạy thật trên Maven** với 4 khối cố ý: một khối lỗi biên dịch, một khối oracle sai (fail trên reference), một khối trùng, một khối đúng. Mỗi khối bị loại đúng lý do, khối đúng được giữ. Đánh dấu test này `@pytest.mark.maven` (đăng ký marker) vì chạy chậm.
- Spike S3 chạy thành công một lần.
