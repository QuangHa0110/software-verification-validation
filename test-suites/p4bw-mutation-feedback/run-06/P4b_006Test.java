package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_006Test {

    @Test
    void shippingCalculatorWithWeightOneAndOrderValueZero() {
        ShippingCalculator calculator = new ShippingCalculator();

        long feeWeightOne = calculator.calculate(Region.INNER_CITY, 1, 100000);
        assertEquals(15000, feeWeightOne);

        long feeZeroOrderValue = calculator.calculate(Region.INNER_CITY, 500, 0);
        assertEquals(15000, feeZeroOrderValue);
    }
}
