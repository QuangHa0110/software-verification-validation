package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @Test
    void businessExceptionsAcceptOrdinaryEmptyAndNullMessages() {
        for (String message : Arrays.asList("failure", "", null)) {
            for (RuntimeException error : List.of(new InsufficientStockException(message),
                    new CouponExpiredException(message), new CouponNotApplicableException(message))) {
                assertEquals(RuntimeException.class, error.getClass().getSuperclass());
            }
        }
    }
}
