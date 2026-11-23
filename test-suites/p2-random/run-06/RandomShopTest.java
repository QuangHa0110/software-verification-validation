package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p733197", "sku168990", 961093L, 16864, 4);
        assertEquals("p733197", p.getId());
        assertEquals("sku168990", p.getName());
        assertEquals(961093L, p.getPrice());
        assertEquals(16864, p.getWeightGrams());
        p.restoreStock(29);
        assertEquals(33, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(39));
        assertEquals(33, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(0));
        assertEquals(33, p.getStock());
        p.reduceStock(33);
        assertEquals(0, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(48));
        assertEquals(0, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p626075", "sku17367", 48112L, 915, 82);
        var item = new CartItem(p, 24);
        assertEquals("p626075", item.getProduct().getId());
        assertEquals(24, item.getQuantity());
        assertEquals(1154688L, item.getLineTotal());
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a789427", "sku511411", 256137L, 775, 38);
        var b = new Product("b454597", "sku828344", 240384L, 782, 72);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a789427"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("a789427", -2));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b454597", 13));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a789427", 12));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -5));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b454597", 9));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample4() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 549750L, 400643L, 165847L, LocalDate.parse("2026-11-08")));
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code84920", CouponType.PERCENT, 7L, 167697L, 82017L, LocalDate.parse("2027-02-09"));
        assertEquals(14937L, new PricingService().calculateDiscount(213396L, coupon, LocalDate.parse("2027-01-08")));
    }

    @Test
    void sample6() {
        assertEquals(95000L, new ShippingCalculator().calculate(Region.REMOTE, 10636, 392895L));
    }

    @Test
    void sample7() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p520679", "sku784063", 12148L, 129, 56);
        cart.addItem(p, 14);
        var coupon = new Coupon("code353172", CouponType.FIXED, 351316L, 468260L, 32646L, LocalDate.parse("2025-12-26"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(" ", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2025-11-30T21:01")));
        assertEquals(56, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-11-30T03:48")));
        assertEquals(56, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(56, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(56, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown458511"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.NullPointerException.class, () -> CouponType.valueOf(null));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown795439"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message213035").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message213035").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message213035").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        var p = new Product("p848710", "sku233271", 42209L, 8245, 19);
        assertEquals("p848710", p.getId());
        assertEquals("sku233271", p.getName());
        assertEquals(42209L, p.getPrice());
        assertEquals(8245, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(21));
        assertEquals(19, p.getStock());
        p.reduceStock(19);
        assertEquals(0, p.getStock());
        p.restoreStock(20);
        assertEquals(20, p.getStock());
    }

    @Test
    void sample10() {
        var p = new Product("p763550", "sku925533", 331842L, 993, 63);
        var item = new CartItem(p, 6);
        assertEquals("p763550", item.getProduct().getId());
        assertEquals(6, item.getQuantity());
        assertEquals(1991052L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a715355", "sku956271", 523415L, 99, 85);
        var b = new Product("b384579", "sku529824", 425112L, 1089, 83);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b384579", 14));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 48);
        assertEquals(20405376L, cart.getSubtotal());
        assertEquals(52272, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b384579", cart.getItems().get(0).getProduct().getId());
        assertEquals(48, cart.getItems().get(0).getQuantity());
        assertEquals(20405376L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other824908", 4));
        assertEquals(20405376L, cart.getSubtotal());
        assertEquals(52272, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b384579", cart.getItems().get(0).getProduct().getId());
        assertEquals(48, cart.getItems().get(0).getQuantity());
        assertEquals(20405376L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 42);
        assertEquals(42388806L, cart.getSubtotal());
        assertEquals(56430, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b384579", cart.getItems().get(0).getProduct().getId());
        assertEquals(48, cart.getItems().get(0).getQuantity());
        assertEquals(20405376L, cart.getItems().get(0).getLineTotal());
        assertEquals("a715355", cart.getItems().get(1).getProduct().getId());
        assertEquals(42, cart.getItems().get(1).getQuantity());
        assertEquals(21983430L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 36));
        assertEquals(42388806L, cart.getSubtotal());
        assertEquals(56430, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b384579", cart.getItems().get(0).getProduct().getId());
        assertEquals(48, cart.getItems().get(0).getQuantity());
        assertEquals(20405376L, cart.getItems().get(0).getLineTotal());
        assertEquals("a715355", cart.getItems().get(1).getProduct().getId());
        assertEquals(42, cart.getItems().get(1).getQuantity());
        assertEquals(21983430L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 162984L, 255790L, 261109L, null));
    }

    @Test
    void sample13() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(72691L, null, null));
    }

    @Test
    void sample14() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 20250, 1221709L));
    }

    @Test
    void sample15() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p911768", "sku225331", 302427L, 1437, 8);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 18));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order226252", cart, null, null, LocalDateTime.parse("2027-10-05T15:33")));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-10-07T04:31")));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(8, p.getStock());
    }

    @Test
    void sample16() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown964037"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message277756").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message277756").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message277756").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p873222", "sku983373", 912921L, 1805, 60);
        assertEquals("p873222", p.getId());
        assertEquals("sku983373", p.getName());
        assertEquals(912921L, p.getPrice());
        assertEquals(1805, p.getWeightGrams());
        p.reduceStock(30);
        assertEquals(30, p.getStock());
        p.restoreStock(14);
        assertEquals(44, p.getStock());
        p.restoreStock(35);
        assertEquals(79, p.getStock());
        p.restoreStock(45);
        assertEquals(124, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p359066", "sku900359", 316303L, 216, 3);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, 0));
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a339717", "sku870306", 67732L, 1263, 27);
        var b = new Product("b438144", "sku299103", 135832L, 1501, 57);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b438144", 48));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 41);
        assertEquals(5569112L, cart.getSubtotal());
        assertEquals(61541, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b438144", cart.getItems().get(0).getProduct().getId());
        assertEquals(41, cart.getItems().get(0).getQuantity());
        assertEquals(5569112L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b438144", 52));
        assertEquals(5569112L, cart.getSubtotal());
        assertEquals(61541, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b438144", cart.getItems().get(0).getProduct().getId());
        assertEquals(41, cart.getItems().get(0).getQuantity());
        assertEquals(5569112L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a339717"));
        assertEquals(5569112L, cart.getSubtotal());
        assertEquals(61541, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b438144", cart.getItems().get(0).getProduct().getId());
        assertEquals(41, cart.getItems().get(0).getQuantity());
        assertEquals(5569112L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample20() {
        var coupon = new Coupon("code303923", CouponType.PERCENT, 51L, 513968L, 288104L, LocalDate.parse("2027-05-11"));
        assertEquals("code303923", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(51L, coupon.getValue());
        assertEquals(513968L, coupon.getMinOrder());
        assertEquals(288104L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-05-11"), coupon.getExpiry());
    }

    @Test
    void sample21() {
        assertEquals(0L, new PricingService().calculateDiscount(785098L, null, LocalDate.parse("2025-08-08")));
    }

    @Test
    void sample22() {
        assertEquals(115000L, new ShippingCalculator().calculate(Region.REMOTE, 15834, 589677L));
    }

    @Test
    void sample23() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p190378", "sku29111", 184765L, 792, 6);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 8));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order618843", cart, null, Region.INNER_CITY, null));
        assertEquals(6, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(6, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(6, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown303430"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown52122"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message412525").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message412525").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message412525").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p426598", " ", 776604L, 15047, 49));
    }

    @Test
    void sample26() {
        var p = new Product("p909035", "sku194992", 109022L, 98, 66);
        var item = new CartItem(p, 20);
        assertEquals("p909035", item.getProduct().getId());
        assertEquals(20, item.getQuantity());
        assertEquals(2180440L, item.getLineTotal());
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a633099", "sku953629", 478247L, 906, 74);
        var b = new Product("b40421", "sku74627", 756393L, 1531, 48);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a633099", 23));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 20));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b40421", 57));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample28() {
        var coupon = new Coupon("code510277", CouponType.FIXED, 460788L, 144448L, 81865L, LocalDate.parse("2025-01-27"));
        assertEquals("code510277", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(460788L, coupon.getValue());
        assertEquals(144448L, coupon.getMinOrder());
        assertEquals(81865L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-01-27"), coupon.getExpiry());
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code233912", CouponType.PERCENT, 91L, 111437L, 123975L, LocalDate.parse("2027-08-20"));
        assertEquals(123975L, new PricingService().calculateDiscount(189003L, coupon, LocalDate.parse("2027-07-25")));
    }

    @Test
    void sample30() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.PROVINCE, 24267, 93093L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p375791", "sku922668", 108168L, 241, 74);
        cart.addItem(p, 15);
        var order = service.placeOrder("order969083", cart, null, Region.REMOTE, LocalDateTime.parse("2027-03-06T08:32"));
        assertEquals(59, p.getStock());
        assertEquals("order969083", order.getId());
        assertEquals(1622520L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(67000L, order.getShipping());
        assertEquals(1689520L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-03-06T08:32"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p375791", order.getItems().get(0).getProduct().getId());
        assertEquals(15, order.getItems().get(0).getQuantity());
        assertEquals(1622520L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(59, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(59, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(59, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message466897").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message466897").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message466897").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p777194", "sku746597", 225035L, 10596, 27);
        assertEquals("p777194", p.getId());
        assertEquals("sku746597", p.getName());
        assertEquals(225035L, p.getPrice());
        assertEquals(10596, p.getWeightGrams());
        p.restoreStock(60);
        assertEquals(87, p.getStock());
        p.restoreStock(58);
        assertEquals(145, p.getStock());
        p.reduceStock(40);
        assertEquals(105, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p449613", "sku250267", 501696L, 248, 63);
        var item = new CartItem(p, 31);
        assertEquals("p449613", item.getProduct().getId());
        assertEquals(31, item.getQuantity());
        assertEquals(15552576L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a431661", "sku776753", 368860L, 1194, 6);
        var b = new Product("b643488", "sku845212", 641860L, 1229, 11);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 47));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a431661", 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 31));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample36() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.FIXED, 392245L, -48410L, 263830L, null));
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code419379", CouponType.PERCENT, 33L, 415730L, 106727L, LocalDate.parse("2027-01-08"));
        assertEquals(106727L, new PricingService().calculateDiscount(902160L, coupon, LocalDate.parse("2027-01-03")));
    }

    @Test
    void sample38() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 4973, 1001802L));
    }

    @Test
    void sample39() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p330643", "sku278683", 500233L, 364, 77);
        cart.addItem(p, 9);
        var order = service.placeOrder("order58000", cart, null, Region.SUBURB, LocalDateTime.parse("2026-07-21T22:41"));
        assertEquals(68, p.getStock());
        assertEquals("order58000", order.getId());
        assertEquals(4502097L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(4502097L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-07-21T22:41"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p330643", order.getItems().get(0).getProduct().getId());
        assertEquals(9, order.getItems().get(0).getQuantity());
        assertEquals(4502097L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-07-21T09:43")));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(68, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(68, p.getStock());
        assertEquals(4502097L, service.cancelOrder(order, LocalDateTime.parse("2026-07-22T10:54")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(77, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown608734"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message530314").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message530314").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message530314").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p802022", null, 25343L, 13027, 59));
    }

    @Test
    void sample42() {
        var p = new Product("p195967", "sku88602", 185998L, 673, 85);
        var item = new CartItem(p, 10);
        assertEquals("p195967", item.getProduct().getId());
        assertEquals(10, item.getQuantity());
        assertEquals(1859980L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a83112", "sku146525", 578608L, 1485, 60);
        var b = new Product("b278157", "sku600617", 981867L, 1509, 29);
        cart.addItem(a, 16);
        assertEquals(9257728L, cart.getSubtotal());
        assertEquals(23760, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a83112", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(9257728L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other473620"));
        assertEquals(9257728L, cart.getSubtotal());
        assertEquals(23760, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a83112", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(9257728L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 59));
        assertEquals(9257728L, cart.getSubtotal());
        assertEquals(23760, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a83112", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(9257728L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b278157", 13));
        assertEquals(9257728L, cart.getSubtotal());
        assertEquals(23760, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a83112", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(9257728L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other979668"));
        assertEquals(9257728L, cart.getSubtotal());
        assertEquals(23760, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a83112", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(9257728L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample44() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 410752L, 453742L, 19743L, LocalDate.parse("2025-01-01")));
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code382899", CouponType.PERCENT, 58L, 22168L, 22246L, LocalDate.parse("2024-05-15"));
        assertEquals(22246L, new PricingService().calculateDiscount(505685L, coupon, LocalDate.parse("2024-05-12")));
    }

    @Test
    void sample46() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 19553, 742991L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p856908", "sku191187", 465641L, 899, 12);
        cart.addItem(p, 1);
        var coupon = new Coupon("code474321", CouponType.FIXED, 161731L, 181590L, 60297L, LocalDate.parse("2026-03-14"));
        var order = service.placeOrder("order237852", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2026-02-07T08:43"));
        assertEquals(11, p.getStock());
        assertEquals("order237852", order.getId());
        assertEquals(465641L, order.getSubtotal());
        assertEquals(161731L, order.getDiscount());
        assertEquals(35000L, order.getShipping());
        assertEquals(338910L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-02-07T08:43"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p856908", order.getItems().get(0).getProduct().getId());
        assertEquals(1, order.getItems().get(0).getQuantity());
        assertEquals(465641L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2026-02-07T14:24")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message392583").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message392583").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message392583").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku817970", 856672L, 17553, 21));
    }

    @Test
    void sample50() {
        var p = new Product("p735799", "sku225087", 77101L, 316, 9);
        var item = new CartItem(p, 58);
        assertEquals("p735799", item.getProduct().getId());
        assertEquals(58, item.getQuantity());
        assertEquals(4471858L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a192899", "sku403310", 441185L, 1589, 49);
        var b = new Product("b667725", "sku604858", 752153L, 1322, 2);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b667725"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other745666", 6));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 2);
        assertEquals(1504306L, cart.getSubtotal());
        assertEquals(2644, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b667725", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(1504306L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 31));
        assertEquals(1504306L, cart.getSubtotal());
        assertEquals(2644, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b667725", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(1504306L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 30));
        assertEquals(1504306L, cart.getSubtotal());
        assertEquals(2644, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b667725", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(1504306L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -1));
        assertEquals(1504306L, cart.getSubtotal());
        assertEquals(2644, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b667725", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(1504306L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b667725");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample52() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.PERCENT, 39L, 256740L, 232211L, null));
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code549483", CouponType.FIXED, 224521L, 35793L, 248538L, LocalDate.parse("2024-09-18"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(570321L, coupon, LocalDate.parse("2024-09-24")));
    }

    @Test
    void sample54() {
        assertEquals(63000L, new ShippingCalculator().calculate(Region.PROVINCE, 7824, 493611L));
    }

    @Test
    void sample55() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p203026", "sku244374", 272504L, 787, 32);
        cart.addItem(p, 18);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(null, cart, null, Region.SUBURB, LocalDateTime.parse("2024-11-30T09:54")));
        assertEquals(32, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(32, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-11-30T10:21")));
        assertEquals(32, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-12-02T06:21")));
        assertEquals(32, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(32, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.NullPointerException.class, () -> Region.valueOf(null));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf(" "));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown116079"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message343572").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message343572").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message343572").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p184789", "sku854546", -48955L, 20695, 45));
    }

    @Test
    void sample58() {
        var p = new Product("p701648", "sku355965", 743424L, 1123, 55);
        var item = new CartItem(p, 47);
        assertEquals("p701648", item.getProduct().getId());
        assertEquals(47, item.getQuantity());
        assertEquals(34940928L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a453889", "sku353673", 787834L, 860, 52);
        var b = new Product("b156230", "sku878784", 56029L, 597, 56);
        cart.addItem(b, 9);
        assertEquals(504261L, cart.getSubtotal());
        assertEquals(5373, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b156230", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(504261L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a453889", 16));
        assertEquals(504261L, cart.getSubtotal());
        assertEquals(5373, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b156230", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(504261L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a453889", 15));
        assertEquals(504261L, cart.getSubtotal());
        assertEquals(5373, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b156230", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(504261L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a453889"));
        assertEquals(504261L, cart.getSubtotal());
        assertEquals(5373, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b156230", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(504261L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample60() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 51860L, 534606L, 41552L, LocalDate.parse("2024-10-06")));
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code512597", CouponType.PERCENT, 59L, 142578L, 168240L, LocalDate.parse("2025-01-06"));
        assertEquals(168240L, new PricingService().calculateDiscount(792048L, coupon, LocalDate.parse("2024-12-30")));
    }

    @Test
    void sample62() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 11732, 250367L));
    }

    @Test
    void sample63() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample64() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown903978"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown952246"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message245269").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message245269").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message245269").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        var p = new Product("p585156", "sku699537", 242599L, 956, 60);
        assertEquals("p585156", p.getId());
        assertEquals("sku699537", p.getName());
        assertEquals(242599L, p.getPrice());
        assertEquals(956, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-2));
        assertEquals(60, p.getStock());
        p.reduceStock(22);
        assertEquals(38, p.getStock());
    }

    @Test
    void sample66() {
        var p = new Product("p16833", "sku127329", 936204L, 141, 58);
        var item = new CartItem(p, 47);
        assertEquals("p16833", item.getProduct().getId());
        assertEquals(47, item.getQuantity());
        assertEquals(44001588L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a897808", "sku21525", 491022L, 630, 51);
        var b = new Product("b309149", "sku846658", 496147L, 838, 33);
        cart.addItem(a, 41);
        assertEquals(20131902L, cart.getSubtotal());
        assertEquals(25830, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a897808", cart.getItems().get(0).getProduct().getId());
        assertEquals(41, cart.getItems().get(0).getQuantity());
        assertEquals(20131902L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("a897808");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -4));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a897808", 47));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a897808"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b309149"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a897808", 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        var coupon = new Coupon("code836996", CouponType.FIXED, 371952L, 403806L, 10849L, LocalDate.parse("2024-08-29"));
        assertEquals("code836996", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(371952L, coupon.getValue());
        assertEquals(403806L, coupon.getMinOrder());
        assertEquals(10849L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-08-29"), coupon.getExpiry());
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code486971", CouponType.PERCENT, 58L, 317014L, 168558L, LocalDate.parse("2025-10-17"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(755177L, coupon, LocalDate.parse("2025-11-05")));
    }

    @Test
    void sample70() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 17624, 1180424L));
    }

    @Test
    void sample71() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p311633", "sku399270", 818969L, 1374, 51);
        cart.addItem(p, 1);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order296", cart, null, null, LocalDateTime.parse("2024-10-23T18:02")));
        assertEquals(51, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-10-24T03:17")));
        assertEquals(51, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(51, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(51, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message615221").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message615221").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message615221").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        var p = new Product("p744151", "sku225680", 530608L, 5087, 22);
        assertEquals("p744151", p.getId());
        assertEquals("sku225680", p.getName());
        assertEquals(530608L, p.getPrice());
        assertEquals(5087, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(0));
        assertEquals(22, p.getStock());
        p.restoreStock(59);
        assertEquals(81, p.getStock());
    }

    @Test
    void sample74() {
        var p = new Product("p855491", "sku387744", 183937L, 34, 54);
        var item = new CartItem(p, 37);
        assertEquals("p855491", item.getProduct().getId());
        assertEquals(37, item.getQuantity());
        assertEquals(6805669L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a803579", "sku192419", 906037L, 375, 1);
        var b = new Product("b777440", "sku760950", 381933L, 868, 85);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a803579", 34));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b777440", 39));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 38));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b777440", 49));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b777440", 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b777440"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        var coupon = new Coupon("code197261", CouponType.FIXED, 249536L, 431144L, 27150L, LocalDate.parse("2026-02-06"));
        assertEquals("code197261", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(249536L, coupon.getValue());
        assertEquals(431144L, coupon.getMinOrder());
        assertEquals(27150L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-02-06"), coupon.getExpiry());
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code226124", CouponType.PERCENT, 58L, 79664L, 261629L, LocalDate.parse("2025-06-15"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-3451L, coupon, LocalDate.parse("2025-04-18")));
    }

    @Test
    void sample78() {
        assertEquals(99000L, new ShippingCalculator().calculate(Region.SUBURB, 19231, 281113L));
    }

    @Test
    void sample79() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p476790", "sku752518", 507437L, 1597, 38);
        cart.addItem(p, 17);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order469791", cart, null, Region.SUBURB, LocalDateTime.parse("2026-08-19T22:09")));
        assertEquals(38, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-08-20T17:04")));
        assertEquals(38, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(38, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown626902"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message479995").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message479995").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message479995").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(null, "sku84069", 884389L, 24842, -2));
    }

    @Test
    void sample82() {
        var p = new Product("p41920", "sku350663", 452152L, 712, 23);
        var item = new CartItem(p, 50);
        assertEquals("p41920", item.getProduct().getId());
        assertEquals(50, item.getQuantity());
        assertEquals(22607600L, item.getLineTotal());
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a268973", "sku155355", 444327L, 1042, 42);
        var b = new Product("b729605", "sku273091", 45187L, 1018, 12);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a268973"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 27);
        assertEquals(11996829L, cart.getSubtotal());
        assertEquals(28134, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a268973", cart.getItems().get(0).getProduct().getId());
        assertEquals(27, cart.getItems().get(0).getQuantity());
        assertEquals(11996829L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 48));
        assertEquals(11996829L, cart.getSubtotal());
        assertEquals(28134, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a268973", cart.getItems().get(0).getProduct().getId());
        assertEquals(27, cart.getItems().get(0).getQuantity());
        assertEquals(11996829L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b729605"));
        assertEquals(11996829L, cart.getSubtotal());
        assertEquals(28134, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a268973", cart.getItems().get(0).getProduct().getId());
        assertEquals(27, cart.getItems().get(0).getQuantity());
        assertEquals(11996829L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b729605", -5));
        assertEquals(11996829L, cart.getSubtotal());
        assertEquals(28134, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a268973", cart.getItems().get(0).getProduct().getId());
        assertEquals(27, cart.getItems().get(0).getQuantity());
        assertEquals(11996829L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample84() {
        var coupon = new Coupon("code500090", CouponType.FIXED, 204880L, 89995L, 129050L, LocalDate.parse("2026-09-26"));
        assertEquals("code500090", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(204880L, coupon.getValue());
        assertEquals(89995L, coupon.getMinOrder());
        assertEquals(129050L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-09-26"), coupon.getExpiry());
    }

    @Test
    void sample85() {
        var coupon = new Coupon("code857547", CouponType.PERCENT, 12L, 200642L, 76594L, LocalDate.parse("2025-09-10"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(317034L, coupon, LocalDate.parse("2025-10-10")));
    }

    @Test
    void sample86() {
        assertEquals(111000L, new ShippingCalculator().calculate(Region.REMOTE, 14713, 718615L));
    }

    @Test
    void sample87() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p947835", "sku613526", 317645L, 77, 85);
        cart.addItem(p, 9);
        var coupon = new Coupon("code184416", CouponType.PERCENT, 91L, 145655L, 51697L, LocalDate.parse("2024-06-19"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order751394", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2024-07-26T00:23")));
        assertEquals(85, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(85, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(85, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(85, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-07-27T07:37")));
        assertEquals(85, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown379028"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message609443").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message609443").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message609443").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p805671", null, -5990L, 20093, 61));
    }

    @Test
    void sample90() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 49));
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a692382", "sku319345", 459774L, 619, 32);
        var b = new Product("b184624", "sku521143", 262254L, 252, 21);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other429476", 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b184624"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 7);
        assertEquals(1835778L, cart.getSubtotal());
        assertEquals(1764, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b184624", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(1835778L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.updateQuantity("b184624", 44));
        assertEquals(1835778L, cart.getSubtotal());
        assertEquals(1764, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b184624", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(1835778L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 25);
        assertEquals(13330128L, cart.getSubtotal());
        assertEquals(17239, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b184624", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(1835778L, cart.getItems().get(0).getLineTotal());
        assertEquals("a692382", cart.getItems().get(1).getProduct().getId());
        assertEquals(25, cart.getItems().get(1).getQuantity());
        assertEquals(11494350L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample92() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.PERCENT, 79L, 431314L, 12789L, null));
    }

    @Test
    void sample93() {
        assertEquals(0L, new PricingService().calculateDiscount(538581L, null, LocalDate.parse("2026-01-05")));
    }

    @Test
    void sample94() {
        assertEquals(117000L, new ShippingCalculator().calculate(Region.REMOTE, 16372, 704103L));
    }

    @Test
    void sample95() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p653408", "sku883775", 518695L, 1414, 58);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(null, null, null, null, LocalDateTime.parse("2026-05-08T00:25")));
        assertEquals(58, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(58, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(58, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample97() {
        var p = new Product("p790520", "sku750189", 424482L, 1382, 2);
        assertEquals("p790520", p.getId());
        assertEquals("sku750189", p.getName());
        assertEquals(424482L, p.getPrice());
        assertEquals(1382, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(57));
        assertEquals(2, p.getStock());
        p.restoreStock(24);
        assertEquals(26, p.getStock());
        p.restoreStock(12);
        assertEquals(38, p.getStock());
        p.restoreStock(19);
        assertEquals(57, p.getStock());
    }

    @Test
    void sample98() {
        var p = new Product("p537898", "sku945023", 491640L, 396, 46);
        var item = new CartItem(p, 7);
        assertEquals("p537898", item.getProduct().getId());
        assertEquals(7, item.getQuantity());
        assertEquals(3441480L, item.getLineTotal());
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a266079", "sku161114", 637769L, 58, 31);
        var b = new Product("b410992", "sku864351", 696786L, 1491, 66);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a266079", 12));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a266079", 39));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b410992", 1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample100() {
        var coupon = new Coupon("code778996", CouponType.FIXED, 344510L, 153359L, 23108L, LocalDate.parse("2026-09-28"));
        assertEquals("code778996", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(344510L, coupon.getValue());
        assertEquals(153359L, coupon.getMinOrder());
        assertEquals(23108L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-09-28"), coupon.getExpiry());
    }
}
