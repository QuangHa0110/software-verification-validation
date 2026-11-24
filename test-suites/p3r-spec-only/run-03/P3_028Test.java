package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void lifecycleOperationsRejectNullOrders() {
        OrderService service = new OrderService();
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, t));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, null));
    }
}
