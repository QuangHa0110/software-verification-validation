# Kết quả Spike S3 – Gọi LLM thực tế (`llm_client.py`)

**Trạng thái:** ĐÃ CHẠY thành công với cả hai provider: `agy-cli` (23/11, 14:45, mục 5) và `codex-cli` (23/11, 21:53, mục 6). Lệnh: `python -m tools.svv.llm_client --smoke --provider codex-cli`

> Ghi chú: nếu đặt `PYTHONPATH=tools`, có thể chạy tương đương bằng `python -m svv.llm_client --smoke --provider codex-cli` (hoặc đổi sang `--provider agy-cli` khi cần kiểm thử provider dự phòng).

---

## 1. Lần thử thật trước đó (đã ghi log lỗi)

- **File log:** `results/llm-logs/spike-s3/run-01/calls.jsonl` (dòng 1)
- **Thời gian (UTC):** `2026-11-23T07:13:26.715401+00:00` → `2026-11-23T07:13:27.028646+00:00` (`duration_s = 0.313`)
- **Cấu hình:** `provider = "codex-cli"`, `model = "gpt-6.2-astra"`, `effort = "medium"`, `purpose = "smoke"`
- **Prompt đã gửi:** ````Viết một phương thức JUnit 5 @Test kiểm tra 1+1==2. Chỉ trả về một khối mã ```java. Không dùng công cụ.````
- **Token / phản hồi:** `input_tokens = 0`, `output_tokens = 0`, `tool_events = 0`, `response = ""`
- **Lỗi nguyên văn:**

```text
[WinError 5] Access is denied: '%TEMP%\svv-codex-et32id7e'
```

## 2. Nguyên nhân

Phiên bản trước của `tools/svv/llm_client.py` tạo thư mục làm việc rỗng cho tiến trình con CLI bằng `tempfile.TemporaryDirectory` dưới thư mục `%TEMP%` của hệ thống (`%TEMP%\svv-codex-...`). Khi chạy bên trong sandbox của Codex trên Windows, tiến trình bị từ chối quyền truy cập/tạo thư mục ngoài workspace tại `%TEMP%`, dẫn tới `[WinError 5] Access is denied` trước khi chạy được lệnh `codex exec`.

## 3. Đã sửa như thế nào

1. **Thêm khóa cấu hình `llm.scratch_dir`** trong `experiments/config/default.yaml` với giá trị mặc định `.work/llm-scratch` (nằm trong `.work/`, đã được loại trừ bởi `.gitignore`).
2. **Thay cơ chế tạo thư mục tạm trong `tools/svv/llm_client.py`**: bỏ hoàn toàn `tempfile.TemporaryDirectory` ở `%TEMP%`, thay bằng context manager `_scratch_dir` tạo một thư mục rỗng mới theo UUID dưới `llm.scratch_dir` (tính theo gốc repo nếu là đường dẫn tương đối) cho mỗi lần gọi `codex-cli` hoặc `agy-cli`.
3. **Dọn dẹp an toàn sau mỗi lần gọi**: xóa thư mục tạm trong khối `finally`; nếu xóa gặp `OSError` (ví dụ file lock trên Windows) thì chỉ phát cảnh báo `RuntimeWarning` thay vì làm hỏng kết quả gọi LLM.
4. **Thêm giao diện CLI một dòng** (`__main__`) trong `tools/svv/llm_client.py` hỗ trợ `--smoke`, `--provider`, `--model`, `--effort`, `--method`, `--run`, `--purpose`, `--prompt` để orchestrator chạy lại S3 trực tiếp.

## 4. Hướng dẫn orchestrator chạy lại và ghi nhận kết quả

Chạy từ gốc repo:

```powershell
python -m tools.svv.llm_client --smoke --provider codex-cli
```

Sau khi chạy thành công, điền các trường sau từ stdout và dòng mới nhất của `results/llm-logs/spike-s3/run-01/calls.jsonl`:

- Lệnh đã chạy: _(chờ orchestrator chạy)_
- `provider` / `model` / `effort`: _(chờ orchestrator chạy)_
- Token (`input_tokens`, `cached_input_tokens`, `output_tokens`, `reasoning_output_tokens`): _(chờ orchestrator chạy)_
- Thời gian (`duration_s`) và `tool_events`: _(chờ orchestrator chạy)_
- Phản hồi (khối ` ```java `): _(chờ orchestrator chạy)_

> **Lưu ý khi so sánh chi phí:** Với `codex-cli`, `input_tokens` bao gồm cả phần ngữ cảnh nền (system prompt / hướng dẫn công cụ của codex — khoảng 22 nghìn token ngay cả với prompt rất ngắn, đo ngày 23/11). Cần ghi rõ yếu tố này khi đối chiếu lượng token và chi phí giữa các phương pháp hoặc giữa `codex-cli` với các provider khác.

## 5. Kết quả chạy lại bằng `agy-cli` (orchestrator, 23/11/2026)

- Lệnh: `python -m tools.svv.llm_client --smoke --provider agy-cli --model gemini-3.8-flash-low` → exit 0
- `provider` / `model` / `effort`: `agy-cli` / `gemini-3.8-flash-low` / `medium` (agy không nhận tham số effort; mức suy luận nằm trong tên model)
- Token: `input_tokens = 15566`, `cached_input_tokens = 0`, `output_tokens = 64`, `reasoning_output_tokens = 0`
- `duration_s = 6.08` (theo agy); từ `started_at` đến `ended_at` ≈ 12,3 s; `tool_events = 0`
- Phản hồi: một khối ```` ```java ```` chứa lớp `MathTest` với `@Test void testAddition() { assertEquals(2, 1 + 1); }`
- Log: dòng cuối của `results/llm-logs/spike-s3/run-01/calls.jsonl`
- Nhận xét: giống codex, `input_tokens` của agy gồm phần ngữ cảnh nền (≈ 15,5 nghìn token với prompt rất ngắn).

## 6. Kết quả chạy lại bằng `codex-cli` (orchestrator, 23/11/2026, 21:53)

- Lệnh: `python -m tools.svv.llm_client --smoke --provider codex-cli` → exit 0
- `provider` / `model` / `effort`: `codex-cli` / `gpt-6.2-astra` / `medium`
- Token: `input_tokens = 22587`, `cached_input_tokens = 15232`, `output_tokens = 41`, `reasoning_output_tokens = 0`
- `duration_s = 16.76`; `tool_events = 0`
- Phản hồi: một khối ```` ```java ```` chứa `@org.junit.jupiter.api.Test void onePlusOneEqualsTwo() { ...assertEquals(2, 1 + 1); }`, đúng định dạng một phương thức mỗi khối
- Log: dòng cuối của `results/llm-logs/spike-s3/run-01/calls.jsonl`
- Kết luận S3: cấu hình mặc định (`codex-cli`, `gpt-6.2-astra`, effort `medium`) dùng được cho thực nghiệm. Token đầu vào gồm ≈ 22,5 nghìn token nền của codex, trong đó phần lớn được cache.
