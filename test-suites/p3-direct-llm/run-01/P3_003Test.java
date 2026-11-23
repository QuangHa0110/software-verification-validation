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
        for (String message : new String[] {"detail", "", null}) {
            assertEquals(RuntimeException.class,
                new InsufficientStockException(message).getClass().getSuperclass());
            assertEquals(RuntimeException.class,
                new CouponExpiredException(message).getClass().getSuperclass());
            assertEquals(RuntimeException.class,
                new CouponNotApplicableException(message).getClass().getSuperclass());
        }
    }
}
