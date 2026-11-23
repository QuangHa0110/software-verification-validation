package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_065Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY, 500000",
        "INNER_CITY, 600000",
        "SUBURB, 500000",
        "SUBURB, 800000"
    })
    void shippingCalculatorFreeShippingForInnerCityAndSuburb(Region region, long orderVal) {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(0L, calc.calculate(region, 1500, orderVal));
    }
}
