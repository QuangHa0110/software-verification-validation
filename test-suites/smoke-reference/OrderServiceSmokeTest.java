package shop;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceSmokeTest {

    private static final LocalDateTime T = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

    @Test
    void defaultAndInjectedConstructorsExamples() {
        OrderService s1 = new OrderService();
        Cart c1 = new Cart();
        Product p1 = new Product("P1", "Món 1", 1, 1, 5);
        c1.addItem(p1, 1);
        Order o1 = s1.placeOrder("O1", c1, null, Region.INNER_CITY, T);
        assertEquals(0L, o1.getDiscount());
        assertEquals(15000L, o1.getShipping());
        assertEquals(15001L, o1.getTotal());

        OrderService s2 = new OrderService();
        Cart c2 = new Cart();
        Product p2 = new Product("P2", "Món 2", 500000, 1, 5);
        c2.addItem(p2, 1);
        Order o2 = s2.placeOrder("O2", c2, null, Region.INNER_CITY, T);
        assertEquals(0L, o2.getDiscount());
        assertEquals(0L, o2.getShipping());
        assertEquals(500000L, o2.getTotal());

        // Null dependency rejected
        assertThrows(IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
        assertThrows(IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));

        // Injected stubs returning 7 and 11, 0 calls in constructor
        AtomicInteger pricingCalls = new AtomicInteger();
        AtomicInteger shippingCalls = new AtomicInteger();
        AtomicLong capturedSubtotal = new AtomicLong(-1);
        AtomicLong capturedOrderValue = new AtomicLong(-1);
        PricingService stubPricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                pricingCalls.incrementAndGet();
                capturedSubtotal.set(subtotal);
                return 7L;
            }
        };
        ShippingCalculator stubShipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                shippingCalls.incrementAndGet();
                capturedOrderValue.set(orderValue);
                return 11L;
            }
        };
        OrderService injected = new OrderService(stubPricing, stubShipping);
        assertEquals(0, pricingCalls.get());
        assertEquals(0, shippingCalls.get());

        Cart c3 = new Cart();
        c3.addItem(new Product("P3", "Món 3", 100, 1, 2), 1);
        Order o3 = injected.placeOrder("O3", c3, null, Region.INNER_CITY, T);
        assertEquals(1, pricingCalls.get());
        assertEquals(1, shippingCalls.get());
        assertEquals(100L, capturedSubtotal.get());
        assertEquals(93L, capturedOrderValue.get());
        assertEquals(104L, o3.getTotal());
    }

    @Test
    void placeOrderExamplesD1ToD4AndInputErrors() {
        OrderService service = new OrderService();

        // D1: subtotal 500000, FIXED 1 -> goods 499999 -> shipping 15000 -> total 514999
        Product a1 = new Product("A", "A", 250000, 500, 2);
        Cart cart1 = new Cart();
        cart1.addItem(a1, 2);
        Coupon fixed1 = new Coupon("F1", CouponType.FIXED, 1, 0, 0, T.toLocalDate());
        Order d1 = service.placeOrder("D1", cart1, fixed1, Region.INNER_CITY, T);
        assertEquals("D1", d1.getId());
        assertEquals(500000L, d1.getSubtotal());
        assertEquals(1L, d1.getDiscount());
        assertEquals(15000L, d1.getShipping());
        assertEquals(514999L, d1.getTotal());
        assertEquals(OrderStatus.PENDING, d1.getStatus());
        assertEquals(T, d1.getCreatedAt());
        assertEquals(Region.INNER_CITY, d1.getRegion());
        assertEquals(0, a1.getStock());
        assertEquals(2, cart1.getItems().get(0).getQuantity());

        // D2: no coupon, SUBURB -> free shipping, total 500000; mutating cart does not alter order
        Product a2 = new Product("A", "A", 250000, 500, 2);
        Cart cart2 = new Cart();
        cart2.addItem(a2, 2);
        Order d2 = service.placeOrder("D2", cart2, null, Region.SUBURB, T);
        assertEquals(500000L, d2.getTotal());
        assertEquals(0L, d2.getShipping());
        assertEquals(0, a2.getStock());
        cart2.removeItem("A");
        assertEquals(1, d2.getItems().size());
        assertEquals(2, d2.getItems().get(0).getQuantity());

        // D3: overweight 20001g -> IAE, stock untouched, cart untouched
        Product a3 = new Product("A", "A", 10, 10000, 1);
        Product b3 = new Product("B", "B", 20, 10001, 2);
        Cart cart3 = new Cart();
        cart3.addItem(a3, 1);
        cart3.addItem(b3, 1);
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D3", cart3, null, Region.REMOTE, T));
        assertEquals(1, a3.getStock());
        assertEquals(2, b3.getStock());
        assertEquals(2, cart3.getItems().size());

        // D4: B stock dropped to 1, expired coupon -> InsufficientStockException before coupon check
        Product a4 = new Product("A", "A", 100, 10, 1);
        Product b4 = new Product("B", "B", 100, 10, 2);
        Cart cart4 = new Cart();
        cart4.addItem(a4, 1);
        cart4.addItem(b4, 2);
        b4.reduceStock(1);
        Coupon expired = new Coupon("EXP", CouponType.FIXED, 10, 0, 0, T.toLocalDate().minusDays(1));
        assertThrows(InsufficientStockException.class,
                () -> service.placeOrder("D4", cart4, expired, Region.INNER_CITY, T));
        assertEquals(1, a4.getStock());
        assertEquals(1, b4.getStock());

        // Input validation order and coupon errors
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(" ", null, null, Region.INNER_CITY, T));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D5", null, null, null, T));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("D5", null, null, Region.INNER_CITY, null));
        assertThrows(IllegalStateException.class,
                () -> service.placeOrder("D5", null, null, Region.INNER_CITY, T));
        assertThrows(IllegalStateException.class,
                () -> service.placeOrder("D5", new Cart(), null, Region.INNER_CITY, T));

        Product a5 = new Product("A", "A", 100, 10, 2);
        Cart cart5 = new Cart();
        cart5.addItem(a5, 1);
        assertThrows(CouponExpiredException.class,
                () -> service.placeOrder("D6", cart5, expired, Region.INNER_CITY, T));
        assertEquals(2, a5.getStock());

        Coupon highMin = new Coupon("MIN", CouponType.FIXED, 10, 500, 0, T.toLocalDate());
        assertThrows(CouponNotApplicableException.class,
                () -> service.placeOrder("D7", cart5, highMin, Region.INNER_CITY, T));
        assertEquals(2, a5.getStock());
    }

    @Test
    void payAndShipExamplesAndInvalidStates() {
        OrderService service = new OrderService();

        // pay examples using D1
        Product a1 = new Product("A", "A", 250000, 500, 2);
        Cart c1 = new Cart();
        c1.addItem(a1, 2);
        Coupon fixed1 = new Coupon("F1", CouponType.FIXED, 1, 0, 0, T.toLocalDate());
        Order d1 = service.placeOrder("D1", c1, fixed1, Region.INNER_CITY, T);
        service.pay(d1);
        assertEquals(OrderStatus.PAID, d1.getStatus());
        assertEquals(514999L, d1.getTotal());
        assertEquals(0, a1.getStock());

        assertThrows(IllegalStateException.class, () -> service.pay(d1));
        assertEquals(OrderStatus.PAID, d1.getStatus());
        assertEquals(514999L, d1.getTotal());
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));

        // ship examples using D2
        Product a2 = new Product("A", "A", 250000, 500, 2);
        Cart c2 = new Cart();
        c2.addItem(a2, 2);
        Order d2 = service.placeOrder("D2", c2, null, Region.SUBURB, T);
        assertThrows(IllegalStateException.class, () -> service.ship(d2));
        assertEquals(OrderStatus.PENDING, d2.getStatus());
        assertEquals(0, a2.getStock());

        service.pay(d2);
        service.ship(d2);
        assertEquals(OrderStatus.SHIPPED, d2.getStatus());
        assertEquals(500000L, d2.getTotal());
        assertEquals(0, a2.getStock());

        assertThrows(IllegalStateException.class, () -> service.ship(d2));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
    }

    @Test
    void cancelOrderAllTableExamplesAndEdgeCases() {
        OrderService service = new OrderService();
        Coupon fixed1 = new Coupon("F1", CouponType.FIXED, 1, 0, 0, T.toLocalDate());

        // Row 1: PENDING at T, stock 0 -> refund 0, stock 2, CANCELLED
        Product p1 = new Product("A", "A", 250000, 500, 2);
        Cart c1 = new Cart();
        c1.addItem(p1, 2);
        Order o1 = service.placeOrder("D1", c1, fixed1, Region.INNER_CITY, T);
        long r1 = service.cancelOrder(o1, T);
        assertEquals(0L, r1);
        assertEquals(2, p1.getStock());
        assertEquals(OrderStatus.CANCELLED, o1.getStatus());
        // Second cancel fails and does not double-restore stock
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(o1, T));
        assertEquals(2, p1.getStock());

        // Row 2: PAID at T + 24h exact -> refund 514999, stock 2, CANCELLED
        Product p2 = new Product("A", "A", 250000, 500, 2);
        Cart c2 = new Cart();
        c2.addItem(p2, 2);
        Order o2 = service.placeOrder("D1", c2, fixed1, Region.INNER_CITY, T);
        service.pay(o2);
        long r2 = service.cancelOrder(o2, LocalDateTime.of(2026, 11, 24, 8, 0, 0));
        assertEquals(514999L, r2);
        assertEquals(2, p2.getStock());
        assertEquals(OrderStatus.CANCELLED, o2.getStatus());

        // Row 3: PAID at T + 24h + 1s, stock restocked to 3 -> refund 399999, stock 5, CANCELLED
        Product p3 = new Product("A", "A", 250000, 500, 2);
        Cart c3 = new Cart();
        c3.addItem(p3, 2);
        Order o3 = service.placeOrder("D1", c3, fixed1, Region.INNER_CITY, T);
        service.pay(o3);
        p3.restoreStock(3);
        long r3 = service.cancelOrder(o3, LocalDateTime.of(2026, 11, 24, 8, 0, 1));
        assertEquals(399999L, r3);
        assertEquals(5, p3.getStock());
        assertEquals(OrderStatus.CANCELLED, o3.getStatus());

        // Row 4: PAID at T - 1s -> IAE, still PAID, stock 0
        Product p4 = new Product("A", "A", 250000, 500, 2);
        Cart c4 = new Cart();
        c4.addItem(p4, 2);
        Order o4 = service.placeOrder("D1", c4, fixed1, Region.INNER_CITY, T);
        service.pay(o4);
        assertThrows(IllegalArgumentException.class,
                () -> service.cancelOrder(o4, LocalDateTime.of(2026, 11, 23, 7, 59, 59)));
        assertEquals(OrderStatus.PAID, o4.getStatus());
        assertEquals(0, p4.getStock());

        // Row 5: SHIPPED at T - 1s -> IllegalStateException (step 2 before step 3), stock 0
        Product p5 = new Product("A", "A", 250000, 500, 2);
        Cart c5 = new Cart();
        c5.addItem(p5, 2);
        Order o5 = service.placeOrder("D1", c5, fixed1, Region.INNER_CITY, T);
        service.pay(o5);
        service.ship(o5);
        assertThrows(IllegalStateException.class,
                () -> service.cancelOrder(o5, LocalDateTime.of(2026, 11, 23, 7, 59, 59)));
        assertEquals(OrderStatus.SHIPPED, o5.getStatus());
        assertEquals(0, p5.getStock());

        // Null checks (including null now on SHIPPED -> IAE at step 1)
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null, T));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(o5, null));
    }
}
