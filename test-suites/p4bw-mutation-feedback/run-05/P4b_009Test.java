package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_009Test {

    @Test
    void testKillShippingCalculatorOrderValueZeroBoundary() {
        ShippingCalculator calculator = new ShippingCalculator();
        assertDoesNotThrow(() ->
            calculator.calculate(Region.INNER_CITY, 500, 0)
        );
        assertEquals(15000, calculator.calculate(Region.INNER_CITY, 500, 0));
    }
}
