package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_061Test {

    @Test
    void shippingCalculatorNullRegionThrowsException() {
        ShippingCalculator calc = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> calc.calculate(null, 500, 100000L));
    }
}
