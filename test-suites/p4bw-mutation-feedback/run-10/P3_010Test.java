package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void testCustomExceptionsInstantiation() {
        InsufficientStockException ex1 = new InsufficientStockException("out of stock");
        assertEquals("out of stock", ex1.getMessage());
        InsufficientStockException ex1Null = new InsufficientStockException(null);
        assertNull(ex1Null.getMessage());

        CouponExpiredException ex2 = new CouponExpiredException("expired");
        assertEquals("expired", ex2.getMessage());
        CouponExpiredException ex2Null = new CouponExpiredException(null);
        assertNull(ex2Null.getMessage());

        CouponNotApplicableException ex3 = new CouponNotApplicableException("not applicable");
        assertEquals("not applicable", ex3.getMessage());
        CouponNotApplicableException ex3Null = new CouponNotApplicableException(null);
        assertNull(ex3Null.getMessage());
    }
}
