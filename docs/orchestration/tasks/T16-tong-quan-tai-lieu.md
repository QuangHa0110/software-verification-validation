# T16 – Ghi chú tổng quan tài liệu (Chương 2)

**Worker:** codex, effort `high`, có tìm kiếm web · **Nhánh:** `docs/literature` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/de-xuat-de-tai-1.md` (mục 2, 5, 11), `docs/ke-hoach-thuc-hien.md` (mục "Tổng quan tài liệu").

## Phạm vi

- `docs/literature/README.md`: bảng tổng hợp
- `docs/literature/<khóa>.md`: một file cho mỗi công trình (khóa dạng `tacgia-nam`, ví dụ `schafer-2024`)
- `docs/literature/references.bib`: BibTeX
- Xóa `docs/literature/.gitkeep`

Không sửa file nào khác.

## Yêu cầu

1. Với **12 công trình** ở mục 11 của đề xuất: **tra cứu và xác minh** tên đầy đủ, tác giả, năm, nơi công bố chính thức (hội nghị/tạp chí, tập, trang hoặc số bài nếu có), DOI hoặc URL chính thức (DBLP, ACM DL, IEEE Xplore, arXiv, trang hội nghị). Ghi cột "Đã xác minh từ" là URL nguồn đã đọc. Chỗ nào **không xác minh được** thì ghi rõ "chưa xác minh", không đoán. Nếu đề xuất ghi sai (năm, nơi công bố, tác giả) thì ghi bản đúng kèm nguồn và ghi chú "đề xuất ghi …".
2. Ưu tiên công trình 5 năm gần đây. Tìm thêm **3–5 công trình** liên quan trực tiếp (sinh test bằng LLM có phản hồi coverage hoặc mutation; đánh giá oracle của test do LLM sinh), xuất bản 2023–2026, có nguồn chính thức kiểm chứng được.
3. Mỗi file công trình (tiếng Việt, ngắn gọn): vấn đề, phương pháp, hệ thống/benchmark đánh giá, kết quả chính (**chỉ** số liệu có trong bài, ghi rõ bảng/mục), hạn chế, liên hệ với đề tài (P3, P4, P4b, oracle, mutation). Không viết điều không có trong nguồn đã đọc.
4. `README.md`: bảng so sánh (cột: công trình, năm, nơi công bố, ngôn ngữ đích, có phản hồi coverage, có phản hồi mutation, cách xử lý oracle, chỉ số đánh giá), và một mục "Khoảng trống nghiên cứu" 5–8 gạch đầu dòng **dựa trên bảng**, chỉ ra vấn đề mà đề tài giải quyết (phản hồi mutation, kiểm tra oracle bằng chương trình tham chiếu độc lập, so sánh với ngẫu nhiên và viết tay trên cùng điều kiện).

## Tiêu chí xong

- Đủ ≥ 15 công trình, mỗi công trình có nguồn URL đã đọc hoặc nhãn "chưa xác minh".
- `references.bib` khớp bảng.
- Báo cáo cuối liệt kê các chỗ đề xuất ghi sai đã phát hiện.
