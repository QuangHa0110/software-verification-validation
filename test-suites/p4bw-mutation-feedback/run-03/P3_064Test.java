package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_064Test {

    @Test
    void shippingCalculatorDefaultConstructor() {
        ShippingCalculator sc = new ShippingCalculator();
        assertEquals(15000L, sc.calculate(Region.INNER_CITY, 1, 0L));
    }
}
