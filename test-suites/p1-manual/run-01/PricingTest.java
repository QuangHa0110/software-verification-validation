package shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static shop.DomainTest.DAY;
import static shop.DomainTest.expectError;

class PricingTest {
    private final PricingService pricing = new PricingService();

    private Coupon percent(long cap) {
        return new Coupon("P", CouponType.PERCENT, 15, 99_999, cap, DAY);
    }

    @Test
    void negativeSubtotalPrecedesMissingCoupon() {
        // -1 < 0 ở bước 1, chưa được trả 0 cho mã null.
        expectError(IllegalArgumentException.class, () -> pricing.calculateDiscount(-1, null, DAY));
    }

    @Test
    void dateRequiredEvenWithoutCoupon() {
        // Tạm tính 0 hợp lệ nhưng ngày null ở bước 1.
        expectError(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 100})
    void noCouponDiscountsNothing(long subtotal) {
        // Với ngày hợp lệ và không mã, mức giảm là 0 VND cho cả hai tạm tính.
        assertEquals(0L, pricing.calculateDiscount(subtotal, null, DAY));
    }

    @Test
    void expiryPrecedesMinimum() {
        Coupon c = percent(10_000);
        // Sau hạn 1 ngày và dưới ngưỡng 1 VND, lỗi hết hạn xảy ra trước.
        expectError(CouponExpiredException.class, () -> pricing.calculateDiscount(99_998, c, DAY.plusDays(1)));
        // Dùng lại cùng mã đúng ngày/đủ tiền: 99999 × 15 / 100 → 14999, cắt còn 10000.
        assertEquals(10_000L, pricing.calculateDiscount(99_999, c, DAY));
    }

    @Test
    void liveCouponOneBelowMinimum() {
        Coupon c = percent(0);
        // Đúng hạn nhưng 99998 = 99999 - 1.
        expectError(CouponNotApplicableException.class, () -> pricing.calculateDiscount(99_998, c, DAY));
    }

    @Test
    void dayBeforeExpiryAndAboveMinimum() {
        // 100000 > 99999 và sớm hơn hạn 1 ngày: 100000 × 15 / 100 = 15000.
        assertEquals(15_000L, pricing.calculateDiscount(100_000, percent(0), DAY.minusDays(1)));
    }

    @ParameterizedTest
    @CsvSource({"0,14999", "14999,14999", "10000,10000", "15000,14999"})
    void percentCapAtMinimumOnExpiry(long cap, long expected) {
        // Đúng minOrder và expiry: 99999 × 15 / 100 → 14999. Trần 0/bằng/thấp/cao cho kết quả trong bảng.
        assertEquals(expected, pricing.calculateDiscount(99_999, percent(cap), DAY));
    }

    @Test
    void percentMultiplicationUsesLong() {
        Coupon c = new Coupon("P", CouponType.PERCENT, 80, 0, 0, DAY);
        // 3000000000 × 80 / 100 = 2400000000, vượt int.
        assertEquals(2_400_000_000L, pricing.calculateDiscount(3_000_000_000L, c, DAY));
    }

    @Test
    void percentCanRoundDownToZero() {
        Coupon c = new Coupon("P", CouponType.PERCENT, 1, 0, 0, DAY);
        // 7 × 1 / 100 lấy phần nguyên bằng 0.
        assertEquals(0L, pricing.calculateDiscount(7, c, DAY));
    }

    @ParameterizedTest
    @CsvSource({"0,0", "399,399", "400,400", "401,400"})
    void fixedIgnoresCapButCannotExceedSubtotal(long subtotal, long expected) {
        Coupon c = new Coupon("F", CouponType.FIXED, 400, 0, 1, DAY);
        // min(400, 0/399/400/401) = 0/399/400/400; trần 1 không tham gia.
        assertEquals(expected, pricing.calculateDiscount(subtotal, c, DAY));
    }
}
