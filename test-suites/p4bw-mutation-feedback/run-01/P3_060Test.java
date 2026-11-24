package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_060Test {

    @Test
    void shippingCalculatorProvinceAndRemoteDoNotQualifyForFreeShipping() {
        ShippingCalculator calc = new ShippingCalculator();
        // 1500g: 1000g base + 500g extra (1 step * 2000)
        // PROVINCE: 35000 + 2000 = 37000
        assertEquals(37000L, calc.calculate(Region.PROVINCE, 1500, 500000L));
        // REMOTE: 55000 + 2000 = 57000
        assertEquals(57000L, calc.calculate(Region.REMOTE, 1500, 500000L));
    }
}
