package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_034Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY, 1, 15000",
        "INNER_CITY, 1000, 15000",
        "INNER_CITY, 1001, 17000",
        "INNER_CITY, 1500, 17000",
        "INNER_CITY, 1501, 19000",
        "INNER_CITY, 20000, 91000",
        "SUBURB, 500, 25000",
        "PROVINCE, 1200, 37000",
        "REMOTE, 1501, 59000"
    })
    void testShippingCalculatorWeightSurcharges(Region region, int weight, long expectedFee) {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(expectedFee, calc.calculate(region, weight, 100000L));
    }
}
