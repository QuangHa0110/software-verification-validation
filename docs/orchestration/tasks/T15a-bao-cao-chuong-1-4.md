# T15a – Báo cáo khoa học: Chương 1–4

**Worker:** agy (hoặc codex), effort cao · **Nhánh:** `docs/report` · **Thư mục làm việc:** worktree riêng

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc, gồm mục 4b), `docs/de-xuat-de-tai-1.md`, `docs/ke-hoach-thuc-hien.md` (mục 5, bảng phân công mục báo cáo), `docs/thiet-ke-ky-thuat.md`, `docs/orchestration/decisions.md`, `docs/spec/`, `docs/literature/`, `experiments/spikes/*/KET-QUA.md`, `experiments/config/default.yaml`, `subject/pom.xml`, `requirements.txt`, và mã trong `tools/`, `experiments/scripts/`, `prompts/` (nếu đã có).

## Phạm vi

- `docs/report/README.md`: mục lục, trạng thái từng mục
- `docs/report/chuong-1-gioi-thieu.md`, `chuong-2-co-so-ly-thuyet.md`, `chuong-3-phuong-phap.md`, `chuong-4-thiet-ke-thuc-nghiem.md`
- `docs/report/figures/*.svg` hoặc `.md` chứa sơ đồ dạng Mermaid (không cần ảnh PNG)
- Không sửa file nào khác.

## Cấu trúc (tiếng Việt, theo mẫu 6 chương của đề)

1. **Giới thiệu**: 1.1 Bối cảnh; 1.2 Vấn đề nghiên cứu; 1.3 Mục tiêu; 1.4 Câu hỏi nghiên cứu (RQ1–RQ6, nguyên văn đề xuất); 1.5 Đóng góp và cấu trúc báo cáo.
2. **Cơ sở lý thuyết và công trình liên quan**: 2.1 Kiểm thử phần mềm và tiêu chí bao phủ (statement, branch, function; cách JaCoCo đo, LINE làm đại diện statement); 2.2 Kiểm thử đột biến (toán tử, mutant tương đương, mutation score theo `Killed / (Total − Equivalent)`); 2.3 Sinh test tự động truyền thống (ngẫu nhiên, dựa trên tìm kiếm, thực thi tượng trưng); 2.4 Sinh test bằng LLM; 2.5 Bài toán oracle; 2.6 Khoảng trống nghiên cứu. Dùng **chỉ** `docs/literature/` làm nguồn, trích dẫn bằng khóa `[schafer-2024]` khớp `references.bib`.
3. **Phương pháp đề xuất**: 3.1 Kiến trúc tổng thể (sơ đồ Mermaid: đặc tả + mã → LLM → bộ lọc (subject, reference) → JaCoCo/PIT → phản hồi); 3.2 Sinh test trực tiếp (P3); 3.3 Sinh test có phản hồi coverage (P4) và phản hồi mutation (P4b), thuật toán dạng mã giả theo đúng mã nguồn `tools/generate_p4.py` (nếu chưa có thì theo `docs/thiet-ke-ky-thuat.md` 6.1 và ghi rõ); 3.4 Thiết kế prompt (theo `prompts/`, nếu chưa có thì để mục chờ, ghi `[CHỜ: prompts/]`); 3.5 Thực thi và cơ chế phản hồi; 3.6 Lọc và chọn test (quy tắc 4.2 của thiết kế, chương trình tham chiếu độc lập, chọn tham lam).
4. **Thiết kế thực nghiệm**: 4.1 Chương trình đối tượng (bảng lớp/phương thức từ `docs/spec/api.md`; số LINE/BRANCH/METHOD tổng = 235/174/55 lấy từ JaCoCo; các nhánh khó phủ có chủ ý; giới hạn: không có literal boolean); 4.2 Môi trường (phiên bản JDK, Maven, JUnit, JaCoCo, PIT, Python, thư viện, model LLM và CLI, lấy từ file trong repo); 4.3 Phương pháp đối chứng (P1, P2, P3, P4, P4b; EvoSuite không thực hiện, lý do D-013); 4.4 Chỉ số đánh giá (8 tiêu chí của đề + tỷ lệ biên dịch, tỷ lệ oracle đúng; ánh xạ 5 toán tử đột biến → mutator, D-012/D-016); 4.5 Quy trình thực nghiệm (10 lần, ghép cặp P3 → P4 → P4b, seed, Mann–Whitney U, Â12, lệnh tái lập).

## Quy tắc nội dung

- **Không có số liệu kết quả** ở các chương này (kết quả thuộc chương 5). Mọi con số mô tả môi trường/chương trình phải lấy từ file trong repo; ghi nguồn trong chú thích hoặc ngoặc.
- Chỗ chưa có thông tin: ghi `[CHỜ: <nguồn>]`, không đoán.
- Văn phong khoa học, câu ngắn, thuật ngữ tiếng Anh để trong ngoặc lần đầu xuất hiện.
- Nêu thẳng các hạn chế thiết kế đã biết: hiệu ứng trần coverage (P1 đạt 100%), token nền của CLI (≈ 22k codex, ≈ 15,5k agy), LLM qua CLI thay vì API trực tiếp (D-005), toán tử hằng boolean chỉ có 2 mutant (D-016).

## Kiểm tra

- Mọi khóa trích dẫn trong báo cáo có trong `docs/literature/references.bib` (viết một lệnh kiểm tra nhỏ bằng Python trong `.work/`, chạy và báo kết quả; không commit script đó).
- Không có BOM. Liệt kê các chỗ `[CHỜ: ...]` còn lại trong báo cáo cuối.
