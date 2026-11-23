package shop;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainSmokeTest {
    @Test
    void productKeepsItsFieldsAndAdjustsCurrentStock() {
        Product product = new Product(" P1 ", " Pen ", 30_000, 500, 7);
        assertEquals(" P1 ", product.getId());
        assertEquals(" Pen ", product.getName());
        assertEquals(30_000, product.getPrice());
        assertEquals(500, product.getWeightGrams());
        product.reduceStock(7);
        assertEquals(0, product.getStock());
        product.restoreStock(8);
        assertEquals(8, product.getStock());
    }

    @Test
    void invalidStockOperationsPreserveStock() {
        assertThrows(IllegalArgumentException.class, () -> new Product(" ", "Pen", 10, 1, 0));
        Product product = new Product("P1", "Pen", 10, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(0));
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(2));
        assertThrows(IllegalArgumentException.class, () -> product.restoreStock(-1));
        assertEquals(1, product.getStock());
    }

    @Test
    void couponKeepsItsTerms() {
        LocalDate expiry = LocalDate.of(2026, 11, 23);
        Coupon coupon = new Coupon(" Sale ", CouponType.FIXED, 150, 30, 1, expiry);
        assertEquals(" Sale ", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(150, coupon.getValue());
        assertEquals(30, coupon.getMinOrder());
        assertEquals(1, coupon.getMaxDiscount());
        assertEquals(expiry, coupon.getExpiry());
        assertThrows(IllegalArgumentException.class,
                () -> new Coupon("Sale", CouponType.PERCENT, 101, 0, 0, expiry));
    }

    @Test
    void lineKeepsProductAndCalculatesTotalAsLong() {
        Product product = new Product("P1", "Pen", 1_000_000_000, 5, 0);
        CartItem item = new CartItem(product, 3);
        assertSame(product, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(3_000_000_000L, item.getLineTotal());
        assertThrows(IllegalArgumentException.class, () -> new CartItem(product, 0));
    }

    @Test
    void enumConstantsCanBeLookedUpByName() {
        assertArrayEquals(new Region[]{Region.INNER_CITY, Region.SUBURB, Region.PROVINCE, Region.REMOTE},
                Region.values());
        assertArrayEquals(new CouponType[]{CouponType.PERCENT, CouponType.FIXED}, CouponType.values());
        assertArrayEquals(new OrderStatus[]{OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED,
                OrderStatus.CANCELLED}, OrderStatus.values());
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertThrows(NullPointerException.class, () -> Region.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf("remote"));
    }
}
