package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_059Test {

    @Test
    void shippingCalculatorFreeShippingForInnerCityAndSuburbAboveThreshold() {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(0L, calc.calculate(Region.INNER_CITY, 1500, 500000L));
        assertEquals(0L, calc.calculate(Region.SUBURB, 1500, 500000L));
    }
}
