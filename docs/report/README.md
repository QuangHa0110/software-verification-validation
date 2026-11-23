# Báo cáo khoa học – Đề tài 1: LLM-based Test Generation

**Tên đề tài:** *LLM-based Test Generation: Sinh kiểm thử tự động bằng mô hình ngôn ngữ nhằm tối ưu hóa độ bao phủ mã nguồn*  
**Hệ thống áp dụng:** Module giỏ hàng và thanh toán của hệ thống bán hàng trực tuyến (Java 17, package `shop`)  
**Tài liệu nguồn:** [Đề xuất đề tài](../de-xuat-de-tai-1.md) · [Kế hoạch thực hiện](../ke-hoach-thuc-hien.md) · [Thiết kế kỹ thuật](../thiet-ke-ky-thuat.md) · [Nhật ký quyết định](../orchestration/decisions.md) · [Tổng quan tài liệu](../literature/README.md)

---

## Mục lục và trạng thái

| Chương | Tập tin | Tiểu mục | Phụ trách (theo kế hoạch) | Trạng thái |
|---|---|---|---|---|
| **Chương 1.** Giới thiệu | [chuong-1-gioi-thieu.md](chuong-1-gioi-thieu.md) | 1.1 Bối cảnh; 1.2 Vấn đề nghiên cứu; 1.3 Mục tiêu; 1.4 Câu hỏi nghiên cứu (RQ1–RQ6); 1.5 Đóng góp và cấu trúc báo cáo | Trưởng nhóm | Hoàn thành bản nháp (T15a) |
| **Chương 2.** Cơ sở lý thuyết và công trình liên quan | [chuong-2-co-so-ly-thuyet.md](chuong-2-co-so-ly-thuyet.md) | 2.1 Tiêu chí bao phủ (JaCoCo); 2.2 Kiểm thử đột biến; 2.3 Sinh test tự động truyền thống; 2.4 Sinh test bằng LLM; 2.5 Bài toán oracle; 2.6 Khoảng trống nghiên cứu | Tổng quan (Chung) | Hoàn thành bản nháp (T15a), đối chiếu 17/17 khóa `references.bib` |
| **Chương 3.** Phương pháp đề xuất | [chuong-3-phuong-phap.md](chuong-3-phuong-phap.md) | 3.1 Kiến trúc tổng thể; 3.2 P3 Direct LLM; 3.3 P4 Coverage-guided & P4b Mutation-feedback; 3.4 Thiết kế prompt; 3.5 Thực thi & phản hồi; 3.6 Lọc & chọn test | G4, G5 | Hoàn thành khung & thuật toán theo thiết kế (T15a); còn các mốc `[CHỜ]` cho `prompts/` và `tools/generate_p3.py`, `generate_p4.py` |
| **Chương 4.** Thiết kế thực nghiệm | [chuong-4-thiet-ke-thuc-nghiem.md](chuong-4-thiet-ke-thuc-nghiem.md) | 4.1 Chương trình đối tượng; 4.2 Môi trường; 4.3 Phương pháp đối chứng; 4.4 Chỉ số đánh giá; 4.5 Quy trình thực nghiệm | G1, G2, G3 | Hoàn thành (T15a); còn mốc `[CHỜ]` cho script tổng hợp T14 và `equivalent-mutants.csv` |
| **Chương 5.** Kết quả và thảo luận | `[CHỜ: T15b — chuong-5-ket-qua-va-thao-luan.md]` | 5.1 Kết quả sinh test; 5.2 Coverage; 5.3 Mutation; 5.4 So sánh phương pháp; 5.5 Trả lời RQ1–RQ6; 5.6 Threats to Validity | G1, G2, G4, G5 | Chưa viết (chờ số liệu thực nghiệm chính thức từ T14) |
| **Chương 6.** Kết luận và hướng phát triển | `[CHỜ: T15b — chuong-6-ket-luan.md]` | 6.1 Kết luận; 6.2 Hạn chế; 6.3 Hướng phát triển | Trưởng nhóm | Chưa viết (chờ kết quả Chương 5) |

---

## Danh sách hình và sơ đồ (`figures/`)

| Hình | Tập tin | Nội dung |
|---|---|---|
| Hình 3.1 | [figures/kien-truc-tong-the.md](figures/kien-truc-tong-the.md) | Kiến trúc tổng thể hệ thống sinh, lọc, đo lường và phản hồi |
| Hình 3.2 | [figures/vong-lap-p4-p4b.md](figures/vong-lap-p4-p4b.md) | Thiết kế ghép cặp P3 → P4 → P4b và vòng lặp phản hồi |
| Hình 3.3 | [figures/quy-trinh-loc-va-do.md](figures/quy-trinh-loc-va-do.md) | Quy trình lọc test bốn tầng và kiểm chứng tính đúng oracle trên `reference/` |

---

## Tài liệu tham khảo và quy ước trích dẫn

- Danh mục tài liệu tham khảo chuẩn hóa nằm tại [docs/literature/references.bib](../literature/references.bib) (17 công trình đã xác minh DOI/ấn phẩm).
- Trong toàn bộ báo cáo, trích dẫn sử dụng khóa trong ngoặc vuông theo đúng định danh BibTeX, ví dụ `[schafer-2024]`, `[pizzorno-2025]`.
- Chương 1–4 **không chứa số liệu kết quả thực nghiệm**; các vị trí phụ thuộc thành phần chưa hoàn tất trên nhánh hiện tại được đánh dấu tường minh bằng `[CHỜ: <nguồn>]` để cập nhật ở đợt T15b.
