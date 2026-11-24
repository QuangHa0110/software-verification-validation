package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void shippingRejectsInvalidInputsBeforeFreeShipping() {
        ShippingCalculator shipping = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(null, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.PROVINCE, 1, -1));
        for (int weight : new int[]{-1, 0, 20001}) {
            assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.INNER_CITY, weight, 500000));
        }
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.REMOTE, 20001, 0));
    }
}
