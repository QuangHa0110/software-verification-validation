package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY, 1, 15000",
        "INNER_CITY, 1000, 15000",
        "INNER_CITY, 1001, 17000",
        "INNER_CITY, 1500, 17000",
        "INNER_CITY, 1501, 19000",
        "INNER_CITY, 20000, 91000",
        "SUBURB, 1000, 25000",
        "SUBURB, 1501, 29000",
        "PROVINCE, 1000, 35000",
        "PROVINCE, 1501, 39000",
        "REMOTE, 1000, 55000",
        "REMOTE, 1501, 59000"
    })
    void shippingCalculatorWeightTiersAllRegions(String regionName, int weight, long expectedFee) {
        ShippingCalculator calc = new ShippingCalculator();
        Region region = Region.valueOf(regionName);
        assertEquals(expectedFee, calc.calculate(region, weight, 100000L));
    }
}
