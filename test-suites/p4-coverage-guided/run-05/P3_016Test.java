package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void shippingValidatesAllInputsBeforeFreeDelivery() {
        ShippingCalculator shipping = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(null, 1, 500_000));
        for (int weight : new int[]{-1, 0, 20_001}) {
            assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.INNER_CITY, weight, 500_000));
        }
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.REMOTE, 1, -1));
    }
}
