# KẾ HOẠCH THỰC HIỆN – ĐỀ TÀI 1: LLM-based Test Generation

**Hạn nộp:** 31/12/2026 · **Nhóm:** 5 người · **Bắt đầu:** 23/11/2026

Kế hoạch chia đề tài thành **5 gói việc (G1–G5)** cộng một số **việc chung**. Mỗi gói ghi rõ đầu vào, việc cần làm, sản phẩm bàn giao và phần báo cáo phụ trách. Trưởng nhóm tự giao gói cho từng thành viên ở mục 1.

---

## 1. Phân công

| Gói | Nội dung | Người phụ trách | Người review chéo |
|---|---|---|---|
| G1 | Chương trình đối tượng & đặc tả | | |
| G2 | Đo lường, thực nghiệm & thống kê | | |
| G3 | Các phương pháp đối chứng (P1, P2, EvoSuite) & oracle tham chiếu | | |
| G4 | Sinh test trực tiếp bằng LLM (P3) & bộ lọc test | | |
| G5 | Coverage-guided (P4) & phản hồi mutation | | |
| Chung | Tổng quan tài liệu (Chương 2) | | |
| Chung | Biên tập báo cáo, slide, kịch bản demo | | |

---

## 2. Các mốc

| Mốc | Hạn | Điều kiện đạt | Gói chính |
|---|---|---|---|
| **M1** | **25/11** | **Chốt đặc tả và chữ ký hàm** – từ đây G3, G4, G5 bắt đầu được | G1 |
| M2 | 29/11 | Chương trình chạy được; đo được coverage và mutation cho P1 bằng script chung. Đã chọn LLM, có API key; đã chốt quy ước chung (mục 6) | G1, G2, G3, trưởng nhóm |
| M3 | 06/12 | P2 và P3 chạy được qua script đo chung; oracle tham chiếu dùng được | G3, G4 |
| M4 | 13/12 | P4 và phản hồi mutation chạy được; EvoSuite xong | G5, G3 |
| **M5** | **20/12** | **Chạy đủ 10 lần, chốt số liệu** – sau mốc này không sửa code | G2 |
| M6 | 27/12 | Báo cáo đủ 6 chương; slide; tập demo 10 bước | Cả nhóm |
| Dự phòng | 28–31/12 | Rà soát theo bảng đối chiếu (mục 7), gắn tag `v1.0-final`, nộp | Cả nhóm |

**Theo tuần**

| Tuần | Thời gian | Trọng tâm |
|---|---|---|
| 1 | 23/11 – 29/11 | Đặc tả, chương trình, hạ tầng đo, P1, bắt đầu tổng quan |
| 2 | 30/11 – 06/12 | P2, oracle tham chiếu, P3, bộ lọc test |
| 3 | 07/12 – 13/12 | P4, phản hồi mutation, EvoSuite |
| 4 | 14/12 – 20/12 | Chạy thực nghiệm, xem xét mutant, chốt số liệu |
| 5 | 21/12 – 27/12 | Thống kê, biểu đồ, viết báo cáo, slide, demo |
| 6 | 28/12 – 31/12 | Dự phòng, rà soát, nộp |

## 3. Phụ thuộc giữa các gói

```
G1 đặc tả (M1 25/11) ──┬─→ G3: P1, P2, oracle tham chiếu ──┐
                       ├─→ G4: P3, bộ lọc ─────────────────┼─→ G5: P4 (dùng lại code gọi LLM + bộ lọc của G4)
                       └─→ G2: script đo ──────────────────┴─→ dùng cho mọi gói
```

- G3, G4, G5 **không cần chờ chương trình chạy được**: chỉ cần đặc tả và chữ ký hàm đã chốt ở M1.
- G5 bắt đầu tuần 1–2 bằng phần không cần LLM: đọc báo cáo JaCoCo, trích nhánh chưa phủ, dựng khung vòng lặp.

---

## 4. Gói việc

### G1 – Chương trình đối tượng & đặc tả

**Đầu vào:** đề xuất đề tài ([de-xuat-de-tai-1.md](de-xuat-de-tai-1.md)), mục 4.

- [ ] Liệt kê chức năng, chữ ký hàm, kiểu dữ liệu, ngoại lệ → **chốt trước 25/11 (M1)**
- [ ] Viết đặc tả đầu vào/đầu ra cho từng hàm trong `docs/spec/`: điều kiện hợp lệ, giá trị biên, ngoại lệ, ví dụ
- [ ] Viết module giỏ hàng & thanh toán trong `subject/src/main/java/`: tối thiểu 5 chức năng, có if/else lồng nhau, switch, vòng lặp, xử lý ngoại lệ
- [ ] Đảm bảo có các **nhánh khó phủ** (điều kiện kết hợp nhiều biến, giá trị biên) để P4 có chỗ cải thiện so với P3
- [ ] Lập bảng các hàm và nhánh cần kiểm thử
- [ ] Tuần 4: cùng G2 **xem xét thủ công các mutant còn sống**, đánh dấu mutant tương đương, ghi lý do
- [ ] Tuần 4: **giải thích nguyên nhân các nhánh không phủ được** và đề xuất cách sinh test bổ sung

**Bàn giao:** `subject/src/main/java/`, `docs/spec/`, bảng hàm và nhánh, danh sách mutant tương đương (`results/mutation/`).
**Báo cáo:** 4.1 Subject Programs; một phần 5.3.

### G2 – Đo lường, thực nghiệm & thống kê

- [ ] `subject/pom.xml`: Java 17, JUnit 5, JaCoCo, PIT
- [ ] **Đối chiếu 5 toán tử đột biến của đề với mutator của PIT**: lập bảng ánh xạ; toán tử nào PIT không có thì cùng G1 tạo mutant thủ công
- [ ] Script đo chung: nhận một bộ test → chép vào `subject/src/test/java/` → chạy → xuất **statement/line, branch, function (method) coverage, mutation score, số test, thời gian chạy** ra CSV/JSON
- [ ] Mutation score tính theo đúng đề: `Killed / Non-equivalent`; mutant không được chạy tới vẫn nằm trong mẫu số
- [ ] Trích **danh sách nhánh chưa phủ** từ báo cáo JaCoCo (dùng chung với G5)
- [ ] Script chạy toàn bộ thực nghiệm: mỗi phương pháp có yếu tố ngẫu nhiên (P2, P3, P4, EvoSuite) chạy **10 lần**
- [ ] Tổng hợp: trung bình, độ lệch chuẩn; kiểm định **Mann–Whitney U** và **Vargha–Delaney Â12**; bảng và biểu đồ
- [ ] Bảng **coverage trước và sau** khi áp dụng coverage-guided
- [ ] Chủ trì việc **chốt số liệu (M5, 20/12)**

**Bàn giao:** `subject/pom.xml`, `experiments/scripts/`, `experiments/config/`, `results/coverage/`, `results/mutation/`, `results/comparison/`.
**Báo cáo:** 4.2 Experimental Environment, 4.4 Evaluation Metrics, 4.5 Experimental Procedures, 5.2, 5.3, 5.4; dẫn dắt 5.5.

### G3 – Các phương pháp đối chứng & oracle tham chiếu

- [ ] **P1 – Manual Testing:** viết test JUnit theo đặc tả (không nhìn code) → `test-suites/p1-manual/`. Đây cũng là bộ test chuẩn để đối chiếu
- [ ] **Oracle tham chiếu:** cài đặt lại các quy tắc tính tiền **độc lập từ đặc tả** (người viết không phải tác giả G1) để tính expected output; dùng cho P2 và để kiểm tra oracle của test LLM
- [ ] **P2 – Random Testing:** bộ sinh đầu vào ngẫu nhiên có seed cố định, oracle lấy từ oracle tham chiếu → `test-suites/p2-random/run-XX/`
- [ ] **EvoSuite** (đối chứng thêm): kiểm tra tương thích phiên bản Java; nếu không chạy được thì ghi lý do vào báo cáo
- [ ] Ghi số test, thời gian sinh của từng phương pháp

**Bàn giao:** `test-suites/p1-manual/`, `test-suites/p2-random/`, `tools/` (bộ sinh ngẫu nhiên, oracle tham chiếu), kết quả EvoSuite.
**Báo cáo:** 4.3 Baseline Methods.

### G4 – Sinh test trực tiếp bằng LLM (P3) & bộ lọc test

- [ ] Code gọi API LLM, đọc key từ `.env`, **đổi được nhà cung cấp/model qua file cấu hình**
- [ ] Ghi log mỗi lần gọi: prompt, phản hồi, model, mức effort/nhiệt độ (nếu model hỗ trợ), số token vào/ra, chi phí, thời gian → `results/llm-logs/`
- [ ] Bộ prompt cho P3 → `prompts/direct/`
- [ ] **Bộ lọc test** (G5 dùng lại): biên dịch được → chạy được trên chương trình gốc → oracle khớp oracle tham chiếu → không trùng lặp. Ghi lại test bị loại và lý do
- [ ] Đo **tỷ lệ test hợp lệ** và **tỷ lệ oracle đúng** – bằng chứng cho yêu cầu "chứng minh tính đúng đắn của test oracle"
- [ ] Sinh P3, 10 lần → `test-suites/p3-direct-llm/run-XX/`

**Bàn giao:** `tools/` (phần LLM và bộ lọc), `prompts/direct/`, `test-suites/p3-direct-llm/`, `results/llm-logs/`.
**Báo cáo:** 3.2 Direct LLM-based Test Generation, 3.4 Prompt Design, 3.6 Test Case Selection and Optimization; 5.1 (phần P3).

### G5 – Coverage-guided (P4) & phản hồi mutation

- [ ] Tuần 1–2 (chưa cần LLM): trích nhánh chưa phủ và điều kiện nhánh từ báo cáo JaCoCo, dựng khung vòng lặp
- [ ] Vòng lặp P4 theo thuật toán gợi ý trong đề: sinh → chạy → đo → nhánh chưa phủ → prompt bổ sung → lọc → gộp; dừng khi đạt ngưỡng coverage hoặc hết số vòng / ngân sách token
- [ ] **Cải tiến:** đưa mutant còn sống từ PIT vào prompt để sinh test diệt mutant
- [ ] Bộ prompt → `prompts/coverage-guided/`, `prompts/mutation-feedback/`
- [ ] Ghi coverage sau từng vòng lặp (để vẽ đường cải thiện)
- [ ] Sinh P4, 10 lần → `test-suites/p4-coverage-guided/run-XX/`
- [ ] **Chế độ demo** chạy nhanh (ít vòng) để trình diễn trực tiếp

**Bàn giao:** `tools/` (vòng lặp), `prompts/coverage-guided/`, `prompts/mutation-feedback/`, `test-suites/p4-coverage-guided/`.
**Báo cáo:** 3.1 Overall Architecture, 3.3 Coverage-guided Test Generation, 3.5 Test Execution and Feedback Mechanism; 5.1 (phần P4).

---

## 5. Việc chung

### Tổng quan tài liệu (tuần 1–3)
- [ ] Đọc và phân tích **ít nhất 5 công trình**, ưu tiên 5 năm gần đây (danh sách sơ bộ ở mục 11 của đề xuất)
- [ ] Chỉ ra hạn chế của các phương pháp hiện có và vấn đề nhóm giải quyết
- [ ] **Kiểm tra lại năm, nơi công bố** của từng tài liệu trước khi đưa vào báo cáo
- [ ] Ghi chú trong `docs/literature/`

**Báo cáo:** Chương 2 (2.1–2.6).

### Báo cáo, slide, demo (tuần 5)
- [ ] Ghép báo cáo theo đúng cấu trúc 6 chương của đề (bảng phân công từng mục ở dưới)
- [ ] Trả lời **RQ1–RQ6 bằng số liệu, bảng biểu**
- [ ] Viết 5.6 Threats to Validity, 6.2 Limitations: nêu rõ cả những tiêu chí LLM **không** hơn phương pháp khác
- [ ] Slide và kịch bản demo 10 bước (mục 8)

| Chương | Mục | Phụ trách |
|---|---|---|
| 1. Introduction | 1.1–1.5 | Trưởng nhóm |
| 2. Background and Related Work | 2.1–2.6 | Người làm tổng quan |
| 3. Proposed Approach | 3.1, 3.3, 3.5 | G5 |
| | 3.2, 3.4, 3.6 | G4 |
| 4. Experimental Design | 4.1 | G1 |
| | 4.2, 4.4, 4.5 | G2 |
| | 4.3 | G3 |
| 5. Results and Discussion | 5.1 | G4, G5 |
| | 5.2, 5.3, 5.4 | G2 (G1 hỗ trợ 5.3) |
| | 5.5, 5.6 | Cả nhóm, G2 dẫn dắt |
| 6. Conclusion and Future Work | 6.1–6.3 | Trưởng nhóm |

---

## 6. Quy ước chung (chốt trước 29/11)

- **Định dạng kết quả:** G2 công bố cấu trúc file CSV/JSON và cách gọi script đo; mọi gói theo chuẩn này
- **Đặt tên:** bộ test lưu tại `test-suites/<phương pháp>/run-XX/`; log LLM tại `results/llm-logs/<phương pháp>/run-XX/`
- **Tái lập:** mọi tham số (model, effort, số vòng lặp, ngưỡng coverage, ngân sách token, seed) nằm trong `experiments/config/`, không viết cứng trong code
- **Bảo mật:** API key chỉ nằm trong `.env`, không commit, không gửi qua chat nhóm
- **Git:** mỗi gói một nhánh (`feature/...`); gộp vào `main` qua pull request, **người review chéo** duyệt trước khi gộp; commit theo quy ước trong README
- **Trung thực số liệu:** không sửa tay test do LLM sinh; không dùng số liệu giả lập; kết quả demo phải khớp báo cáo

---

## 7. Bảng đối chiếu yêu cầu của đề

| Yêu cầu của đề | Gói | Bằng chứng |
|---|---|---|
| NV1: cơ sở lý thuyết, ≥ 5 công trình, hạn chế, vấn đề nghiên cứu | Chung | Chương 2 |
| NV2: chương trình có mã nguồn, 3–5+ hàm, rẽ nhánh/vòng lặp/ngoại lệ, đặc tả, bộ test chuẩn | G1, G3 | `subject/`, `docs/spec/`, P1 |
| NV3: ≥ 1 LLM, ≥ 2 chiến lược, bộ prompt, test sinh tự động, cơ chế thực thi và phản hồi coverage, script tái lập | G4, G5 | `prompts/`, `tools/`, `test-suites/p3`, `p4` |
| NV4: statement, branch, function coverage; danh sách nhánh chưa phủ; phân tích nguyên nhân; trước/sau coverage-guided | G2, G1, G5 | `results/coverage/`, 5.2 |
| NV5: bộ mutant, 5 toán tử, mutant tương đương và không được chạy, mutation score từng phương pháp, phân tích lỗi không phát hiện | G2, G1 | `results/mutation/`, 5.3 |
| NV6: P1–P4 cùng chương trình, cùng điều kiện; 8 tiêu chí; nhiều lần chạy; ghi cấu hình model, prompt, nhiệt độ, token | G2, G3, G4 | `results/comparison/`, `experiments/config/`, `results/llm-logs/` |
| RQ1–RQ6 trả lời bằng số liệu | Cả nhóm | 5.5 |
| 9 sản phẩm bắt buộc | Cả nhóm | Các thư mục tương ứng trong repo |
| Báo cáo đúng cấu trúc 6 chương | Cả nhóm | `docs/report/` |
| Demo trực tiếp 10 bước, chạy lại được | G5, G2 | Kịch bản demo (mục 8) |
| Chứng minh tính đúng của oracle và tính hợp lệ của test | G3, G4 | Oracle tham chiếu, tỷ lệ test hợp lệ / oracle đúng |
| Có cải tiến phương pháp, không chỉ dùng prompt có sẵn | G5 | Phản hồi mutation, bộ lọc test |
| Không mặc định LLM tốt hơn | Cả nhóm | 5.4–5.6, 6.2 |

**Thang điểm:** tổng quan 1 · thiết kế phương pháp 2 · công cụ & thực nghiệm 2 · coverage & mutation 2 · so sánh & thảo luận 2 · báo cáo & tái lập 1.

---

## 8. Kịch bản demo (theo mục 10 của đề)

| Bước | Nội dung | Phụ trách |
|---|---|---|
| 1 | Giới thiệu chương trình đối tượng | G1 |
| 2 | Dùng LLM sinh test case | G4 |
| 3 | Chạy test bằng JUnit | G2 |
| 4 | Hiển thị báo cáo coverage ban đầu | G2 |
| 5 | Chỉ ra các nhánh chưa được kiểm thử | G5 |
| 6 | Đưa thông tin coverage cho LLM sinh test bổ sung | G5 |
| 7 | Chạy lại test, hiển thị coverage mới | G5 |
| 8 | Kết quả mutation testing | G2 |
| 9 | So sánh kết quả giữa các phương pháp | G2 |
| 10 | Hạn chế và đóng góp của phương pháp | Trưởng nhóm |

---

## 9. Quyết định cần chốt

| Việc | Hạn | Người quyết |
|---|---|---|
| Giao gói cho từng thành viên (mục 1) | 22/11 | Trưởng nhóm |
| Chọn LLM, model cụ thể, có API key và giới hạn chi tiêu | 29/11 | Trưởng nhóm |
| Ngưỡng coverage và số vòng lặp tối đa của P4, ngân sách token | 06/12 | G5, G2 |
