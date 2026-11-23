package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @Test
    void shippingCalculatorInvalidInputs() {
        ShippingCalculator calc = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(null, 100, 10000L));
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, 0, 10000L));
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, -1, 10000L));
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, 100, -1L));
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, 20001, 10000L));
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, 20001, 500000L));
    }
}
