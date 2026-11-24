package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_037Test {

    @Test
    void testPricingServiceConstructorAndNullCoupon() {
        PricingService pricing = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        assertEquals(0L, pricing.calculateDiscount(0L, null, today));
        assertEquals(0L, pricing.calculateDiscount(500000L, null, today));
    }
}
