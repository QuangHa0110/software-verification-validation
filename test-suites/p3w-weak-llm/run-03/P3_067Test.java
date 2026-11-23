package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_067Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY, 500000, 0",
        "SUBURB, 500000, 0",
        "INNER_CITY, 600000, 0",
        "SUBURB, 600000, 0",
        "PROVINCE, 500000, 35000",
        "REMOTE, 500000, 55000"
    })
    void shippingCalculatorFreeShippingThresholdAt1000Grams(Region region, long orderValue, long expectedFee) {
        ShippingCalculator sc = new ShippingCalculator();
        assertEquals(expectedFee, sc.calculate(region, 1000, orderValue));
    }
}
