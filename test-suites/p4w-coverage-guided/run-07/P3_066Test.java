package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_066Test {

    @ParameterizedTest
    @CsvSource({
        "PROVINCE, 500000, 35000",
        "REMOTE, 500000, 55000"
    })
    void shippingCalculatorNoFreeShippingForProvinceAndRemote(Region region, long orderVal, long expectedBase) {
        ShippingCalculator calc = new ShippingCalculator();
        assertEquals(expectedBase, calc.calculate(region, 500, orderVal));
    }
}
