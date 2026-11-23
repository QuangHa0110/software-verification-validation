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
        "SUBURB, 1200, 27000",
        "PROVINCE, 1200, 37000",
        "REMOTE, 1200, 57000"
    })
    void shippingCalculatorBaseRegionsWithSurcharge(Region region, int weight, long expectedFee) {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(expectedFee, calc.calculate(region, weight, 100000L));
    }
}
