package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void orderStatusEnumValuesAndValueOf() {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(4, values.length);
        assertEquals(OrderStatus.PENDING, values[0]);
        assertEquals(OrderStatus.PAID, values[1]);
        assertEquals(OrderStatus.SHIPPED, values[2]);
        assertEquals(OrderStatus.CANCELLED, values[3]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("pending"));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
    }
}
