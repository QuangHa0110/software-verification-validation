package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_009Test {

    @Test
    void killPricingServiceCalculateDiscountSubtotalZeroBoundary() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        // subtotal == 0 is valid and should not throw IllegalArgumentException
        long discount = pricingService.calculateDiscount(0L, null, today);
        assertEquals(0L, discount);
    }
}
