package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void orderStatusEnumValuesAndOrder() {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(4, values.length);
        assertEquals(OrderStatus.PENDING, values[0]);
        assertEquals(OrderStatus.PAID, values[1]);
        assertEquals(OrderStatus.SHIPPED, values[2]);
        assertEquals(OrderStatus.CANCELLED, values[3]);
    }
}
