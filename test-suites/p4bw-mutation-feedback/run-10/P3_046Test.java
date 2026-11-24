package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_046Test {

    @Test
    void testShippingCalculatorHighWeightWithFreeShipping() {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(0L, calc.calculate(Region.INNER_CITY, 20000, 500000L));
        assertEquals(0L, calc.calculate(Region.SUBURB, 20000, 500000L));
        assertEquals(35000L + 76000L, calc.calculate(Region.PROVINCE, 20000, 500000L));
        assertEquals(55000L + 76000L, calc.calculate(Region.REMOTE, 20000, 500000L));
    }
}
