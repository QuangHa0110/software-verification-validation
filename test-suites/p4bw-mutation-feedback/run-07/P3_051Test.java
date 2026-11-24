package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_051Test {

    @Test
    void pricingServiceCalculateDiscountSubtotalNegativeThrowsException() {
        PricingService pricingService = new PricingService();
        LocalDate today = LocalDate.of(2026, 11, 23);
        assertThrows(IllegalArgumentException.class, () -> pricingService.calculateDiscount(-1L, null, today));
    }
}
