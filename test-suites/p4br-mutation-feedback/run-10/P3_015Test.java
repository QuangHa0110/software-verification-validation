package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY,1,0,15000", "INNER_CITY,1000,499999,15000", "SUBURB,1000,499999,25000",
        "PROVINCE,1000,499999,35000", "REMOTE,1000,499999,55000",
        "INNER_CITY,1000,500000,0", "SUBURB,1000,500000,0",
        "PROVINCE,1000,500000,35000", "REMOTE,1000,500000,55000",
        "INNER_CITY,1001,499999,17000", "INNER_CITY,1500,499999,17000",
        "INNER_CITY,1501,499999,19000", "INNER_CITY,20000,499999,91000",
        "INNER_CITY,20000,500001,0", "SUBURB,20000,500001,0",
        "PROVINCE,20000,500001,111000", "REMOTE,20000,500001,131000",
        "SUBURB,1501,499999,29000", "PROVINCE,1501,500000,39000", "REMOTE,1501,500000,59000"
    })
    void shippingUsesRegionalBaseStartedWeightBlocksAndEligibleFreeThreshold(
            Region region, int weight, long goods, long expected) {
        assertEquals(expected, new ShippingCalculator().calculate(region, weight, goods));
    }
}
