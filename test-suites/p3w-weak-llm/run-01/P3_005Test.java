package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void customExceptionsInstantiation() {
        InsufficientStockException ex1 = new InsufficientStockException("Out of stock");
        assertEquals("Out of stock", ex1.getMessage());
        InsufficientStockException ex1Null = new InsufficientStockException(null);
        assertNull(ex1Null.getMessage());

        CouponExpiredException ex2 = new CouponExpiredException("Expired");
        assertEquals("Expired", ex2.getMessage());
        CouponExpiredException ex2Null = new CouponExpiredException(null);
        assertNull(ex2Null.getMessage());

        CouponNotApplicableException ex3 = new CouponNotApplicableException("Min order not met");
        assertEquals("Min order not met", ex3.getMessage());
        CouponNotApplicableException ex3Null = new CouponNotApplicableException(null);
        assertNull(ex3Null.getMessage());
    }
}
