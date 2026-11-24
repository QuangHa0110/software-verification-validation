package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_066Test {

    @Test
    void shippingCalculatorExceedsMaxWeightThrows() {
        ShippingCalculator sc = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> sc.calculate(Region.INNER_CITY, 20001, 500000L));
        assertThrows(IllegalArgumentException.class, () -> sc.calculate(Region.PROVINCE, 20001, 100000L));
    }
}
