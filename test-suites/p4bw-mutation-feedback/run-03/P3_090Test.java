package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_090Test {

    @Test
    void orderServicePlaceOrderTotalWeightExceedsLimitPreservesStock() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Bút", 10L, 10000, 1);
        Product p2 = new Product("P2", "Vở", 20L, 10001, 2);
        cart.addItem(p1, 1);
        cart.addItem(p2, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () ->
            service.placeOrder("D3", cart, null, Region.REMOTE, now));
        assertEquals(1, p1.getStock());
        assertEquals(2, p2.getStock());
    }
}
