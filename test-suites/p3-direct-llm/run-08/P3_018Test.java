package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void injectedServicesReceiveExactInputsInOrderOncePerPlacementIncludingRetries() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Coupon code = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        Product a = new Product("A", "A", 40, 2, 2);
        Product b = new Product("B", "B", 20, 7, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        int[] attempt = {0};
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate date) {
                calls.add("price");
                assertEquals(100L, subtotal);
                assertEquals(code, coupon);
                assertEquals(t.toLocalDate(), date);
                assertEquals(2, a.getStock());
                assertEquals(1, b.getStock());
                if (attempt[0] == 0) {
                    throw new ArithmeticException();
                }
                return 7;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("ship");
                assertEquals(Region.REMOTE, region);
                assertEquals(11, weight);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(1, b.getStock());
                if (attempt[0] == 1) {
                    throw new UnsupportedOperationException();
                }
                return 11;
            }
        });
        assertTrue(calls.isEmpty());
        Runnable verifyCart = () -> {
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        };

        assertThrows(ArithmeticException.class, () -> service.placeOrder("O", cart, code, Region.REMOTE, t));
        assertEquals(List.of("price"), calls);
        assertEquals(2, a.getStock());
        assertEquals(1, b.getStock());
        verifyCart.run();

        attempt[0] = 1;
        calls.clear();
        assertThrows(UnsupportedOperationException.class,
            () -> service.placeOrder("O", cart, code, Region.REMOTE, t));
        assertEquals(List.of("price", "ship"), calls);
        assertEquals(2, a.getStock());
        assertEquals(1, b.getStock());
        verifyCart.run();

        attempt[0] = 2;
        calls.clear();
        Order order = service.placeOrder(" O ", cart, code, Region.REMOTE, t);
        assertEquals(List.of("price", "ship"), calls);
        assertEquals(" O ", order.getId());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(0, b.getStock());
        verifyCart.run();
    }
}
