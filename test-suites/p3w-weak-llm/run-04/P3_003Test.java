package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @Test
    void enumValuesAndValueOfOrderStatus() {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(4, values.length);
        assertSame(OrderStatus.PENDING, values[0]);
        assertSame(OrderStatus.PAID, values[1]);
        assertSame(OrderStatus.SHIPPED, values[2]);
        assertSame(OrderStatus.CANCELLED, values[3]);
        assertSame(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertSame(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertSame(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertSame(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("INVALID"));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
    }
}
