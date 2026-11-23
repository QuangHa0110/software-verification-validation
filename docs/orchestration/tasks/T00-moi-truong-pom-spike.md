# T00 – Môi trường, `subject/pom.xml`, spike S1 và S2

**Worker:** codex, effort `high` · **Nhánh:** `feature/measurement` · **Thư mục làm việc:** gốc repo

Đọc trước: `docs/orchestration/worker-rules.md` (bắt buộc), `docs/thiet-ke-ky-thuat.md` mục 1, 2, 5.3, 7.

## Phạm vi (chỉ được tạo/sửa các file này)

- `.gitignore`
- `requirements.txt`
- `subject/pom.xml`
- `experiments/spikes/s1-s2/**` (dự án Maven nhỏ dùng để thử, kèm file kết quả)

Không tạo mã Java trong `subject/src/`: việc đó thuộc bước khác.

## Việc cần làm

1. **`.gitignore`**: thêm (giữ nguyên các dòng cũ) `.work/`, `.m2repo/`, `__pycache__/`, `*.pyc`, `.pytest_cache/`, `.venv/`.
2. **`requirements.txt`**: ghim **đúng phiên bản** (`==`) cho `anthropic`, `pyyaml`, `scipy`, `numpy`, `matplotlib`, `pytest`, `jsonschema`. Chọn bản ổn định mới nhất cài được trên Python 3.12. Cài bằng `python -m pip install -r requirements.txt`, rồi kiểm tra `python -c "import anthropic, yaml, scipy, numpy, matplotlib, pytest, jsonschema; print('ok')"`.
3. **`subject/pom.xml`**: dự án Maven, `groupId` `vn.svv`, `artifactId` `subject`, package Java là `shop` (chưa có lớp nào).
   - `maven.compiler.release=17`; `project.build.sourceEncoding` và `project.reporting.outputEncoding` là `UTF-8`.
   - Ghim đúng phiên bản: `junit-jupiter` (scope test), `maven-compiler-plugin`, `maven-surefire-plugin`, `jacoco-maven-plugin`, `pitest-maven`, `pitest-junit5-plugin`.
   - JaCoCo: execution `prepare-agent`, và execution `report` gắn với phase `test`, để `mvn test` sinh ra `target/site/jacoco/jacoco.xml`. Lệnh `mvn test jacoco:report` trong README cũng phải chạy được.
   - PIT: `targetClasses` = `shop.*`, `targetTests` = `shop.*`, `outputFormats` = `XML,HTML`, `timestampedReports=false`, `failWhenNoMutations=false`. Mutators và `timeoutFactor` lấy từ property Maven (`pit.mutators`, mặc định `DEFAULTS`; `pit.timeoutFactor`, mặc định `3`) để đổi được từ dòng lệnh.
   - Kiểm tra: `mvn -B -q -f subject/pom.xml test` exit 0 khi chưa có mã nguồn.
4. **Spike S1** trong `experiments/spikes/s1-s2/`: một dự án Maven nhỏ, **cấu hình plugin y hệt `subject/pom.xml`**, package `shop`, có một lớp mẫu và test JUnit 5. Chạy `mvn -B test` và `mvn -B org.pitest:pitest-maven:mutationCoverage`. Xác nhận có `target/site/jacoco/jacoco.xml` và `target/pit-reports/mutations.xml`.
5. **Spike S2**: lớp mẫu phải chứa, mỗi cấu trúc trên **một dòng riêng có chú thích**: so sánh `>`, phép `+`, điều kiện `&&`, hằng `true` gán cho biến boolean, lời gọi hàm `void`, câu lệnh gán bình thường, `return` một giá trị boolean. Chạy PIT với `-Dpit.mutators=ALL`. Từ `mutations.xml`, lập bảng: mỗi dòng/cấu trúc → các mutator PIT đã sinh mutant tại đó, kèm `description` PIT ghi.
6. Ghi **`experiments/spikes/s1-s2/KET-QUA.md`** bằng tiếng Việt, gồm:
   - Phiên bản JDK, Maven, JUnit, JaCoCo, PIT, pitest-junit5-plugin đã dùng (lấy từ output thật).
   - Lệnh đã chạy và trích output thật.
   - Bảng ánh xạ 5 toán tử của đề sang mutator PIT: (1) thay toán tử so sánh `>` thành `>=`; (2) thay toán tử số học `+` thành `-`; (3) thay toán tử điều kiện `&&` thành `||`; (4) thay hằng boolean `true` thành `false`; (5) xóa câu lệnh. Mỗi toán tử ghi: mutator PIT tương ứng, **"có / có một phần / không"** và bằng chứng (dòng nào trong `mutations.xml`). Chỉ kết luận từ output đã chạy.
   - Đề xuất danh sách mutator cho `pit.mutators` của thực nghiệm, và toán tử nào cần tự tạo mutant bằng `tools/mutate_custom.py`.

## Tiêu chí xong

- `mvn -B -q -f subject/pom.xml test` exit 0.
- Spike sinh được `jacoco.xml` và `mutations.xml`.
- `KET-QUA.md` có bảng 5 toán tử, kết luận dựa trên output thật.
- Import Python ra `ok`.
- Không có thư mục `target/` hay `.m2repo/` nào bị git theo dõi (`git status --porcelain` không liệt kê chúng).
