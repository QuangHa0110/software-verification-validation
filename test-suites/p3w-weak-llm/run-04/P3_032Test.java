package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @Test
    void shippingCalculatorExcessWeightProgression() {
        ShippingCalculator calc = new ShippingCalculator();

        assertEquals(15000L, calc.calculate(Region.INNER_CITY, 1, 0L));
        assertEquals(15000L, calc.calculate(Region.INNER_CITY, 1000, 0L));
        assertEquals(17000L, calc.calculate(Region.INNER_CITY, 1001, 0L));
        assertEquals(17000L, calc.calculate(Region.INNER_CITY, 1500, 0L));
        assertEquals(19000L, calc.calculate(Region.INNER_CITY, 1501, 0L));
        assertEquals(91000L, calc.calculate(Region.INNER_CITY, 20000, 0L));
    }
}
