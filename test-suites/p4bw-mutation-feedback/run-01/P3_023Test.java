package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void couponConstructorNullTypeThrows() {
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C1", null, 100L, 0L, 0L, expiry));
    }
}
