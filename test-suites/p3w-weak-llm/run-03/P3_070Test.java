package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_070Test {

    @Test
    void shippingCalculatorExcessWeightFreeShippingAppliesEvenForExcessWeight() {
        ShippingCalculator sc = new ShippingCalculator();
        assertEquals(0L, sc.calculate(Region.SUBURB, 20000, 500000L));
        assertEquals(0L, sc.calculate(Region.INNER_CITY, 1501, 500000L));
    }
}
