package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_044Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY, 1000, 499999, 15000",
        "SUBURB, 1000, 499999, 25000",
        "PROVINCE, 1000, 499999, 35000",
        "REMOTE, 1000, 499999, 55000",
        "INNER_CITY, 1000, 500000, 0",
        "SUBURB, 1000, 500000, 0",
        "PROVINCE, 1000, 500000, 35000",
        "REMOTE, 1000, 500000, 55000"
    })
    void testShippingCalculatorFreeShippingThresholds(Region region, int weight, long orderValue, long expectedFee) {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(expectedFee, calc.calculate(region, weight, orderValue));
    }
}
