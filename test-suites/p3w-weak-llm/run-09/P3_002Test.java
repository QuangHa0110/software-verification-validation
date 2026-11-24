package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void customExceptionsInstantiationWithMessageAndNull() {
        InsufficientStockException e1 = new InsufficientStockException("out of stock");
        InsufficientStockException e2 = new InsufficientStockException(null);
        assertNotNull(e1);
        assertNotNull(e2);

        CouponExpiredException e3 = new CouponExpiredException("expired");
        CouponExpiredException e4 = new CouponExpiredException(null);
        assertNotNull(e3);
        assertNotNull(e4);

        CouponNotApplicableException e5 = new CouponNotApplicableException("not applicable");
        CouponNotApplicableException e6 = new CouponNotApplicableException(null);
        assertNotNull(e5);
        assertNotNull(e6);
    }
}
