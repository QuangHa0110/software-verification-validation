package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_104Test {

    @Test
    void exceptionClassesInstantiateProperly() {
        InsufficientStockException ex1 = new InsufficientStockException("Out of stock");
        assertNotNull(ex1);
        CouponExpiredException ex2 = new CouponExpiredException("Expired");
        assertNotNull(ex2);
        CouponNotApplicableException ex3 = new CouponNotApplicableException("Not applicable");
        assertNotNull(ex3);
    }
}
