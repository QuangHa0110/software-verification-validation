# Rà soát mutant còn sống và mutant tương đương

Người rà soát: orchestrator (vai trò G1). Mỗi mutant còn sống được xét thủ công, đối chiếu mã nguồn `subject/` với đặc tả `docs/spec/`. Chỉ mutant **không có đầu vào hợp lệ nào phân biệt được** với chương trình gốc mới được đưa vào `equivalent-mutants.csv`. Lệnh liệt kê: `python tools/mutation_ids.py --suite-results results/mutation/<phương pháp>/run-XX`.

## Đợt 1 – mutant còn sống của P1 run-01 (23/11/2026)

[ĐÃ CHẠY] `mutation_ids.py` trên `results/mutation/p1-manual/run-01` cho 2 mutant còn sống, cả hai đều `SURVIVED`.

| # | Vị trí | Mutant (index PIT) | Kết luận | Lý do |
|---|---|---|---|---|
| 1 | `ShippingCalculator.java:8`, `if (region == null \|\| weightGrams <= 0 \|\| orderValue < 0)` | `ConditionalsBoundaryMutator`, index 10 | **Không tương đương** | Mutant ở dòng này đổi một biên so sánh. P1 có ca `weightGrams = 0` (`ShippingTest.invalidInputBeforeFreeShipping`), nên biên `weightGrams <= 0` đã được kiểm; mutant còn sống là biên `orderValue < 0` → `orderValue <= 0`. Đầu vào `orderValue = 0` phân biệt được: đặc tả coi 0 là hợp lệ (`docs/spec/shipping.md`, ví dụ 1: nội thành 1 g, tiền hàng 0 → 15000), còn mutant ném `IllegalArgumentException`. Đây là **lỗ hổng của bộ P1** (thiếu ca tiền hàng bằng 0), giữ trong mẫu số |
| 2 | `PricingService.java:24`, `if (coupon.getMaxDiscount() > 0 && discount > coupon.getMaxDiscount())` | `ConditionalsBoundaryMutator`, index 85 | **Tương đương** | Hai mutant ở dòng này: index 80 (`getMaxDiscount() > 0`) đã bị diệt bởi ca PERCENT trần 0; index 85 đổi `discount > max` thành `discount >= max`. Khi `discount == max`, chương trình gốc trả `discount`, mutant trả `max`, hai giá trị bằng nhau; mọi trường hợp khác hai bên đi cùng nhánh. Không có đầu vào nào phân biệt được. Đã thêm vào `equivalent-mutants.csv` |

Mutant tương đương bị loại khỏi mẫu số cho **mọi** phương pháp (thiết kế 5.4); `analyze.py` tính lại score theo CSV hiện hành.

## Đợt sau

Sau khi có P3, P4, P4b: rà soát các mutant còn sống ở **mọi** phương pháp nhưng chưa xét ở đây, ưu tiên mutant còn sống ở tất cả các lần chạy.

## Đợt 2 – toàn bộ mutant còn sống của mọi phương pháp đã đo (24/11/2026)

Nguồn [ĐÃ CHẠY]: `tools/mutation_ids.py` trên P2 run-01..10 (66 mutant khác nhau còn sống ở ít nhất một lần), và trường `mutation.alive` của P3, P4, P4b run-01..10, P3r/P4r/P4br run-01..06, P3w/P4w run-01 (13 mutant khác nhau còn sống ở ít nhất một lần, gồm mutant index 85 đã biết).

Kết luận: **không có mutant tương đương mới.** Lý do: ngoại trừ `PricingService:24` index 85 (đã ghi ở đợt 1), **mọi** mutant còn sống ở các phương pháp trên đều đã bị diệt bởi ít nhất một bộ test khác:

- P1 run-01 diệt mọi mutant trừ index 85 và `ShippingCalculator:8` index 10 (đợt 1).
- `ShippingCalculator:8` index 10 bị diệt ở cả 10 lần chạy P3 (kiểm tra `mutation.alive` của P3 run-01..10: 0/10 lần còn sống).

Một mutant đã bị một test hợp lệ (xanh trên chương trình gốc và trên `reference/`) diệt thì có đầu vào phân biệt được nó với chương trình gốc, nên không tương đương. Vì vậy `equivalent-mutants.csv` giữ đúng một dòng. Các mutant còn sống ở P2 và P3w là **lỗ hổng của bộ test** (thiếu ca biên, thiếu kiểm tra trạng thái sau thao tác), không phải mutant tương đương.
