package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @ParameterizedTest
    @ValueSource(strings = {"success", "pricingFailure", "shippingFailure"})
    void injectedServicesReceiveExactArgumentsInOrderBeforeAnyStockIsDebited(String path) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999999999);
        Coupon coupon = new Coupon("X", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        Product a = new Product("A", "A", 40, 7, 3);
        Product b = new Product("B", "B", 20, 3, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon supplied, LocalDate today) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertEquals(coupon, supplied);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(3, a.getStock());
                assertEquals(1, b.getStock());
                if (path.equals("pricingFailure")) {
                    throw new ArithmeticException();
                }
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(17, grams);
                assertEquals(93L, goods);
                assertEquals(3, a.getStock());
                assertEquals(1, b.getStock());
                if (path.equals("shippingFailure")) {
                    throw new UnsupportedOperationException();
                }
                return 11;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        if (path.equals("success")) {
            Order order = service.placeOrder(" X ", cart, coupon, Region.PROVINCE, t);
            assertEquals(" X ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
            assertEquals(OrderStatus.PENDING, order.getStatus());
            assertEquals(100L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(104L, order.getTotal());
            assertEquals(1, a.getStock());
            assertEquals(0, b.getStock());
        } else {
            if (path.equals("pricingFailure")) {
                assertThrows(ArithmeticException.class,
                    () -> service.placeOrder(" X ", cart, coupon, Region.PROVINCE, t));
            } else {
                assertThrows(UnsupportedOperationException.class,
                    () -> service.placeOrder(" X ", cart, coupon, Region.PROVINCE, t));
            }
            assertEquals(3, a.getStock());
            assertEquals(1, b.getStock());
        }
        assertEquals(path.equals("pricingFailure") ? List.of("pricing") : List.of("pricing", "shipping"), calls);
        assertEquals(List.of(a, b), cart.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 1), cart.getItems().stream().map(CartItem::getQuantity).toList());
    }
}
