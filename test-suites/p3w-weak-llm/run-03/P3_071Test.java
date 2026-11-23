package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_071Test {

    @Test
    void shippingCalculatorProvinceAndRemoteExcessWeightOverThreshold() {
        ShippingCalculator sc = new ShippingCalculator();
        assertEquals(39000L, sc.calculate(Region.PROVINCE, 1501, 500000L));
        assertEquals(59000L, sc.calculate(Region.REMOTE, 1501, 500000L));
    }
}
