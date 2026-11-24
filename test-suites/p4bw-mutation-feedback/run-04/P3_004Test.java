package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void customExceptionsInstantiation() {
        InsufficientStockException ex1 = new InsufficientStockException("out of stock");
        InsufficientStockException ex2 = new InsufficientStockException(null);
        CouponExpiredException ex3 = new CouponExpiredException("expired");
        CouponExpiredException ex4 = new CouponExpiredException(null);
        CouponNotApplicableException ex5 = new CouponNotApplicableException("not applicable");
        CouponNotApplicableException ex6 = new CouponNotApplicableException(null);
        assertNotNull(ex1);
        assertNotNull(ex2);
        assertNotNull(ex3);
        assertNotNull(ex4);
        assertNotNull(ex5);
        assertNotNull(ex6);
    }
}
