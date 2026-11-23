package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @ParameterizedTest
    @CsvSource({"1,15000", "999,15000", "1000,15000", "1001,17000",
                "1499,17000", "1500,17000", "1501,19000", "20000,91000"})
    void shippingChargesForEachStartedBlockAboveOneKilogram(int weight, long expected) {
        assertEquals(expected, new ShippingCalculator().calculate(Region.INNER_CITY, weight, 0));
    }
}
