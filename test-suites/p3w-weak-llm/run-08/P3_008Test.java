package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void customExceptionsInstantiation() {
        InsufficientStockException e1 = new InsufficientStockException("out of stock");
        assertTrue(e1 instanceof RuntimeException);
        InsufficientStockException e1Null = new InsufficientStockException(null);
        assertNull(e1Null.getMessage());

        CouponExpiredException e2 = new CouponExpiredException("expired");
        assertTrue(e2 instanceof RuntimeException);
        CouponExpiredException e2Null = new CouponExpiredException(null);
        assertNull(e2Null.getMessage());

        CouponNotApplicableException e3 = new CouponNotApplicableException("not applicable");
        assertTrue(e3 instanceof RuntimeException);
        CouponNotApplicableException e3Null = new CouponNotApplicableException(null);
        assertNull(e3Null.getMessage());
    }
}
