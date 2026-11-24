package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_061Test {

    @Test
    void shippingCalculatorBaseAndWeightTiers() {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(15000L, calc.calculate(Region.INNER_CITY, 1000, 100000L));
        assertEquals(17000L, calc.calculate(Region.INNER_CITY, 1001, 100000L));
        assertEquals(17000L, calc.calculate(Region.INNER_CITY, 1500, 100000L));
        assertEquals(19000L, calc.calculate(Region.INNER_CITY, 1501, 100000L));
        assertEquals(25000L, calc.calculate(Region.SUBURB, 500, 100000L));
        assertEquals(35000L, calc.calculate(Region.PROVINCE, 500, 100000L));
        assertEquals(55000L, calc.calculate(Region.REMOTE, 500, 100000L));
    }
}
