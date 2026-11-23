package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_045Test {

    @Test
    void orderServiceConstructors() {
        OrderService serviceDefault = new OrderService();
        assertNotNull(serviceDefault);

        PricingService ps = new PricingService();
        ShippingCalculator sc = new ShippingCalculator();
        OrderService serviceCustom = new OrderService(ps, sc);
        assertNotNull(serviceCustom);

        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, sc));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(ps, null));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, null));
    }
}
