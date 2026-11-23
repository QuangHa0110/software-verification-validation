package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void customExceptionConstructors() {
        InsufficientStockException ex1 = new InsufficientStockException("Out of stock");
        assertNotNull(ex1);
        InsufficientStockException ex1Null = new InsufficientStockException(null);
        assertNotNull(ex1Null);

        CouponExpiredException ex2 = new CouponExpiredException("Expired");
        assertNotNull(ex2);
        CouponExpiredException ex2Null = new CouponExpiredException(null);
        assertNotNull(ex2Null);

        CouponNotApplicableException ex3 = new CouponNotApplicableException("Not applicable");
        assertNotNull(ex3);
        CouponNotApplicableException ex3Null = new CouponNotApplicableException(null);
        assertNotNull(ex3Null);
    }
}
