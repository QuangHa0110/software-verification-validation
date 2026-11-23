package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_065Test {

    @Test
    void shippingCalculatorInvalidInputsThrowsBeforeFreeShipping() {
        ShippingCalculator sc = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> sc.calculate(null, 100, 500000L));
        assertThrows(IllegalArgumentException.class, () -> sc.calculate(Region.INNER_CITY, 0, 500000L));
        assertThrows(IllegalArgumentException.class, () -> sc.calculate(Region.INNER_CITY, -1, 500000L));
        assertThrows(IllegalArgumentException.class, () -> sc.calculate(Region.INNER_CITY, 100, -1L));
    }
}
