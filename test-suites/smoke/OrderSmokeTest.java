package shop;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderSmokeTest {
    private final LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
    private final OrderService service = new OrderService();
    private final Product pen = new Product("P", "Pen", 250_000, 500, 2);
    private final Cart cart = new Cart();
    private final Coupon coupon = new Coupon("Sale", CouponType.FIXED, 1, 0, 0, now.toLocalDate());

    @Test
    void placedOrderKeepsItsAmountsAndLinesThroughPaymentAndShipping() {
        cart.addItem(pen, 2);
        Order order = service.placeOrder(" D1 ", cart, coupon, Region.INNER_CITY, now);
        assertEquals(" D1 ", order.getId());
        assertEquals(500_000, order.getSubtotal());
        assertEquals(1, order.getDiscount());
        assertEquals(15_000, order.getShipping());
        assertEquals(514_999, order.getTotal());
        assertEquals(now, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, pen.getStock());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.removeItem("P");
        assertSame(pen, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertThrows(UnsupportedOperationException.class, () -> order.getItems().clear());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now.minusDays(1)));
        assertEquals(0, pen.getStock());
    }

    @Test
    void latePaidCancellationRefundsGoodsAndRestoresCurrentStockOnce() {
        cart.addItem(pen, 2);
        Order order = service.placeOrder("D1", cart, coupon, Region.INNER_CITY, now);
        service.pay(order);
        pen.restoreStock(3);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, now.minusNanos(1)));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(3, pen.getStock());
        assertEquals(399_999, service.cancelOrder(order, now.plusHours(24).plusNanos(1)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, now.plusDays(2)));
        assertEquals(5, pen.getStock());
    }

    @Test
    void invalidLifecycleCallsPreserveTheOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        cart.addItem(pen, 1);
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, now);
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        service.pay(order);
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void invalidOrderHeaderPrecedesEmptyCart() {
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(" ", cart, null, Region.SUBURB, now));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("D", cart, null, Region.SUBURB, now));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void checkoutFailuresKeepEveryLineAndStock() {
        Product heavy = new Product("B", "Books", 20, 19_501, 1);
        cart.addItem(pen, 1);
        cart.addItem(heavy, 1);
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.REMOTE, now));
        Coupon ineligible = new Coupon("C", CouponType.FIXED, 10, 300_000, 0, now.toLocalDate());
        assertThrows(CouponNotApplicableException.class,
                () -> service.placeOrder("D", cart, ineligible, Region.REMOTE, now));
        assertThrows(CouponExpiredException.class,
                () -> service.placeOrder("D", cart, ineligible, Region.REMOTE, now.plusDays(1)));
        assertEquals(1, heavy.getStock());
        heavy.reduceStock(1);
        assertThrows(InsufficientStockException.class,
                () -> service.placeOrder("D", cart, ineligible, Region.REMOTE, now.plusDays(1)));
        assertEquals(2, pen.getStock());
        assertEquals(0, heavy.getStock());
        assertEquals(2, cart.getItems().size());
        assertEquals(250_020, cart.getSubtotal());
    }

    @Test
    void injectedShippingFailurePropagatesBeforeStockIsReduced() {
        RuntimeException failure = new IllegalStateException("shipping unavailable");
        ShippingCalculator calculator = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                assertEquals(249_999, orderValue);
                throw failure;
            }
        };
        OrderService injected = new OrderService(new PricingService(), calculator);
        cart.addItem(pen, 1);
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> injected.placeOrder("D", cart, coupon, Region.SUBURB, now)));
        assertEquals(2, pen.getStock());
    }
}
