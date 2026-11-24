package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void domainExceptionsAcceptOrdinaryEmptyAndNullMessages() {
        for (String message : Arrays.asList("stock", "", null)) {
            assertEquals(RuntimeException.class, new InsufficientStockException(message).getClass().getSuperclass());
            assertEquals(RuntimeException.class, new CouponExpiredException(message).getClass().getSuperclass());
            assertEquals(RuntimeException.class, new CouponNotApplicableException(message).getClass().getSuperclass());
        }
    }
}
