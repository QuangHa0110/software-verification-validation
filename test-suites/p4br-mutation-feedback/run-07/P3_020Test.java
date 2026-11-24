package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void shippingRejectsInvalidArgumentsAndOverweightEvenWhenShippingWouldBeFree() {
        ShippingCalculator shipping = new ShippingCalculator();
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(null, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.REMOTE, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.SUBURB, 0, 500_000));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.PROVINCE, 1, -1));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.INNER_CITY, 20_001, 499_999));
        assertThrows(IllegalArgumentException.class, () -> shipping.calculate(Region.SUBURB, 20_001, 500_000));
    }
}
