package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_062Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -500})
    void shippingCalculatorNonPositiveWeightThrowsException(int invalidWeight) {
        ShippingCalculator calc = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(Region.INNER_CITY, invalidWeight, 100000L));
    }
}
