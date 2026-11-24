package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void customExceptionsInstantiationAndHierarchy() {
        InsufficientStockException e1 = new InsufficientStockException("stock error");
        InsufficientStockException e1Null = new InsufficientStockException(null);
        CouponExpiredException e2 = new CouponExpiredException("expired error");
        CouponExpiredException e2Null = new CouponExpiredException(null);
        CouponNotApplicableException e3 = new CouponNotApplicableException("not applicable");
        CouponNotApplicableException e3Null = new CouponNotApplicableException(null);

        assertTrue(e1 instanceof RuntimeException);
        assertTrue(e1Null instanceof RuntimeException);
        assertTrue(e2 instanceof RuntimeException);
        assertTrue(e2Null instanceof RuntimeException);
        assertTrue(e3 instanceof RuntimeException);
        assertTrue(e3Null instanceof RuntimeException);
    }
}
