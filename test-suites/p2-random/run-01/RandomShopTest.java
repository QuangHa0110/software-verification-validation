package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p990331", "sku315552", 789475L, 572, 69);
        assertEquals("p990331", p.getId());
        assertEquals("sku315552", p.getName());
        assertEquals(789475L, p.getPrice());
        assertEquals(572, p.getWeightGrams());
        p.restoreStock(53);
        assertEquals(122, p.getStock());
        p.reduceStock(12);
        assertEquals(110, p.getStock());
        p.reduceStock(4);
        assertEquals(106, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p937047", "sku226715", 165389L, 280, 41);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -4));
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a353424", "sku93874", 937155L, 516, 71);
        var b = new Product("b234374", "sku814076", 479181L, 8, 28);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a353424", 5));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b234374"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a353424"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b234374"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b234374"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a353424", 16));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample4() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.FIXED, 516451L, 476246L, 110931L, null));
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code303536", CouponType.FIXED, 595339L, 315614L, 243277L, LocalDate.parse("2027-07-19"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(55106L, coupon, LocalDate.parse("2027-08-15")));
    }

    @Test
    void sample6() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.SUBURB, 21873, 108619L));
    }

    @Test
    void sample7() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p754355", "sku892463", 627722L, 1420, 25);
        var coupon = new Coupon("code250638", CouponType.FIXED, 530436L, 406115L, 32596L, LocalDate.parse("2024-12-21"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order830375", null, coupon, Region.SUBURB, LocalDateTime.parse("2025-01-20T16:08")));
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-01-21T04:50")));
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-01-23T07:56")));
        assertEquals(25, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown67047"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message599892").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message599892").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message599892").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        var p = new Product("p702710", "sku876165", 487866L, 12509, 21);
        assertEquals("p702710", p.getId());
        assertEquals("sku876165", p.getName());
        assertEquals(487866L, p.getPrice());
        assertEquals(12509, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(51));
        assertEquals(21, p.getStock());
        p.restoreStock(17);
        assertEquals(38, p.getStock());
        p.reduceStock(14);
        assertEquals(24, p.getStock());
        p.reduceStock(23);
        assertEquals(1, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(26));
        assertEquals(1, p.getStock());
    }

    @Test
    void sample10() {
        var p = new Product("p114098", "sku813102", 719785L, 916, 9);
        var item = new CartItem(p, 34);
        assertEquals("p114098", item.getProduct().getId());
        assertEquals(34, item.getQuantity());
        assertEquals(24472690L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a175407", "sku437823", 810114L, 79, 64);
        var b = new Product("b75415", "sku668500", 295386L, 297, 21);
        cart.addItem(b, 20);
        assertEquals(5907720L, cart.getSubtotal());
        assertEquals(5940, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b75415", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(5907720L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a175407", 10));
        assertEquals(5907720L, cart.getSubtotal());
        assertEquals(5940, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b75415", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(5907720L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b75415");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b75415", 39));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a175407", 13));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 56));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b75415"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample12() {
        var coupon = new Coupon("code328564", CouponType.PERCENT, 33L, 480759L, 97696L, LocalDate.parse("2025-06-16"));
        assertEquals("code328564", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(33L, coupon.getValue());
        assertEquals(480759L, coupon.getMinOrder());
        assertEquals(97696L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-06-16"), coupon.getExpiry());
    }

    @Test
    void sample13() {
        var coupon = new Coupon("code231377", CouponType.PERCENT, 12L, 172545L, 119686L, LocalDate.parse("2025-08-30"));
        assertThrows(shop.CouponNotApplicableException.class, () -> new PricingService().calculateDiscount(21556L, coupon, LocalDate.parse("2025-08-13")));
    }

    @Test
    void sample14() {
        assertEquals(109000L, new ShippingCalculator().calculate(Region.PROVINCE, 19465, 118694L));
    }

    @Test
    void sample15() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p471485", "sku440703", 305335L, 947, 40);
        cart.addItem(p, 17);
        var coupon = new Coupon("code378791", CouponType.PERCENT, 35L, 120934L, 264495L, LocalDate.parse("2025-12-19"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(" ", cart, coupon, Region.REMOTE, LocalDateTime.parse("2025-11-06T04:01")));
        assertEquals(40, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(40, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(40, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-11-06T19:42")));
        assertEquals(40, p.getStock());
    }

    @Test
    void sample16() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown700583"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown678159"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message191803").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message191803").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message191803").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p580517", "sku672174", 563328L, 3660, 35);
        assertEquals("p580517", p.getId());
        assertEquals("sku672174", p.getName());
        assertEquals(563328L, p.getPrice());
        assertEquals(3660, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-2));
        assertEquals(35, p.getStock());
        p.restoreStock(3);
        assertEquals(38, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(59));
        assertEquals(38, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(60));
        assertEquals(38, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-5));
        assertEquals(38, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p901608", "sku134814", 625061L, 1053, 3);
        var item = new CartItem(p, 43);
        assertEquals("p901608", item.getProduct().getId());
        assertEquals(43, item.getQuantity());
        assertEquals(26877623L, item.getLineTotal());
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a504460", "sku720626", 109661L, 397, 6);
        var b = new Product("b570541", "sku663441", 442730L, 539, 73);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a504460", 39));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b570541", 18));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 4);
        assertEquals(1770920L, cart.getSubtotal());
        assertEquals(2156, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b570541", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(1770920L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample20() {
        var coupon = new Coupon("code640783", CouponType.FIXED, 292612L, 522940L, 6496L, LocalDate.parse("2025-01-09"));
        assertEquals("code640783", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(292612L, coupon.getValue());
        assertEquals(522940L, coupon.getMinOrder());
        assertEquals(6496L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-01-09"), coupon.getExpiry());
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code950641", CouponType.PERCENT, 13L, 165612L, 273003L, LocalDate.parse("2024-06-18"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(552997L, coupon, LocalDate.parse("2024-06-30")));
    }

    @Test
    void sample22() {
        assertEquals(111000L, new ShippingCalculator().calculate(Region.PROVINCE, 19727, 991406L));
    }

    @Test
    void sample23() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p895500", "sku837520", 252202L, 692, 27);
        cart.addItem(p, 1);
        var coupon = new Coupon("code561890", CouponType.PERCENT, 2L, 2838L, 133769L, LocalDate.parse("2024-08-20"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order970394", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2024-09-26T02:46")));
        assertEquals(27, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(27, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(27, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown241807"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown239237"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message120263").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message120263").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message120263").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        var p = new Product("p957245", "sku359933", 951583L, 4619, 50);
        assertEquals("p957245", p.getId());
        assertEquals("sku359933", p.getName());
        assertEquals(951583L, p.getPrice());
        assertEquals(4619, p.getWeightGrams());
        p.reduceStock(40);
        assertEquals(10, p.getStock());
        p.restoreStock(9);
        assertEquals(19, p.getStock());
    }

    @Test
    void sample26() {
        var p = new Product("p670034", "sku38707", 124597L, 546, 36);
        var item = new CartItem(p, 22);
        assertEquals("p670034", item.getProduct().getId());
        assertEquals(22, item.getQuantity());
        assertEquals(2741134L, item.getLineTotal());
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a660057", "sku257959", 245870L, 611, 38);
        var b = new Product("b546611", "sku535787", 503488L, 686, 55);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a660057"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 13));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 4);
        assertEquals(983480L, cart.getSubtotal());
        assertEquals(2444, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a660057", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(983480L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b546611", 10));
        assertEquals(983480L, cart.getSubtotal());
        assertEquals(2444, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a660057", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(983480L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("a660057", 30);
        assertEquals(7376100L, cart.getSubtotal());
        assertEquals(18330, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a660057", cart.getItems().get(0).getProduct().getId());
        assertEquals(30, cart.getItems().get(0).getQuantity());
        assertEquals(7376100L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other70047", 49));
        assertEquals(7376100L, cart.getSubtotal());
        assertEquals(18330, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a660057", cart.getItems().get(0).getProduct().getId());
        assertEquals(30, cart.getItems().get(0).getQuantity());
        assertEquals(7376100L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample28() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 115L, 238657L, 11472L, LocalDate.parse("2025-07-30")));
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code290092", CouponType.PERCENT, 3L, 395680L, 242672L, LocalDate.parse("2025-03-07"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(277192L, coupon, null));
    }

    @Test
    void sample30() {
        assertEquals(47000L, new ShippingCalculator().calculate(Region.PROVINCE, 3763, 1006921L));
    }

    @Test
    void sample31() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
    }

    @Test
    void sample32() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown530471"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown174884"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message130704").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message130704").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message130704").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p663804", "sku973966", 726709L, 7357, 39);
        assertEquals("p663804", p.getId());
        assertEquals("sku973966", p.getName());
        assertEquals(726709L, p.getPrice());
        assertEquals(7357, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(0));
        assertEquals(39, p.getStock());
        p.restoreStock(7);
        assertEquals(46, p.getStock());
        p.restoreStock(23);
        assertEquals(69, p.getStock());
        p.restoreStock(28);
        assertEquals(97, p.getStock());
        p.reduceStock(11);
        assertEquals(86, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p502258", "sku158802", 689762L, 544, 44);
        var item = new CartItem(p, 40);
        assertEquals("p502258", item.getProduct().getId());
        assertEquals(40, item.getQuantity());
        assertEquals(27590480L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a700870", "sku879683", 682624L, 172, 28);
        var b = new Product("b83235", "sku503668", 278455L, 382, 56);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 56));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 39));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b83235"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a700870", 45));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b83235", 16));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a700870"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample36() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 20L, -38509L, 114733L, LocalDate.parse("2026-04-27")));
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code530692", CouponType.PERCENT, 85L, 406620L, 153760L, LocalDate.parse("2024-11-15"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1195730L, coupon, LocalDate.parse("2024-12-30")));
    }

    @Test
    void sample38() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.SUBURB, 9248, 944640L));
    }

    @Test
    void sample39() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p791570", "sku602458", 428935L, 1380, 63);
        cart.addItem(p, 13);
        var coupon = new Coupon("code281317", CouponType.PERCENT, 20L, 177307L, 263033L, LocalDate.parse("2026-01-02"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(" ", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2025-11-08T14:40")));
        assertEquals(63, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(63, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(63, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-11-10T19:06")));
        assertEquals(63, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown690743"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message172406").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message172406").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message172406").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        var p = new Product("p483738", "sku802782", 873786L, 18821, 42);
        assertEquals("p483738", p.getId());
        assertEquals("sku802782", p.getName());
        assertEquals(873786L, p.getPrice());
        assertEquals(18821, p.getWeightGrams());
        p.reduceStock(1);
        assertEquals(41, p.getStock());
        p.restoreStock(25);
        assertEquals(66, p.getStock());
        p.reduceStock(25);
        assertEquals(41, p.getStock());
        p.restoreStock(51);
        assertEquals(92, p.getStock());
        p.reduceStock(4);
        assertEquals(88, p.getStock());
    }

    @Test
    void sample42() {
        var p = new Product("p976338", "sku796157", 39420L, 1191, 71);
        var item = new CartItem(p, 22);
        assertEquals("p976338", item.getProduct().getId());
        assertEquals(22, item.getQuantity());
        assertEquals(867240L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a835252", "sku457255", 664202L, 1308, 58);
        var b = new Product("b380654", "sku15757", 416382L, 780, 22);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other215183", 27));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 19);
        assertEquals(12619838L, cart.getSubtotal());
        assertEquals(24852, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a835252", cart.getItems().get(0).getProduct().getId());
        assertEquals(19, cart.getItems().get(0).getQuantity());
        assertEquals(12619838L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 24));
        assertEquals(12619838L, cart.getSubtotal());
        assertEquals(24852, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a835252", cart.getItems().get(0).getProduct().getId());
        assertEquals(19, cart.getItems().get(0).getQuantity());
        assertEquals(12619838L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample44() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 122L, -48274L, 20081L, LocalDate.parse("2024-02-25")));
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code312342", CouponType.PERCENT, 13L, 565979L, 49500L, LocalDate.parse("2027-02-18"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(838695L, coupon, LocalDate.parse("2027-04-17")));
    }

    @Test
    void sample46() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 7791, 1200471L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p525988", "sku837272", 648902L, 718, 72);
        cart.addItem(p, 18);
        var order = service.placeOrder("order416530", cart, null, Region.REMOTE, LocalDateTime.parse("2027-07-27T17:06"));
        assertEquals(54, p.getStock());
        assertEquals("order416530", order.getId());
        assertEquals(11680236L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(103000L, order.getShipping());
        assertEquals(11783236L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-07-27T17:06"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p525988", order.getItems().get(0).getProduct().getId());
        assertEquals(18, order.getItems().get(0).getQuantity());
        assertEquals(11680236L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(54, p.getStock());
        assertEquals(9344188L, service.cancelOrder(order, LocalDateTime.parse("2027-07-29T15:12")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(72, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown899096"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown230084"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message763916").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message763916").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message763916").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p986557", "sku1817", -29560L, 7829, 83));
    }

    @Test
    void sample50() {
        var p = new Product("p914555", "sku951356", 225331L, 545, 67);
        var item = new CartItem(p, 30);
        assertEquals("p914555", item.getProduct().getId());
        assertEquals(30, item.getQuantity());
        assertEquals(6759930L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a683539", "sku684453", 256026L, 49, 28);
        var b = new Product("b615622", "sku358817", 709405L, 325, 48);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a683539"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 9);
        assertEquals(6384645L, cart.getSubtotal());
        assertEquals(2925, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b615622", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(6384645L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a683539", 40));
        assertEquals(6384645L, cart.getSubtotal());
        assertEquals(2925, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b615622", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(6384645L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample52() {
        var coupon = new Coupon("code973220", CouponType.PERCENT, 34L, 500550L, 261535L, LocalDate.parse("2024-01-12"));
        assertEquals("code973220", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(34L, coupon.getValue());
        assertEquals(500550L, coupon.getMinOrder());
        assertEquals(261535L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-01-12"), coupon.getExpiry());
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code348350", CouponType.PERCENT, 14L, 585771L, 271461L, LocalDate.parse("2024-03-20"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1025435L, coupon, LocalDate.parse("2024-04-19")));
    }

    @Test
    void sample54() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.SUBURB, 10237, 549810L));
    }

    @Test
    void sample55() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, null));
    }

    @Test
    void sample56() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown925742"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown349598"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message258022").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message258022").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message258022").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p266025", "sku933403", 641526L, 21031, 14);
        assertEquals("p266025", p.getId());
        assertEquals("sku933403", p.getName());
        assertEquals(641526L, p.getPrice());
        assertEquals(21031, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(42));
        assertEquals(14, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(36));
        assertEquals(14, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(53));
        assertEquals(14, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(55));
        assertEquals(14, p.getStock());
        p.restoreStock(17);
        assertEquals(31, p.getStock());
    }

    @Test
    void sample58() {
        var p = new Product("p735708", "sku77942", 436591L, 460, 73);
        var item = new CartItem(p, 30);
        assertEquals("p735708", item.getProduct().getId());
        assertEquals(30, item.getQuantity());
        assertEquals(13097730L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a601182", "sku272294", 550161L, 1414, 16);
        var b = new Product("b904509", "sku914", 941765L, 326, 7);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b904509"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 31));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b904509", 7));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a601182"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample60() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, null, 385586L, 5522L, 174773L, LocalDate.parse("2026-12-22")));
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code45483", CouponType.PERCENT, 59L, 377231L, 284336L, LocalDate.parse("2025-12-14"));
        assertThrows(shop.CouponNotApplicableException.class, () -> new PricingService().calculateDiscount(165654L, coupon, LocalDate.parse("2025-11-11")));
    }

    @Test
    void sample62() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.INNER_CITY, 21604, 923425L));
    }

    @Test
    void sample63() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p767041", "sku853079", 968567L, 599, 35);
        cart.addItem(p, 5);
        var order = service.placeOrder("order286194", cart, null, Region.INNER_CITY, LocalDateTime.parse("2025-09-12T06:42"));
        assertEquals(30, p.getStock());
        assertEquals("order286194", order.getId());
        assertEquals(4842835L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(4842835L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-09-12T06:42"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p767041", order.getItems().get(0).getProduct().getId());
        assertEquals(5, order.getItems().get(0).getQuantity());
        assertEquals(4842835L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2025-09-11T22:40")));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(30, p.getStock());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2025-09-14T13:30")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(35, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(35, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(35, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message578537").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message578537").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message578537").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p243834", null, 347557L, 24787, 45));
    }

    @Test
    void sample66() {
        var p = new Product("p89390", "sku927951", 376527L, 1587, 22);
        var item = new CartItem(p, 30);
        assertEquals("p89390", item.getProduct().getId());
        assertEquals(30, item.getQuantity());
        assertEquals(11295810L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a807132", "sku835304", 527704L, 373, 69);
        var b = new Product("b548516", "sku488697", 387878L, 1460, 51);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a807132"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 19);
        assertEquals(7369682L, cart.getSubtotal());
        assertEquals(27740, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b548516", cart.getItems().get(0).getProduct().getId());
        assertEquals(19, cart.getItems().get(0).getQuantity());
        assertEquals(7369682L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b548516");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other813645", 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        var coupon = new Coupon("code502593", CouponType.FIXED, 191262L, 507672L, 184648L, LocalDate.parse("2024-10-11"));
        assertEquals("code502593", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(191262L, coupon.getValue());
        assertEquals(507672L, coupon.getMinOrder());
        assertEquals(184648L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-10-11"), coupon.getExpiry());
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code219361", CouponType.FIXED, 253280L, 82388L, 109374L, LocalDate.parse("2024-04-30"));
        assertEquals(200503L, new PricingService().calculateDiscount(200503L, coupon, LocalDate.parse("2024-04-28")));
    }

    @Test
    void sample70() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 16573, 535247L));
    }

    @Test
    void sample71() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p416653", "sku599896", 292673L, 815, 37);
        cart.addItem(p, 15);
        var order = service.placeOrder("order901337", cart, null, Region.PROVINCE, LocalDateTime.parse("2026-09-21T14:49"));
        assertEquals(22, p.getStock());
        assertEquals("order901337", order.getId());
        assertEquals(4390095L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(81000L, order.getShipping());
        assertEquals(4471095L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-09-21T14:49"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p416653", order.getItems().get(0).getProduct().getId());
        assertEquals(15, order.getItems().get(0).getQuantity());
        assertEquals(4390095L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(22, p.getStock());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2026-09-22T19:01")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(37, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown558232"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.NullPointerException.class, () -> CouponType.valueOf(null));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message655394").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message655394").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message655394").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p166485", null, 85627L, 3332, 70));
    }

    @Test
    void sample74() {
        var p = new Product("p906090", "sku593259", 233780L, 1029, 43);
        var item = new CartItem(p, 31);
        assertEquals("p906090", item.getProduct().getId());
        assertEquals(31, item.getQuantity());
        assertEquals(7247180L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a356157", "sku579504", 121419L, 1577, 14);
        var b = new Product("b629251", "sku212254", 390807L, 682, 28);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other996860", 8));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, -4));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 41));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b629251"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b629251", 18));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        var coupon = new Coupon("code685097", CouponType.PERCENT, 53L, 4923L, 27137L, LocalDate.parse("2025-05-28"));
        assertEquals("code685097", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(53L, coupon.getValue());
        assertEquals(4923L, coupon.getMinOrder());
        assertEquals(27137L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-05-28"), coupon.getExpiry());
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code836318", CouponType.PERCENT, 9L, 362118L, 80121L, LocalDate.parse("2024-08-17"));
        assertEquals(80121L, new PricingService().calculateDiscount(1178243L, coupon, LocalDate.parse("2024-08-01")));
    }

    @Test
    void sample78() {
        assertEquals(61000L, new ShippingCalculator().calculate(Region.PROVINCE, 7353, 302022L));
    }

    @Test
    void sample79() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p517351", "sku419913", 600117L, 43, 12);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 15));
        var coupon = new Coupon("code355182", CouponType.FIXED, 530050L, 34737L, 107969L, LocalDate.parse("2027-05-03"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order375494", cart, coupon, Region.SUBURB, LocalDateTime.parse("2027-03-29T01:54")));
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-04-01T13:45")));
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(12, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown492161"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown373003"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message313529").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message313529").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message313529").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p305366", "sku550340", 735845L, 9640, 81);
        assertEquals("p305366", p.getId());
        assertEquals("sku550340", p.getName());
        assertEquals(735845L, p.getPrice());
        assertEquals(9640, p.getWeightGrams());
        p.restoreStock(5);
        assertEquals(86, p.getStock());
        p.restoreStock(60);
        assertEquals(146, p.getStock());
        p.reduceStock(48);
        assertEquals(98, p.getStock());
        p.reduceStock(46);
        assertEquals(52, p.getStock());
        p.restoreStock(26);
        assertEquals(78, p.getStock());
    }

    @Test
    void sample82() {
        var p = new Product("p263031", "sku821117", 376487L, 1285, 15);
        var item = new CartItem(p, 11);
        assertEquals("p263031", item.getProduct().getId());
        assertEquals(11, item.getQuantity());
        assertEquals(4141357L, item.getLineTotal());
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a428512", "sku188207", 544L, 88, 14);
        var b = new Product("b324324", "sku883665", 187736L, 1318, 11);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a428512"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other334018"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b324324", 38));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample84() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code535217", CouponType.FIXED, 22520L, -48247L, 280252L, LocalDate.parse("2027-04-08")));
    }

    @Test
    void sample85() {
        var coupon = new Coupon("code899623", CouponType.PERCENT, 60L, 245307L, 46421L, LocalDate.parse("2027-01-12"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1058390L, coupon, LocalDate.parse("2027-01-29")));
    }

    @Test
    void sample86() {
        assertEquals(99000L, new ShippingCalculator().calculate(Region.SUBURB, 19099, 392768L));
    }

    @Test
    void sample87() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p901187", "sku25769", 725799L, 339, 13);
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order581838", null, null, Region.REMOTE, LocalDateTime.parse("2025-04-13T13:50")));
        assertEquals(13, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(13, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(13, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown604337"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown312748"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message66225").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message66225").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message66225").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        var p = new Product("p93936", "sku915450", 48525L, 23686, 63);
        assertEquals("p93936", p.getId());
        assertEquals("sku915450", p.getName());
        assertEquals(48525L, p.getPrice());
        assertEquals(23686, p.getWeightGrams());
        p.restoreStock(45);
        assertEquals(108, p.getStock());
        p.restoreStock(40);
        assertEquals(148, p.getStock());
        p.restoreStock(45);
        assertEquals(193, p.getStock());
        p.restoreStock(10);
        assertEquals(203, p.getStock());
        p.restoreStock(54);
        assertEquals(257, p.getStock());
    }

    @Test
    void sample90() {
        var p = new Product("p454721", "sku992467", 475603L, 506, 85);
        var item = new CartItem(p, 12);
        assertEquals("p454721", item.getProduct().getId());
        assertEquals(12, item.getQuantity());
        assertEquals(5707236L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a804693", "sku860436", 688096L, 709, 39);
        var b = new Product("b408018", "sku891129", 114650L, 919, 34);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a804693", 41));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 34);
        assertEquals(3898100L, cart.getSubtotal());
        assertEquals(31246, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b408018", cart.getItems().get(0).getProduct().getId());
        assertEquals(34, cart.getItems().get(0).getQuantity());
        assertEquals(3898100L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b408018");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a804693"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a804693", 53));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample92() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code200147", CouponType.FIXED, 115918L, 341553L, 148341L, null));
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code370451", CouponType.FIXED, 235686L, 340664L, 160635L, LocalDate.parse("2027-11-25"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-40101L, coupon, LocalDate.parse("2027-12-08")));
    }

    @Test
    void sample94() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 2595, 577976L));
    }

    @Test
    void sample95() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample96() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown409248"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message88809").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message88809").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message88809").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku26161", 469544L, 4408, -3));
    }

    @Test
    void sample98() {
        var p = new Product("p939796", "sku104413", 635122L, 1235, 2);
        var item = new CartItem(p, 44);
        assertEquals("p939796", item.getProduct().getId());
        assertEquals(44, item.getQuantity());
        assertEquals(27945368L, item.getLineTotal());
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a691755", "sku351081", 710961L, 1506, 60);
        var b = new Product("b411679", "sku859018", 141934L, 1489, 13);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a691755", 43));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a691755"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a691755"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b411679"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a691755", 1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 44);
        assertEquals(31282284L, cart.getSubtotal());
        assertEquals(66264, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a691755", cart.getItems().get(0).getProduct().getId());
        assertEquals(44, cart.getItems().get(0).getQuantity());
        assertEquals(31282284L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample100() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code729665", null, 469290L, 133160L, 243582L, LocalDate.parse("2027-07-23")));
    }
}
