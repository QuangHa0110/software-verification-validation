package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_005Test {

    @Test
    void killShippingCalculatorCalculateBoundaryMutants() {
        ShippingCalculator calculator = new ShippingCalculator();

        // weightGrams = 1 (boundary weightGrams <= 0 mutant, e.g. < 0 vs <= 0)
        assertEquals(15000, calculator.calculate(Region.INNER_CITY, 1, 0));

        // orderValue = 0 (boundary orderValue < 0 mutant, e.g. <= 0 vs < 0)
        assertEquals(15000, calculator.calculate(Region.INNER_CITY, 500, 0));
    }
}
