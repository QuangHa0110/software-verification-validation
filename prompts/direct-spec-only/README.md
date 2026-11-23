# P3r – Sinh test từ đặc tả

`tools/generate_p3.py --run N --variant spec-only` ghép `system.md` và `generate.md` như P3. `{{API}}` lấy từ `docs/spec/api.md`. `{{SPEC}}` dùng cùng danh sách và thứ tự tài liệu với P3, gồm cả `branches.md`. Quy tắc oracle, định dạng JUnit, bộ lọc và cấu hình LLM dùng chung với P3.

Bộ test và `generation.json` được lưu ở `test-suites/p3r-spec-only/run-XX/`. Trường `prompt.source_files` là danh sách rỗng. Thêm `--dry-run` để xem prompt, số ký tự và hash.
