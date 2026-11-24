package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @ParameterizedTest
    @ValueSource(strings = {"success", "pricingError", "shippingError"})
    void injectedCalculatorsReceiveExactArgumentsInOrderBeforeAnyStockDeduction(String scenario) {
        Product a = new Product("A", "A", 30, 3, 2);
        Product b = new Product("B", "B", 40, 5, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Coupon input = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        List<String> calls = new ArrayList<>();

        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertEquals(input, coupon);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(2, a.getStock());
                assertEquals(4, b.getStock());
                if ("pricingError".equals(scenario)) {
                    throw new ArithmeticException();
                }
                return 7L;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(11, grams);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(4, b.getStock());
                if ("shippingError".equals(scenario)) {
                    throw new UnsupportedOperationException();
                }
                return 11L;
            }
        });

        assertTrue(calls.isEmpty());
        if ("pricingError".equals(scenario)) {
            assertThrows(ArithmeticException.class, () -> service.placeOrder("O", cart, input, Region.PROVINCE, t));
            assertEquals(List.of("pricing"), calls);
        } else if ("shippingError".equals(scenario)) {
            assertThrows(UnsupportedOperationException.class,
                    () -> service.placeOrder("O", cart, input, Region.PROVINCE, t));
            assertEquals(List.of("pricing", "shipping"), calls);
        } else {
            Order order = service.placeOrder("O", cart, input, Region.PROVINCE, t);
            assertEquals(List.of("pricing", "shipping"), calls);
            assertEquals(100L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(104L, order.getTotal());
            assertEquals(OrderStatus.PENDING, order.getStatus());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
        }
        boolean placed = "success".equals(scenario);
        assertEquals(placed ? 0 : 2, a.getStock());
        assertEquals(placed ? 3 : 4, b.getStock());
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
    }
}
