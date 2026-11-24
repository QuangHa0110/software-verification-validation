package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void shippingCalculatorFreeShippingThreshold() {
        ShippingCalculator calculator = new ShippingCalculator();

        assertEquals(0L, calculator.calculate(Region.INNER_CITY, 1000, 500000L));
        assertEquals(0L, calculator.calculate(Region.SUBURB, 1000, 500000L));
        assertEquals(0L, calculator.calculate(Region.INNER_CITY, 20000, 1000000L));
        assertEquals(0L, calculator.calculate(Region.SUBURB, 20000, 1000000L));

        assertEquals(15000L, calculator.calculate(Region.INNER_CITY, 1000, 499999L));
        assertEquals(25000L, calculator.calculate(Region.SUBURB, 1000, 499999L));

        assertEquals(35000L, calculator.calculate(Region.PROVINCE, 1000, 500000L));
        assertEquals(55000L, calculator.calculate(Region.REMOTE, 1000, 500000L));
    }
}
