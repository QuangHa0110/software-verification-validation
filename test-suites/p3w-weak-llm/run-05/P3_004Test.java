package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void testCustomExceptionsInstantiation() {
        InsufficientStockException ex1 = new InsufficientStockException("Out of stock");
        InsufficientStockException ex1Null = new InsufficientStockException(null);
        assertNotNull(ex1);
        assertNotNull(ex1Null);

        CouponExpiredException ex2 = new CouponExpiredException("Expired");
        CouponExpiredException ex2Null = new CouponExpiredException(null);
        assertNotNull(ex2);
        assertNotNull(ex2Null);

        CouponNotApplicableException ex3 = new CouponNotApplicableException("Not applicable");
        CouponNotApplicableException ex3Null = new CouponNotApplicableException(null);
        assertNotNull(ex3);
        assertNotNull(ex3Null);
    }
}
