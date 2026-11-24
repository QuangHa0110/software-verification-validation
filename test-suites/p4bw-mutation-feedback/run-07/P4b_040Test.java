package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_040Test {

    @Test
    void shippingCalculatorWeightGramsOneAndOrderValueZeroSucceeds() {
        ShippingCalculator calculator = new ShippingCalculator();
        long fee = calculator.calculate(Region.INNER_CITY, 1, 0L);
        assertEquals(15_000L, fee);
    }
}
