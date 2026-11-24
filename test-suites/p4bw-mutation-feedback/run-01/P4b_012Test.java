package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_012Test {

    @Test
    void killShippingCalculatorCalculateWeight20000Boundary() {
        ShippingCalculator calculator = new ShippingCalculator();
        // weightGrams == 20000 is the upper valid limit, must not throw IllegalArgumentException
        long fee = calculator.calculate(Region.INNER_CITY, 20_000, 100_000L);
        // 15,000 + 38 * 2,000 = 91,000
        assertEquals(91_000L, fee);
    }
}
