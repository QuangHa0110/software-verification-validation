package shop;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainSmokeTest {

    @Test
    void enumsValuesAndValueOfFollowSpecExamples() {
        Region[] regions = Region.values();
        assertEquals(4, regions.length);
        assertEquals(Region.INNER_CITY, regions[0]);
        assertEquals(Region.REMOTE, regions[3]);
        regions[0] = Region.REMOTE;
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("inner_city"));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf(" SUBURB "));

        CouponType[] types = CouponType.values();
        assertEquals(2, types.length);
        assertEquals(CouponType.PERCENT, types[0]);
        assertEquals(CouponType.FIXED, types[1]);
        types[0] = CouponType.FIXED;
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertThrows(NullPointerException.class, () -> CouponType.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> CouponType.valueOf("fixed"));

        OrderStatus[] statuses = OrderStatus.values();
        assertEquals(4, statuses.length);
        assertEquals(OrderStatus.PENDING, statuses[0]);
        assertEquals(OrderStatus.CANCELLED, statuses[3]);
        statuses[0] = OrderStatus.PAID;
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("UNKNOWN"));
    }

    @Test
    void customExceptionsAcceptAnyMessageIncludingNull() {
        RuntimeException e1 = new InsufficientStockException("0");
        RuntimeException e2 = new InsufficientStockException(null);
        assertInstanceOf(InsufficientStockException.class, e1);
        assertInstanceOf(InsufficientStockException.class, e2);

        RuntimeException e3 = new CouponExpiredException("1");
        RuntimeException e4 = new CouponExpiredException(null);
        assertInstanceOf(CouponExpiredException.class, e3);
        assertInstanceOf(CouponExpiredException.class, e4);

        RuntimeException e5 = new CouponNotApplicableException("2");
        RuntimeException e6 = new CouponNotApplicableException(null);
        assertInstanceOf(CouponNotApplicableException.class, e5);
        assertInstanceOf(CouponNotApplicableException.class, e6);
    }

    @Test
    void productExamplesAndGetters() {
        Product a = new Product("P1", "Bút 1", 1, 1, 0);
        assertEquals("P1", a.getId());
        assertEquals("Bút 1", a.getName());
        assertEquals(1L, a.getPrice());
        assertEquals(1, a.getWeightGrams());
        assertEquals(0, a.getStock());

        Product b = new Product(" P2 ", "Vở 2", 30000, 500, 7);
        assertEquals(" P2 ", b.getId());
        assertEquals("Vở 2", b.getName());
        assertEquals(30000L, b.getPrice());
        assertEquals(500, b.getWeightGrams());
        assertEquals(7, b.getStock());

        b.reduceStock(2);
        assertEquals(5, b.getStock());
    }

    @Test
    void productConstructorRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "A", 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("   ", "A", 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", null, 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "  ", 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product(" P2 ", "Vở 2", 0, 500, -1));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "A", -10, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "A", 1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "A", 1, 1, -1));
    }

    @Test
    void productReduceAndRestoreStockExamples() {
        Product p1 = new Product("P1", "Item", 100, 10, 7);
        p1.reduceStock(7);
        assertEquals(0, p1.getStock());

        Product p2 = new Product("P2", "Item", 100, 10, 1);
        assertThrows(InsufficientStockException.class, () -> p2.reduceStock(2));
        assertEquals(1, p2.getStock());

        Product p3 = new Product("P3", "Item", 100, 10, 0);
        assertThrows(IllegalArgumentException.class, () -> p3.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> p3.reduceStock(-1));
        assertEquals(0, p3.getStock());

        Product p4 = new Product("P4", "Item", 100, 10, 50);
        p4.restoreStock(2);
        assertEquals(52, p4.getStock());

        assertThrows(IllegalArgumentException.class, () -> p3.restoreStock(0));
        assertThrows(IllegalArgumentException.class, () -> p3.restoreStock(-5));
        assertEquals(0, p3.getStock());
    }

    @Test
    void couponExamplesAndValidation() {
        Coupon a = new Coupon("C1", CouponType.PERCENT, 100, 0, 0, LocalDate.of(2026, 11, 23));
        assertEquals("C1", a.getCode());
        assertEquals(CouponType.PERCENT, a.getType());
        assertEquals(100L, a.getValue());
        assertEquals(0L, a.getMinOrder());
        assertEquals(0L, a.getMaxDiscount());
        assertEquals(LocalDate.of(2026, 11, 23), a.getExpiry());

        Coupon b = new Coupon("C2", CouponType.FIXED, 150, 30, 1, LocalDate.of(2026, 11, 24));
        assertEquals("C2", b.getCode());
        assertEquals(CouponType.FIXED, b.getType());
        assertEquals(150L, b.getValue());
        assertEquals(30L, b.getMinOrder());
        assertEquals(1L, b.getMaxDiscount());
        assertEquals(LocalDate.of(2026, 11, 24), b.getExpiry());

        LocalDate expiry = LocalDate.of(2026, 11, 24);
        assertThrows(IllegalArgumentException.class, () -> new Coupon("   ", CouponType.FIXED, 150, 30, 1, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C2", null, 150, 30, 1, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C2", CouponType.PERCENT, 0, 0, 0, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C2", CouponType.PERCENT, 101, 0, 0, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C2", CouponType.FIXED, 150, -1, 1, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C2", CouponType.FIXED, 150, 30, -1, expiry));
        assertThrows(IllegalArgumentException.class, () -> new Coupon("C2", CouponType.FIXED, 150, 30, 1, null));
    }

    @Test
    void cartItemExamplesAndValidation() {
        Product p1 = new Product("P1", "Bút 1", 1, 1, 0);
        CartItem itemA = new CartItem(p1, 1);
        assertSame(p1, itemA.getProduct());
        assertEquals(1, itemA.getQuantity());
        assertEquals(1L, itemA.getLineTotal());
        assertEquals(0, p1.getStock());

        Product p2 = new Product("P2", "Vở 2", 30000, 500, 7);
        CartItem itemB = new CartItem(p2, 2);
        assertSame(p2, itemB.getProduct());
        assertEquals(2, itemB.getQuantity());
        assertEquals(60000L, itemB.getLineTotal());
        assertEquals(7, p2.getStock());

        Product expensive = new Product("P3", "Expensive", 1_000_000_000L, 100, 5);
        CartItem bigItem = new CartItem(expensive, 3);
        assertEquals(3_000_000_000L, bigItem.getLineTotal());

        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p1, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p1, -1));
    }

    @Test
    void orderPackagePrivateConstructorGettersAndSetStatus() {
        Product p2 = new Product("P2", "Vở 2", 30000, 500, 7);
        List<CartItem> sourceA = new ArrayList<>();
        sourceA.add(new CartItem(p2, 2));
        LocalDateTime t1 = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order orderA = new Order("D1", sourceA, 60000, 6000, 15000, 69000, t1, Region.INNER_CITY);
        sourceA.clear();

        assertEquals("D1", orderA.getId());
        assertEquals(1, orderA.getItems().size());
        assertSame(p2, orderA.getItems().get(0).getProduct());
        assertEquals(2, orderA.getItems().get(0).getQuantity());
        assertEquals(60000L, orderA.getSubtotal());
        assertEquals(6000L, orderA.getDiscount());
        assertEquals(15000L, orderA.getShipping());
        assertEquals(69000L, orderA.getTotal());
        assertEquals(OrderStatus.PENDING, orderA.getStatus());
        assertEquals(t1, orderA.getCreatedAt());
        assertEquals(Region.INNER_CITY, orderA.getRegion());
        assertThrows(UnsupportedOperationException.class, () -> orderA.getItems().clear());

        orderA.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, orderA.getStatus());
        assertEquals(69000L, orderA.getTotal());
        assertThrows(IllegalArgumentException.class, () -> orderA.setStatus(null));
        assertEquals(OrderStatus.PAID, orderA.getStatus());

        Product p3 = new Product("P3", "Hàng 3", 500000, 500, 5);
        List<CartItem> itemsB = List.of(new CartItem(p3, 1));
        LocalDateTime t2 = LocalDateTime.of(2026, 11, 24, 9, 30);
        Order orderB = new Order("D2", itemsB, 500000, 0, 0, 500000, t2, Region.SUBURB);
        assertEquals("D2", orderB.getId());
        assertEquals(500000L, orderB.getSubtotal());
        assertEquals(0L, orderB.getDiscount());
        assertEquals(0L, orderB.getShipping());
        assertEquals(500000L, orderB.getTotal());
        assertEquals(OrderStatus.PENDING, orderB.getStatus());
        orderB.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, orderB.getStatus());
        orderB.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, orderB.getStatus());
        assertEquals(500000L, orderB.getTotal());

        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", itemsB, 500000, 0, 0, 499999, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order(" ", itemsB, 500000, 0, 0, 500000, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", null, 500000, 0, 0, 500000, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", List.of(), 0, 0, 0, 0, t2, Region.SUBURB));
        List<CartItem> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", listWithNull, 0, 0, 0, 0, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", itemsB, 400000, 0, 0, 400000, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", itemsB, 500000, -1, 0, 500001, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", itemsB, 500000, 500001, 0, -1, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", itemsB, 500000, 0, -1, 499999, t2, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", itemsB, 500000, 0, 0, 500000, null, Region.SUBURB));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D2", itemsB, 500000, 0, 0, 500000, t2, null));
    }
}
