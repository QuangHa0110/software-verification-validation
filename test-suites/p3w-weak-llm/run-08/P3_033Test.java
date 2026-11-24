package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @Test
    void pricingServiceCalculateDiscountNullCouponReturnsZero() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        assertEquals(0L, pricingService.calculateDiscount(500000L, null, today));
        assertEquals(0L, pricingService.calculateDiscount(0L, null, today));
    }
}
