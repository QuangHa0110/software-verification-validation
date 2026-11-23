package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_082Test {

    @Test
    void orderServiceConstructorInjectionAndNullChecks() {
        PricingService ps = new PricingService();
        ShippingCalculator sc = new ShippingCalculator();

        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, sc));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(ps, null));

        OrderService customService = new OrderService(ps, sc);
        assertNotNull(customService);
        OrderService defaultService = new OrderService();
        assertNotNull(defaultService);
    }
}
