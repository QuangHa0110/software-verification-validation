package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_051Test {

    @Test
    void testOrderServiceConstructors() {
        assertDoesNotThrow(() -> new OrderService());
        PricingService pricing = new PricingService();
        ShippingCalculator shipping = new ShippingCalculator();
        assertDoesNotThrow(() -> new OrderService(pricing, shipping));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, shipping));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(pricing, null));
    }
}
