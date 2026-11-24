package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_083Test {

    @Test
    void orderServiceConstructorNullDependenciesThrowsException() {
        PricingService pricingService = new PricingService();
        ShippingCalculator shippingCalc = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, shippingCalc));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(pricingService, null));
    }
}
