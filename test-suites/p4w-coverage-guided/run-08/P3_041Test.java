package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_041Test {

    @Test
    void shippingCalculatorWeightTiersAndExcess() {
        ShippingCalculator calc = new ShippingCalculator();
        long val = 100000L;

        assertEquals(15000L, calc.calculate(Region.INNER_CITY, 1, val));
        assertEquals(15000L, calc.calculate(Region.INNER_CITY, 1000, val));
        assertEquals(17000L, calc.calculate(Region.INNER_CITY, 1001, val));
        assertEquals(17000L, calc.calculate(Region.INNER_CITY, 1500, val));
        assertEquals(19000L, calc.calculate(Region.INNER_CITY, 1501, val));
        assertEquals(91000L, calc.calculate(Region.INNER_CITY, 20000, val));

        assertEquals(25000L, calc.calculate(Region.SUBURB, 1000, val));
        assertEquals(35000L, calc.calculate(Region.PROVINCE, 1000, val));
        assertEquals(55000L, calc.calculate(Region.REMOTE, 1000, val));

        assertEquals(39000L, calc.calculate(Region.PROVINCE, 1501, 500000L));
        assertEquals(59000L, calc.calculate(Region.REMOTE, 1501, 500000L));
    }
}
