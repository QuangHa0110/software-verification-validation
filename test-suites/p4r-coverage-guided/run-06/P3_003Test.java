package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @Test
    void businessExceptionsAcceptOrdinaryEmptyAndNullMessages() {
        for (String message : Arrays.asList("failure", "", null)) {
            RuntimeException[] errors = {
                new InsufficientStockException(message),
                new CouponExpiredException(message),
                new CouponNotApplicableException(message)
            };
            assertEquals(InsufficientStockException.class, errors[0].getClass());
            assertEquals(CouponExpiredException.class, errors[1].getClass());
            assertEquals(CouponNotApplicableException.class, errors[2].getClass());
            for (RuntimeException error : errors) {
                assertEquals(RuntimeException.class, error.getClass().getSuperclass());
            }
        }
    }
}
