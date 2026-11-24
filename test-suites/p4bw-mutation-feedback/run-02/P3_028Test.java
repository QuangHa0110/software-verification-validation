package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void shippingCalculatorFreeShippingThreshold() {
        ShippingCalculator calc = new ShippingCalculator();

        assertEquals(0L, calc.calculate(Region.INNER_CITY, 1500, 500000L));
        assertEquals(0L, calc.calculate(Region.SUBURB, 1500, 500000L));
        assertEquals(0L, calc.calculate(Region.INNER_CITY, 1500, 600000L));

        assertEquals(39000L, calc.calculate(Region.PROVINCE, 1501, 500000L));
        assertEquals(59000L, calc.calculate(Region.REMOTE, 1501, 500000L));

        assertEquals(15000L, calc.calculate(Region.INNER_CITY, 1000, 499999L));
        assertEquals(25000L, calc.calculate(Region.SUBURB, 1000, 499999L));
    }
}
