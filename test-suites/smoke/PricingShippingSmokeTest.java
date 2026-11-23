package shop;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PricingShippingSmokeTest {
    private final LocalDate today = LocalDate.of(2026, 11, 23);
    private final PricingService pricing = new PricingService();
    private final ShippingCalculator shipping = new ShippingCalculator();

    @Test
    void percentageCouponAppliesOnExpiryDateAndRespectsCap() {
        Coupon coupon = new Coupon("Sale", CouponType.PERCENT, 15, 99_999, 10_000, today);
        assertEquals(10_000, pricing.calculateDiscount(99_999, coupon, today));
        assertEquals(0, pricing.calculateDiscount(0, null, today));
    }

    @Test
    void invalidPricingRequestAndIneligibleCouponsAreRejectedInOrder() {
        Coupon coupon = new Coupon("Sale", CouponType.FIXED, 100, 300, 0, today);
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(0, null, null));
        assertThrows(CouponExpiredException.class,
                () -> pricing.calculateDiscount(100, coupon, today.plusDays(1)));
        assertThrows(CouponNotApplicableException.class, () -> pricing.calculateDiscount(100, coupon, today));
    }

    @Test
    void shippingUsesStartedWeightBlocksAndChecksWeightBeforeFreeShipping() {
        assertEquals(39_000, shipping.calculate(Region.PROVINCE, 1_501, 500_000));
        assertEquals(0, shipping.calculate(Region.SUBURB, 20_000, 500_000));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.SUBURB, 20_001, 500_000));
    }
}
