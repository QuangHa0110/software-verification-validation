package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_037Test {

    @Test
    void testShippingCalculatorZeroBoundaries() {
        ShippingCalculator calculator = new ShippingCalculator();

        // Boundary check: weightGrams == 0 throws IllegalArgumentException (boundary: <= 0 vs < 0)
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculate(Region.INNER_CITY, 0, 100_000L)
        );

        // Boundary check: orderValue == 0 is valid and succeeds (boundary: < 0 vs <= 0)
        assertEquals(15_000L, calculator.calculate(Region.INNER_CITY, 500, 0L));
    }
}
