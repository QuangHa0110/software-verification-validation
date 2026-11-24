package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY,15000,0,0",
        "SUBURB,25000,0,0",
        "PROVINCE,35000,35000,111000",
        "REMOTE,55000,55000,131000"
    })
    void shippingThresholdDependsOnRegionAndWaivesTheEntireEligibleFee(
            Region region, long base, long atThreshold, long heavyAboveThreshold) {
        ShippingCalculator shipping = new ShippingCalculator();
        assertEquals(base, shipping.calculate(region, 1000, 499_999));
        assertEquals(atThreshold, shipping.calculate(region, 1000, 500_000));
        assertEquals(heavyAboveThreshold, shipping.calculate(region, 20_000, 500_001));
    }
}
