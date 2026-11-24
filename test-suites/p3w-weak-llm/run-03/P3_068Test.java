package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_068Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY, 499999, 1000, 15000",
        "SUBURB, 499999, 1000, 25000",
        "PROVINCE, 499999, 1000, 35000",
        "REMOTE, 499999, 1000, 55000"
    })
    void shippingCalculatorBaseFeesUnderThreshold(Region region, long orderValue, int weight, long expectedFee) {
        ShippingCalculator sc = new ShippingCalculator();
        assertEquals(expectedFee, sc.calculate(region, weight, orderValue));
    }
}
