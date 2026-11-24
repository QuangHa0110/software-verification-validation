package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @ParameterizedTest
    @ValueSource(strings = {"expired", "belowMinimum", "overweight"})
    void failedStandardCheckoutLeavesAllStockAndCartLinesIntact(String failure) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0);
        Product a = new Product("A", "A", 10, 10_000, 1);
        Product b = new Product("B", "B", 20, 10_001, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        OrderService service = new OrderService();
        if (failure.equals("expired")) {
            Coupon expired = new Coupon("E", CouponType.PERCENT, 10, 31, 0, t.toLocalDate().minusDays(1));
            assertThrows(CouponExpiredException.class,
                () -> service.placeOrder("D", cart, expired, Region.REMOTE, t));
        } else if (failure.equals("belowMinimum")) {
            Coupon restricted = new Coupon("M", CouponType.FIXED, 2, 31, 0, t.toLocalDate());
            assertThrows(CouponNotApplicableException.class,
                () -> service.placeOrder("D", cart, restricted, Region.REMOTE, t));
        } else {
            assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D", cart, null, Region.REMOTE, t));
        }
        assertEquals(1, a.getStock());
        assertEquals(2, b.getStock());
        assertEquals(List.of(a, b), cart.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(1, 1), cart.getItems().stream().map(CartItem::getQuantity).toList());
    }
}
