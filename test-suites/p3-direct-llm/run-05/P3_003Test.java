package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @Test
    void domainExceptionsAcceptOrdinaryEmptyAndNullMessages() {
        for (String message : Arrays.asList("example", "", null)) {
            RuntimeException stock = new InsufficientStockException(message);
            RuntimeException expired = new CouponExpiredException(message);
            RuntimeException minimum = new CouponNotApplicableException(message);
            assertEquals(InsufficientStockException.class, stock.getClass());
            assertEquals(CouponExpiredException.class, expired.getClass());
            assertEquals(CouponNotApplicableException.class, minimum.getClass());
            for (RuntimeException exception : List.of(stock, expired, minimum)) {
                assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
            }
        }
    }
}
