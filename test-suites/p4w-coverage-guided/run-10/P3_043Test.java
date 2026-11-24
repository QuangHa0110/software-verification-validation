package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_043Test {

    @Test
    void testShippingCalculatorWeightExceeds20000() {
        ShippingCalculator calc = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, 20001, 100000L));
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, 20001, 500000L));
    }
}
