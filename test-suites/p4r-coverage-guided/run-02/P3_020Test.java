package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void shippingRejectsInvalidRegionWeightAndValueBeforeFreeShipping() {
        ShippingCalculator shipping = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(null, 1000, 500000));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.REMOTE, 1, -1));
        for (Region region : Region.values()) {
            for (int weight : new int[] {-1, 0, 20001}) {
                assertThrows(IllegalArgumentException.class,
                    () -> shipping.calculate(region, weight, 500000));
            }
        }
    }
}
