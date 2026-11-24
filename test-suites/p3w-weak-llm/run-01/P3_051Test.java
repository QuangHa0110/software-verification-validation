package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_051Test {

    @Test
    void pricingServiceCalculateDiscountNullTodayThrows() {
        PricingService pricing = new PricingService();
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateDiscount(100000L, null, null));
    }
}
