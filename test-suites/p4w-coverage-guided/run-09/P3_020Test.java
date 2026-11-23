package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void shippingCalculatorValidation() {
        ShippingCalculator calculator = new ShippingCalculator();

        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null, 1000, 100000L));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(Region.INNER_CITY, 0, 100000L));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(Region.INNER_CITY, -1, 100000L));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(Region.INNER_CITY, 1000, -1L));

        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(Region.INNER_CITY, 20001, 100000L));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(Region.INNER_CITY, 20001, 500000L));
    }
}
