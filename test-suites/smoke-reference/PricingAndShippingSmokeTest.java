package shop;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PricingAndShippingSmokeTest {

    @Test
    void pricingServiceExamplesAndTable() {
        PricingService service1 = new PricingService();
        PricingService service2 = new PricingService();
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        LocalDate today = LocalDate.of(2026, 11, 23);

        // Constructor examples
        assertEquals(0L, service1.calculateDiscount(0, null, today));
        Coupon tenPct = new Coupon("P10", CouponType.PERCENT, 10, 0, 0, expiry);
        assertEquals(20L, service1.calculateDiscount(200, tenPct, today));
        assertEquals(20L, service2.calculateDiscount(200, tenPct, today));

        // Table rows 1-9 in pricing.md
        Coupon row1Coupon = new Coupon("C15", CouponType.PERCENT, 15, 99999, 10000, expiry);
        assertEquals(10000L, service1.calculateDiscount(99999, row1Coupon, today));

        Coupon row2Coupon = new Coupon("C15NC", CouponType.PERCENT, 15, 99999, 0, expiry);
        assertEquals(14999L, service1.calculateDiscount(99999, row2Coupon, today));

        Coupon row3Coupon = new Coupon("C10", CouponType.PERCENT, 10, 0, 10000, expiry);
        assertEquals(10000L, service1.calculateDiscount(100000, row3Coupon, today));

        Coupon row4Coupon = new Coupon("C1", CouponType.PERCENT, 1, 0, 0, expiry);
        assertEquals(0L, service1.calculateDiscount(7, row4Coupon, today));

        Coupon fixed400 = new Coupon("F400", CouponType.FIXED, 400, 0, 1, expiry);
        assertEquals(300L, service1.calculateDiscount(300, fixed400, today));
        assertEquals(0L, service1.calculateDiscount(0, fixed400, today));

        // Row 7: expired + below minOrder -> CouponExpiredException first
        assertThrows(CouponExpiredException.class,
                () -> service1.calculateDiscount(99998, row1Coupon, LocalDate.of(2026, 11, 24)));

        // Row 8: on expiry date, below minOrder -> CouponNotApplicableException
        assertThrows(CouponNotApplicableException.class,
                () -> service1.calculateDiscount(99998, row1Coupon, today));

        // Row 9: null today with null coupon -> IAE; valid today -> 0
        assertThrows(IllegalArgumentException.class,
                () -> service1.calculateDiscount(0, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> service1.calculateDiscount(-1, null, today));
        assertEquals(0L, service1.calculateDiscount(0, null, today));
    }

    @Test
    void shippingCalculatorExamplesAndTable() {
        ShippingCalculator calc1 = new ShippingCalculator();
        ShippingCalculator calc2 = new ShippingCalculator();

        // Constructor examples
        assertEquals(15000L, calc1.calculate(Region.INNER_CITY, 1, 0));
        assertEquals(0L, calc1.calculate(Region.SUBURB, 1, 500000));
        assertEquals(0L, calc2.calculate(Region.SUBURB, 1, 500000));

        // Weight table for INNER_CITY, orderValue 499999
        assertEquals(15000L, calc1.calculate(Region.INNER_CITY, 1, 499999));
        assertEquals(15000L, calc1.calculate(Region.INNER_CITY, 1000, 499999));
        assertEquals(17000L, calc1.calculate(Region.INNER_CITY, 1001, 499999));
        assertEquals(17000L, calc1.calculate(Region.INNER_CITY, 1500, 499999));
        assertEquals(19000L, calc1.calculate(Region.INNER_CITY, 1501, 499999));
        assertEquals(91000L, calc1.calculate(Region.INNER_CITY, 20000, 499999));
        assertThrows(IllegalArgumentException.class,
                () -> calc1.calculate(Region.INNER_CITY, 20001, 499999));

        // Region comparison at 1501g and 500000 VND
        assertEquals(0L, calc1.calculate(Region.SUBURB, 1501, 500000));
        assertEquals(39000L, calc1.calculate(Region.PROVINCE, 1501, 500000));
        assertEquals(59000L, calc1.calculate(Region.REMOTE, 1501, 500000));

        // Base fees for all regions below free threshold
        assertEquals(25000L, calc1.calculate(Region.SUBURB, 1000, 499999));
        assertEquals(35000L, calc1.calculate(Region.PROVINCE, 1000, 499999));
        assertEquals(55000L, calc1.calculate(Region.REMOTE, 1000, 499999));

        // Invalid inputs including weight > 20000 even when orderValue >= 500000
        assertThrows(IllegalArgumentException.class, () -> calc1.calculate(null, 100, 1000));
        assertThrows(IllegalArgumentException.class, () -> calc1.calculate(Region.INNER_CITY, 0, 1000));
        assertThrows(IllegalArgumentException.class, () -> calc1.calculate(Region.INNER_CITY, -5, 1000));
        assertThrows(IllegalArgumentException.class, () -> calc1.calculate(Region.INNER_CITY, 100, -1));
        assertThrows(IllegalArgumentException.class, () -> calc1.calculate(Region.INNER_CITY, 20001, 500000));
    }
}
