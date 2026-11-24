package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_011Test {

    @Test
    void killShippingCalculatorCalculateWeightAndOrderValueZeroBoundaries() {
        ShippingCalculator calculator = new ShippingCalculator();

        // weightGrams <= 0 check: weightGrams == 0 throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(Region.INNER_CITY, 0, 100_000L));

        // orderValue < 0 check: orderValue == 0 is valid and should not throw
        long fee = calculator.calculate(Region.INNER_CITY, 500, 0L);
        assertEquals(15_000L, fee);
    }
}
