# Kết quả S1 và S2 trên JDK 17

Ngày chạy 23/11/2026, trên Windows 11 x64. Hai test JUnit 5 của spike chạy xanh. PIT sinh 9 mutant với `DEFAULTS` và 20 mutant với `ALL`. Bảng dưới đây dùng lần chạy `ALL`. [Mã mẫu](src/main/java/shop/OperatorProbe.java) · [XML gốc của lần chạy ALL](mutations-all.xml)

## S2 — PIT thực hiện được những phép thay nào?

“Dòng Java” là giá trị `lineNumber` PIT ghi cho `OperatorProbe.java`. “Dòng XML” là số dòng vật lý trong bản sao nguyên văn của `target/pit-reports/mutations.xml`. Mỗi phần tử `mutation` chiếm một dòng.

| Toán tử của đề | Mức hỗ trợ trên mẫu | Mutator PIT | Bằng chứng |
|---|---|---|---|
| 1. `>` → `>=` | có | `CONDITIONALS_BOUNDARY` | Java 7, XML 3 ghi `changed conditional boundary` tại phép so sánh duy nhất `left > right`. Test với hai toán hạng bằng nhau diệt mutant này. |
| 2. `+` → `-` | có | `MATH` | Java 8, XML 8 ghi rõ `Replaced integer addition with subtraction`. |
| 3. `&&` → `\|\|` | không | Không có mutant khớp | Java 10, XML 10–15. Sáu mutant đảo hoặc ép kết quả của từng phép kiểm tra riêng lẻ tại hai chỉ số PIT 27 và 29. Không mutant nào thực hiện phép OR ngắn mạch. |
| 4. Hằng `true` → `false` | có | `INLINE_CONSTS` | Java 9, XML 6 ghi `Substituted 1 with 0` ngay tại `boolean accepted = true`. Hai mutant đổi giá trị trả về nằm riêng ở Java 15, XML 19–20. |
| 5. Xóa câu lệnh | có một phần | `VOID_METHOD_CALLS`, `EXPERIMENTAL_MEMBER_VARIABLE` | XML 18 xóa lời gọi `record(sum)` ở Java 11. XML 21 xóa phép gán trường `recorded = value` ở Java 19. Với phép gán cục bộ ở Java 13, XML 7 chỉ thay hằng bên phải. |

### Các mutant theo dòng mã nguồn

Cột mutator giữ phần tên cuối của trường `mutator` trong XML. Hai số dòng XML trong cùng một hàng là hai mutant có cùng mô tả.

| Dòng Java và cấu trúc | Dòng XML | Mutator | `description` nguyên văn |
|---|---:|---|---|
| 7 · `left > right` | 3 | `ConditionalsBoundaryMutator` | `changed conditional boundary` |
| 7 · giá trị boolean của so sánh | 4 | `InlineConstantMutator` | `Substituted 1 with 0` |
| 7 · giá trị boolean của so sánh | 5 | `InlineConstantMutator` | `Substituted 0 with 1` |
| 7 · `left > right` | 9 | `NegateConditionalsMutator` | `negated conditional` |
| 7 · `left > right` | 16 | `RemoveConditionalMutator_ORDER_ELSE` | `removed conditional - replaced comparison check with false` |
| 7 · `left > right` | 17 | `RemoveConditionalMutator_ORDER_IF` | `removed conditional - replaced comparison check with true` |
| 8 · `left + right` | 8 | `MathMutator` | `Replaced integer addition with subtraction` |
| 9 · `boolean accepted = true` | 6 | `InlineConstantMutator` | `Substituted 1 with 0` |
| 10 · `greater && enabled` | 10, 11 | `NegateConditionalsMutator` | `negated conditional` |
| 10 · `greater && enabled` | 12, 13 | `RemoveConditionalMutator_EQUAL_ELSE` | `removed conditional - replaced equality check with false` |
| 10 · `greater && enabled` | 14, 15 | `RemoveConditionalMutator_EQUAL_IF` | `removed conditional - replaced equality check with true` |
| 11 · `record(sum)` | 18 | `VoidMethodCallMutator` | `removed call to shop/OperatorProbe::record` |
| 13 · `accepted = false` | 7 | `InlineConstantMutator` | `Substituted 0 with 1` |
| 15 · `return accepted` | 19 | `BooleanFalseReturnValsMutator` | `replaced boolean return with false for shop/OperatorProbe::evaluate` |
| 15 · `return accepted` | 20 | `BooleanTrueReturnValsMutator` | `replaced boolean return with true for shop/OperatorProbe::evaluate` |
| 19 · `recorded = value` | 21 | `MemberVariableMutator` | `Removed assignment to member variable recorded` |
| 23 · `return recorded` | 22 | `PrimitiveReturnsMutator` | `replaced int return with 0 for shop/OperatorProbe::recorded` |

### Cấu hình đề xuất cho thực nghiệm

Dùng `pit.mutators=CONDITIONALS_BOUNDARY,MATH,INLINE_CONSTS,VOID_METHOD_CALLS,EXPERIMENTAL_MEMBER_VARIABLE`. Lần chạy với cấu hình này sinh 8 mutant. Trên mẫu, 5 mutant khớp phép biến đổi của đề (XML của lần `ALL`, dòng 3, 6, 8, 18, 21). Khi thống kê, đối chiếu dòng mã, toán tử nguồn và mô tả. Cụ thể, loại các biến đổi hằng do phép so sánh sinh ra ở Java 7 và phép đổi ngược `false` → `true` ở Java 13. Chỉ lọc theo tên `INLINE_CONSTS` sẽ đếm cả ba mutant này.

Giao `tools/mutate_custom.py` thực hiện `&&` → `||` tại mã nguồn và xóa các câu lệnh đủ điều kiện mà PIT chưa xóa, chẳng hạn `accepted = false;` ở Java 13. Mỗi vị trí tạo một mutant. Loại các vị trí xóa đã có mutant PIT để tránh đếm hai lần. Với phép đổi hằng `true` ở Java 9, PIT đã sinh đúng mutant.

## S1 — môi trường, lệnh và output

Các lệnh sau dùng PowerShell. Tại gốc repo, `java -version` và `mvn -B --version` ghi nhận môi trường sau.

```text
java version "17.0.12" 2024-07-16 LTS
Apache Maven 3.9.15 (98b2cdbfdb5f1ac8781f537ea9acccaed7922349)
Java version: 17.0.12, vendor: Oracle Corporation, runtime: <JAVA_HOME>
```

| Thành phần | Phiên bản | Dấu vết thực thi |
|---|---|---|
| JUnit Jupiter | 5.13.4 | Classpath trong báo cáo Surefire `target/surefire-reports/TEST-shop.OperatorProbeTest.xml` chứa `junit-jupiter-5.13.4.jar`. |
| Maven Compiler / Surefire | 3.14.1 / 3.5.4 | Log `mvn -B test` ghi `compiler:3.14.1:compile` và `surefire:3.5.4:test`. |
| JaCoCo | 0.8.14 | Log pha test ghi `jacoco:0.8.14:prepare-agent` và `jacoco:0.8.14:report`. |
| PIT | 1.20.3 | Log mutation ghi `pitest:1.20.3:mutationCoverage`. |
| pitest-junit5-plugin | 1.2.3 | Log `mvn -B -X '-Dpit.mutators=ALL' org.pitest:pitest-maven:mutationCoverage` ghi `[DEBUG]   Included: org.pitest:pitest-junit5-plugin:jar:1.2.3`. |

Tại gốc repo, `mvn -B -q -f subject/pom.xml test` thoát 0, không in output. Lệnh `mvn -B -f subject/pom.xml test jacoco:report` cũng thoát 0. Vì `subject` chưa có lớp hay test, JaCoCo ghi `[INFO] Skipping JaCoCo execution due to missing execution data file.`

Tại `experiments/spikes/s1-s2/`, chạy các lệnh theo thứ tự bên dưới. Mỗi lệnh Maven thoát 0.

```powershell
mvn -B test
mvn -B org.pitest:pitest-maven:mutationCoverage
```

Hai lệnh lần lượt ghi nhận kết quả JUnit, JaCoCo và PIT mặc định.

```text
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jacoco:0.8.14:report (report) @ s1-s2 ---
[INFO] Analyzed bundle 's1-s2' with 1 classes
[INFO] BUILD SUCCESS

>> Line Coverage (for mutated classes only): 11/11 (100%)
>> Generated 9 mutations Killed 9 (100%)
[INFO] BUILD SUCCESS
```

Sau S1, `Test-Path` trả về `True` cho cả `target/site/jacoco/jacoco.xml` và `target/pit-reports/mutations.xml`. XML JaCoCo ghi LINE 11/11, BRANCH 6/6 và METHOD 4/4. Lệnh README `mvn -B test jacoco:report` trên spike cũng ghi `[INFO] BUILD SUCCESS`.

```powershell
mvn -B '-Dpit.mutators=ALL' org.pitest:pitest-maven:mutationCoverage
Copy-Item target/pit-reports/mutations.xml mutations-all.xml
mvn -B '-Dpit.mutators=CONDITIONALS_BOUNDARY,MATH,INLINE_CONSTS,VOID_METHOD_CALLS,EXPERIMENTAL_MEMBER_VARIABLE' '-Dpit.timeoutFactor=4' org.pitest:pitest-maven:mutationCoverage
```

Hai kết quả dưới đây lần lượt thuộc về `ALL` và danh sách đề xuất.

```text
>> Generated 20 mutations Killed 20 (100%)
>> Mutations with no coverage 0. Test strength 100%

>> Generated 8 mutations Killed 8 (100%)
>> Mutations with no coverage 0. Test strength 100%
```

Đối chiếu bytecode bằng `javap -c -l -p -classpath target/classes shop.OperatorProbe`. Dòng Java 9 bắt đầu tại offset 17 (`iconst_1`), tiếp theo là `istore`. Dòng Java 10 bắt đầu tại offset 20 và chứa hai lệnh rẽ nhánh `22: ifeq 38` và `26: ifeq 38`. Đây là hai phép kiểm tra mà PIT sửa riêng trong XML dòng 10–15.
