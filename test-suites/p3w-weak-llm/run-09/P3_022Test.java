package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void shippingCalculatorWeightTiersAndBaseFees() {
        ShippingCalculator calculator = new ShippingCalculator();

        assertEquals(15000L, calculator.calculate(Region.INNER_CITY, 1, 0L));
        assertEquals(15000L, calculator.calculate(Region.INNER_CITY, 1000, 0L));
        assertEquals(17000L, calculator.calculate(Region.INNER_CITY, 1001, 0L));
        assertEquals(17000L, calculator.calculate(Region.INNER_CITY, 1500, 0L));
        assertEquals(19000L, calculator.calculate(Region.INNER_CITY, 1501, 0L));
        assertEquals(91000L, calculator.calculate(Region.INNER_CITY, 20000, 0L));

        assertEquals(25000L, calculator.calculate(Region.SUBURB, 1000, 100L));
        assertEquals(35000L, calculator.calculate(Region.PROVINCE, 1000, 100L));
        assertEquals(55000L, calculator.calculate(Region.REMOTE, 1000, 100L));

        assertEquals(39000L, calculator.calculate(Region.PROVINCE, 1501, 500000L));
        assertEquals(59000L, calculator.calculate(Region.REMOTE, 1501, 500000L));
    }
}
