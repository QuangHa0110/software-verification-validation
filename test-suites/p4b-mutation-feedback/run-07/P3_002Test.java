package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void domainExceptionsAcceptNullEmptyAndOrdinaryMessages() {
        for (String message : Arrays.asList(null, "", "detail")) {
            RuntimeException[] exceptions = {
                new InsufficientStockException(message),
                new CouponExpiredException(message),
                new CouponNotApplicableException(message)
            };
            assertEquals(InsufficientStockException.class, exceptions[0].getClass());
            assertEquals(CouponExpiredException.class, exceptions[1].getClass());
            assertEquals(CouponNotApplicableException.class, exceptions[2].getClass());
            for (RuntimeException exception : exceptions) {
                assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
            }
        }
    }
}
