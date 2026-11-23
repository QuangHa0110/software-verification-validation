package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p333526", "sku843730", 726556L, 20747, 58);
        assertEquals("p333526", p.getId());
        assertEquals("sku843730", p.getName());
        assertEquals(726556L, p.getPrice());
        assertEquals(20747, p.getWeightGrams());
        p.restoreStock(12);
        assertEquals(70, p.getStock());
        p.reduceStock(30);
        assertEquals(40, p.getStock());
        p.restoreStock(21);
        assertEquals(61, p.getStock());
        p.reduceStock(19);
        assertEquals(42, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p323496", "sku896615", 556835L, 713, 8);
        var item = new CartItem(p, 53);
        assertEquals("p323496", item.getProduct().getId());
        assertEquals(53, item.getQuantity());
        assertEquals(29512255L, item.getLineTotal());
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a192994", "sku868838", 316940L, 17, 6);
        var b = new Product("b246660", "sku755182", 734607L, 603, 12);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b246660"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other7546", 13));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 1);
        assertEquals(734607L, cart.getSubtotal());
        assertEquals(603, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b246660", cart.getItems().get(0).getProduct().getId());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(734607L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, -2));
        assertEquals(734607L, cart.getSubtotal());
        assertEquals(603, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b246660", cart.getItems().get(0).getProduct().getId());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(734607L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 23));
        assertEquals(734607L, cart.getSubtotal());
        assertEquals(603, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b246660", cart.getItems().get(0).getProduct().getId());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(734607L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample4() {
        var coupon = new Coupon("code170255", CouponType.FIXED, 191737L, 45296L, 57616L, LocalDate.parse("2027-12-02"));
        assertEquals("code170255", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(191737L, coupon.getValue());
        assertEquals(45296L, coupon.getMinOrder());
        assertEquals(57616L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-12-02"), coupon.getExpiry());
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code479431", CouponType.PERCENT, 49L, 401846L, 251142L, LocalDate.parse("2025-06-12"));
        assertEquals(251142L, new PricingService().calculateDiscount(760714L, coupon, LocalDate.parse("2025-06-08")));
    }

    @Test
    void sample6() {
        assertEquals(87000L, new ShippingCalculator().calculate(Region.PROVINCE, 13895, 435481L));
    }

    @Test
    void sample7() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p628002", "sku513626", 106700L, 164, 66);
        cart.addItem(p, 9);
        var order = service.placeOrder("order836126", cart, null, Region.REMOTE, LocalDateTime.parse("2025-03-04T05:04"));
        assertEquals(57, p.getStock());
        assertEquals("order836126", order.getId());
        assertEquals(960300L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(57000L, order.getShipping());
        assertEquals(1017300L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-03-04T05:04"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p628002", order.getItems().get(0).getProduct().getId());
        assertEquals(9, order.getItems().get(0).getQuantity());
        assertEquals(960300L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(57, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(57, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(57, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2025-03-08T00:08")));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(57, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(57, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown607573"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException(" ").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(" ").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(" ").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        var p = new Product("p797958", "sku674234", 804204L, 17054, 77);
        assertEquals("p797958", p.getId());
        assertEquals("sku674234", p.getName());
        assertEquals(804204L, p.getPrice());
        assertEquals(17054, p.getWeightGrams());
        p.restoreStock(4);
        assertEquals(81, p.getStock());
        p.restoreStock(46);
        assertEquals(127, p.getStock());
    }

    @Test
    void sample10() {
        var p = new Product("p205200", "sku894729", 870621L, 1454, 31);
        var item = new CartItem(p, 44);
        assertEquals("p205200", item.getProduct().getId());
        assertEquals(44, item.getQuantity());
        assertEquals(38307324L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a462521", "sku488071", 871832L, 490, 10);
        var b = new Product("b307980", "sku8061", 266734L, 325, 82);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 30));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b307980", 54));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a462521"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a462521"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b307980"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, null, 236314L, 533203L, 8809L, null));
    }

    @Test
    void sample13() {
        var coupon = new Coupon("code487416", CouponType.FIXED, 141470L, 312249L, 81551L, LocalDate.parse("2025-07-10"));
        assertEquals(141470L, new PricingService().calculateDiscount(815073L, coupon, LocalDate.parse("2025-06-08")));
    }

    @Test
    void sample14() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 23039, 161103L));
    }

    @Test
    void sample15() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p71442", "sku16350", 937162L, 316, 59);
        cart.addItem(p, 12);
        var order = service.placeOrder("order302290", cart, null, Region.PROVINCE, LocalDateTime.parse("2025-02-12T16:32"));
        assertEquals(47, p.getStock());
        assertEquals("order302290", order.getId());
        assertEquals(11245944L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(47000L, order.getShipping());
        assertEquals(11292944L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-02-12T16:32"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p71442", order.getItems().get(0).getProduct().getId());
        assertEquals(12, order.getItems().get(0).getQuantity());
        assertEquals(11245944L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(47, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(47, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2025-02-12T03:10")));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(47, p.getStock());
    }

    @Test
    void sample16() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown554421"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown399688"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message757984").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message757984").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message757984").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p569264", "sku62296", 410797L, 17565, 5);
        assertEquals("p569264", p.getId());
        assertEquals("sku62296", p.getName());
        assertEquals(410797L, p.getPrice());
        assertEquals(17565, p.getWeightGrams());
        p.reduceStock(1);
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-4));
        assertEquals(4, p.getStock());
        p.restoreStock(28);
        assertEquals(32, p.getStock());
        p.reduceStock(2);
        assertEquals(30, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p926546", "sku346102", 625140L, 116, 74);
        var item = new CartItem(p, 25);
        assertEquals("p926546", item.getProduct().getId());
        assertEquals(25, item.getQuantity());
        assertEquals(15628500L, item.getLineTotal());
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a570714", "sku684716", 429934L, 1230, 5);
        var b = new Product("b441908", "sku110530", 65664L, 256, 51);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b441908"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a570714"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b441908"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample20() {
        var coupon = new Coupon("code503246", CouponType.FIXED, 98583L, 352477L, 106111L, LocalDate.parse("2027-03-21"));
        assertEquals("code503246", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(98583L, coupon.getValue());
        assertEquals(352477L, coupon.getMinOrder());
        assertEquals(106111L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-03-21"), coupon.getExpiry());
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code109489", CouponType.FIXED, 596804L, 501559L, 237189L, LocalDate.parse("2026-06-11"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(474282L, coupon, null));
    }

    @Test
    void sample22() {
        assertEquals(117000L, new ShippingCalculator().calculate(Region.REMOTE, 16359, 348989L));
    }

    @Test
    void sample23() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p45783", "sku496047", 724068L, 690, 9);
        cart.addItem(p, 6);
        var order = service.placeOrder("order403544", cart, null, Region.PROVINCE, LocalDateTime.parse("2027-06-21T04:40"));
        assertEquals(3, p.getStock());
        assertEquals("order403544", order.getId());
        assertEquals(4344408L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(49000L, order.getShipping());
        assertEquals(4393408L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-06-21T04:40"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p45783", order.getItems().get(0).getProduct().getId());
        assertEquals(6, order.getItems().get(0).getQuantity());
        assertEquals(4344408L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(3, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(3, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(3, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(3, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(3, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown758700"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf(" "));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message28639").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message28639").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message28639").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        var p = new Product("p745268", "sku365054", 367189L, 18366, 56);
        assertEquals("p745268", p.getId());
        assertEquals("sku365054", p.getName());
        assertEquals(367189L, p.getPrice());
        assertEquals(18366, p.getWeightGrams());
        p.reduceStock(49);
        assertEquals(7, p.getStock());
        p.restoreStock(34);
        assertEquals(41, p.getStock());
        p.restoreStock(38);
        assertEquals(79, p.getStock());
        p.reduceStock(7);
        assertEquals(72, p.getStock());
        p.reduceStock(11);
        assertEquals(61, p.getStock());
    }

    @Test
    void sample26() {
        var p = new Product("p329823", "sku940912", 954619L, 304, 33);
        var item = new CartItem(p, 48);
        assertEquals("p329823", item.getProduct().getId());
        assertEquals(48, item.getQuantity());
        assertEquals(45821712L, item.getLineTotal());
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a917365", "sku567176", 336150L, 1133, 79);
        var b = new Product("b902004", "sku731739", 955517L, 140, 52);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 55));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 32);
        assertEquals(30576544L, cart.getSubtotal());
        assertEquals(4480, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b902004", cart.getItems().get(0).getProduct().getId());
        assertEquals(32, cart.getItems().get(0).getQuantity());
        assertEquals(30576544L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a917365", 60));
        assertEquals(30576544L, cart.getSubtotal());
        assertEquals(4480, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b902004", cart.getItems().get(0).getProduct().getId());
        assertEquals(32, cart.getItems().get(0).getQuantity());
        assertEquals(30576544L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 3);
        assertEquals(33443095L, cart.getSubtotal());
        assertEquals(4900, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b902004", cart.getItems().get(0).getProduct().getId());
        assertEquals(35, cart.getItems().get(0).getQuantity());
        assertEquals(33443095L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a917365"));
        assertEquals(33443095L, cart.getSubtotal());
        assertEquals(4900, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b902004", cart.getItems().get(0).getProduct().getId());
        assertEquals(35, cart.getItems().get(0).getQuantity());
        assertEquals(33443095L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample28() {
        var coupon = new Coupon("code793056", CouponType.FIXED, 200714L, 496196L, 292258L, LocalDate.parse("2026-02-07"));
        assertEquals("code793056", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(200714L, coupon.getValue());
        assertEquals(496196L, coupon.getMinOrder());
        assertEquals(292258L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-02-07"), coupon.getExpiry());
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code21635", CouponType.PERCENT, 85L, 142987L, 260066L, LocalDate.parse("2024-09-16"));
        assertEquals(260066L, new PricingService().calculateDiscount(1113105L, coupon, LocalDate.parse("2024-09-15")));
    }

    @Test
    void sample30() {
        assertEquals(105000L, new ShippingCalculator().calculate(Region.REMOTE, 13458, 765652L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p366205", "sku619468", 837172L, 601, 57);
        cart.addItem(p, 12);
        var order = service.placeOrder("order722141", cart, null, Region.INNER_CITY, LocalDateTime.parse("2027-09-13T13:49"));
        assertEquals(45, p.getStock());
        assertEquals("order722141", order.getId());
        assertEquals(10046064L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(10046064L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-09-13T13:49"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p366205", order.getItems().get(0).getProduct().getId());
        assertEquals(12, order.getItems().get(0).getQuantity());
        assertEquals(10046064L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(45, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2027-09-13T00:29")));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(45, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown864042"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown553631"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message508527").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message508527").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message508527").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(null, "sku225388", 523086L, 11078, 8));
    }

    @Test
    void sample34() {
        var p = new Product("p554440", "sku862004", 875790L, 717, 5);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -4));
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a961062", "sku354724", 814712L, 1118, 21);
        var b = new Product("b130364", "sku370960", 884608L, 522, 9);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a961062"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b130364"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a961062"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b130364", 45));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample36() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code7107", CouponType.FIXED, 38786L, -8595L, 238605L, null));
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code657400", CouponType.FIXED, 70625L, 269647L, 37886L, LocalDate.parse("2027-11-20"));
        assertEquals(70625L, new PricingService().calculateDiscount(329907L, coupon, LocalDate.parse("2027-10-08")));
    }

    @Test
    void sample38() {
        assertEquals(107000L, new ShippingCalculator().calculate(Region.PROVINCE, 18551, 552337L));
    }

    @Test
    void sample39() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p498584", "sku259152", 326209L, 1226, 65);
        cart.addItem(p, 18);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order151956", cart, null, Region.PROVINCE, LocalDateTime.parse("2026-07-01T00:22")));
        assertEquals(65, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-07-01T18:43")));
        assertEquals(65, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(65, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(65, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(65, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown190866"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown543859"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message994501").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message994501").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message994501").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p632773", null, 9106L, 22357, 59));
    }

    @Test
    void sample42() {
        var p = new Product("p600557", "sku514737", 747945L, 3, 80);
        var item = new CartItem(p, 9);
        assertEquals("p600557", item.getProduct().getId());
        assertEquals(9, item.getQuantity());
        assertEquals(6731505L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a716756", "sku893003", 285932L, 707, 2);
        var b = new Product("b157556", "sku518224", 417366L, 1014, 14);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a716756", 37));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a716756"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a716756"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a716756"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a716756", 19));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample44() {
        var coupon = new Coupon("code451682", CouponType.FIXED, 539570L, 466576L, 298809L, LocalDate.parse("2027-10-06"));
        assertEquals("code451682", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(539570L, coupon.getValue());
        assertEquals(466576L, coupon.getMinOrder());
        assertEquals(298809L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-10-06"), coupon.getExpiry());
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code721379", CouponType.PERCENT, 5L, 81149L, 76496L, LocalDate.parse("2026-08-06"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(974741L, coupon, LocalDate.parse("2026-09-07")));
    }

    @Test
    void sample46() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 6055, 163163L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p181261", "sku466269", 544763L, 723, 70);
        cart.addItem(p, 6);
        var order = service.placeOrder("order287836", cart, null, Region.INNER_CITY, LocalDateTime.parse("2026-04-07T18:20"));
        assertEquals(64, p.getStock());
        assertEquals("order287836", order.getId());
        assertEquals(3268578L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(3268578L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-04-07T18:20"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p181261", order.getItems().get(0).getProduct().getId());
        assertEquals(6, order.getItems().get(0).getQuantity());
        assertEquals(3268578L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(64, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(64, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.NullPointerException.class, () -> Region.valueOf(null));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown925178"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message959139").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message959139").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message959139").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        var p = new Product("p24571", "sku341329", 46394L, 5523, 75);
        assertEquals("p24571", p.getId());
        assertEquals("sku341329", p.getName());
        assertEquals(46394L, p.getPrice());
        assertEquals(5523, p.getWeightGrams());
        p.restoreStock(23);
        assertEquals(98, p.getStock());
        p.reduceStock(20);
        assertEquals(78, p.getStock());
        p.reduceStock(40);
        assertEquals(38, p.getStock());
        p.restoreStock(17);
        assertEquals(55, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(57));
        assertEquals(55, p.getStock());
    }

    @Test
    void sample50() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 10));
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a405491", "sku801664", 275845L, 964, 65);
        var b = new Product("b610332", "sku515156", 71486L, 346, 40);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b610332"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 11));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b610332", -5));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b610332"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample52() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code156131", CouponType.FIXED, 114906L, -36974L, 16218L, null));
    }

    @Test
    void sample53() {
        assertEquals(0L, new PricingService().calculateDiscount(629046L, null, LocalDate.parse("2024-10-18")));
    }

    @Test
    void sample54() {
        assertEquals(99000L, new ShippingCalculator().calculate(Region.REMOTE, 11524, 271700L));
    }

    @Test
    void sample55() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p813063", "sku576839", 281532L, 988, 22);
        cart.addItem(p, 15);
        var coupon = new Coupon("code22584", CouponType.PERCENT, 41L, 342040L, 216499L, LocalDate.parse("2027-10-29"));
        var order = service.placeOrder("order87093", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2027-09-17T03:35"));
        assertEquals(7, p.getStock());
        assertEquals("order87093", order.getId());
        assertEquals(4222980L, order.getSubtotal());
        assertEquals(216499L, order.getDiscount());
        assertEquals(91000L, order.getShipping());
        assertEquals(4097481L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-09-17T03:35"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p813063", order.getItems().get(0).getProduct().getId());
        assertEquals(15, order.getItems().get(0).getQuantity());
        assertEquals(4222980L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2027-09-18T11:48")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(22, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(22, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2027-09-18T07:14")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(22, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(22, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message6678").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message6678").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message6678").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p981671", "sku818204", 482534L, 9108, 14);
        assertEquals("p981671", p.getId());
        assertEquals("sku818204", p.getName());
        assertEquals(482534L, p.getPrice());
        assertEquals(9108, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(45));
        assertEquals(14, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(15));
        assertEquals(14, p.getStock());
    }

    @Test
    void sample58() {
        var p = new Product("p898776", "sku69096", 933163L, 1041, 71);
        var item = new CartItem(p, 12);
        assertEquals("p898776", item.getProduct().getId());
        assertEquals(12, item.getQuantity());
        assertEquals(11197956L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a21452", "sku501197", 803074L, 614, 42);
        var b = new Product("b903388", "sku779205", 282825L, 1030, 11);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b903388", 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem(" "));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b903388"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 19));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("other934065", -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b903388"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a21452"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample60() {
        var coupon = new Coupon("code825015", CouponType.PERCENT, 72L, 234956L, 238329L, LocalDate.parse("2025-09-29"));
        assertEquals("code825015", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(72L, coupon.getValue());
        assertEquals(234956L, coupon.getMinOrder());
        assertEquals(238329L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-09-29"), coupon.getExpiry());
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code768963", CouponType.FIXED, 506552L, 100796L, 90639L, LocalDate.parse("2024-11-17"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-1304L, coupon, LocalDate.parse("2024-09-22")));
    }

    @Test
    void sample62() {
        assertEquals(37000L, new ShippingCalculator().calculate(Region.INNER_CITY, 6009, 327586L));
    }

    @Test
    void sample63() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p914293", "sku779262", 995641L, 1196, 23);
        cart.addItem(p, 9);
        var coupon = new Coupon("code741841", CouponType.PERCENT, 9L, 474679L, 185420L, LocalDate.parse("2024-10-25"));
        var order = service.placeOrder("order688063", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2024-09-21T06:53"));
        assertEquals(14, p.getStock());
        assertEquals("order688063", order.getId());
        assertEquals(8960769L, order.getSubtotal());
        assertEquals(185420L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(8775349L, order.getTotal());
        assertEquals(LocalDateTime.parse("2024-09-21T06:53"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p914293", order.getItems().get(0).getProduct().getId());
        assertEquals(9, order.getItems().get(0).getQuantity());
        assertEquals(8960769L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2024-09-22T00:18")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(23, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(23, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown314541"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown600432"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertEquals("InsufficientStockException", new InsufficientStockException("message809190").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message809190").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message809190").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        var p = new Product("p150391", "sku97459", 66849L, 16706, 11);
        assertEquals("p150391", p.getId());
        assertEquals("sku97459", p.getName());
        assertEquals(66849L, p.getPrice());
        assertEquals(16706, p.getWeightGrams());
        p.restoreStock(2);
        assertEquals(13, p.getStock());
        p.restoreStock(2);
        assertEquals(15, p.getStock());
    }

    @Test
    void sample66() {
        var p = new Product("p577964", "sku58170", 123724L, 980, 19);
        var item = new CartItem(p, 57);
        assertEquals("p577964", item.getProduct().getId());
        assertEquals(57, item.getQuantity());
        assertEquals(7052268L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a837904", "sku358374", 72896L, 104, 81);
        var b = new Product("b375399", "sku641707", 119671L, 834, 52);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b375399"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a837904", 35));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 33);
        assertEquals(3949143L, cart.getSubtotal());
        assertEquals(27522, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b375399", cart.getItems().get(0).getProduct().getId());
        assertEquals(33, cart.getItems().get(0).getQuantity());
        assertEquals(3949143L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a837904", 22));
        assertEquals(3949143L, cart.getSubtotal());
        assertEquals(27522, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b375399", cart.getItems().get(0).getProduct().getId());
        assertEquals(33, cart.getItems().get(0).getQuantity());
        assertEquals(3949143L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample68() {
        var coupon = new Coupon("code930459", CouponType.PERCENT, 84L, 518816L, 278650L, LocalDate.parse("2024-10-11"));
        assertEquals("code930459", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(84L, coupon.getValue());
        assertEquals(518816L, coupon.getMinOrder());
        assertEquals(278650L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-10-11"), coupon.getExpiry());
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code96410", CouponType.PERCENT, 43L, 121600L, 256711L, LocalDate.parse("2027-04-03"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-11345L, coupon, LocalDate.parse("2027-05-10")));
    }

    @Test
    void sample70() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.PROVINCE, 23796, 69126L));
    }

    @Test
    void sample71() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p149372", "sku73840", 218285L, 658, 47);
        cart.addItem(p, 14);
        var coupon = new Coupon("code28123", CouponType.PERCENT, 84L, 252792L, 22145L, LocalDate.parse("2025-06-30"));
        var order = service.placeOrder("order456923", cart, coupon, Region.REMOTE, LocalDateTime.parse("2025-06-29T04:05"));
        assertEquals(33, p.getStock());
        assertEquals("order456923", order.getId());
        assertEquals(3055990L, order.getSubtotal());
        assertEquals(22145L, order.getDiscount());
        assertEquals(89000L, order.getShipping());
        assertEquals(3122845L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-06-29T04:05"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p149372", order.getItems().get(0).getProduct().getId());
        assertEquals(14, order.getItems().get(0).getQuantity());
        assertEquals(3055990L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(33, p.getStock());
        assertEquals(2427076L, service.cancelOrder(order, LocalDateTime.parse("2025-07-03T03:03")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(47, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown840162"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown962407"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown995753"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message94232").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message94232").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message94232").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(null, "sku126415", 626288L, 9324, 39));
    }

    @Test
    void sample74() {
        var p = new Product("p703635", "sku515055", 363776L, 261, 52);
        var item = new CartItem(p, 1);
        assertEquals("p703635", item.getProduct().getId());
        assertEquals(1, item.getQuantity());
        assertEquals(363776L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a580314", "sku488972", 91378L, 610, 63);
        var b = new Product("b867379", "sku813464", 609629L, 1314, 31);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a580314", 7));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a580314"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a580314", 11));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        var coupon = new Coupon("code585750", CouponType.FIXED, 10715L, 415281L, 152968L, LocalDate.parse("2026-05-20"));
        assertEquals("code585750", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(10715L, coupon.getValue());
        assertEquals(415281L, coupon.getMinOrder());
        assertEquals(152968L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-05-20"), coupon.getExpiry());
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code403055", CouponType.FIXED, 220821L, 365061L, 221507L, LocalDate.parse("2024-10-29"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(637407L, coupon, LocalDate.parse("2024-12-01")));
    }

    @Test
    void sample78() {
        assertEquals(45000L, new ShippingCalculator().calculate(Region.SUBURB, 5923, 275288L));
    }

    @Test
    void sample79() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p642624", "sku237462", 854578L, 728, 63);
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order342957", null, null, Region.INNER_CITY, LocalDateTime.parse("2026-05-25T05:29")));
        assertEquals(63, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(63, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-05-26T15:22")));
        assertEquals(63, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown119598"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown125937"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown975034"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message797661").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message797661").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message797661").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p522228", "sku935165", 491112L, 3584, 32);
        assertEquals("p522228", p.getId());
        assertEquals("sku935165", p.getName());
        assertEquals(491112L, p.getPrice());
        assertEquals(3584, p.getWeightGrams());
        p.restoreStock(12);
        assertEquals(44, p.getStock());
        p.restoreStock(15);
        assertEquals(59, p.getStock());
        p.reduceStock(10);
        assertEquals(49, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-1));
        assertEquals(49, p.getStock());
        p.restoreStock(29);
        assertEquals(78, p.getStock());
    }

    @Test
    void sample82() {
        var p = new Product("p579266", "sku788962", 774850L, 447, 84);
        var item = new CartItem(p, 39);
        assertEquals("p579266", item.getProduct().getId());
        assertEquals(39, item.getQuantity());
        assertEquals(30219150L, item.getLineTotal());
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a224078", "sku236431", 715810L, 1027, 63);
        var b = new Product("b26653", "sku855568", 649916L, 81, 84);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a224078"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 17);
        assertEquals(11048572L, cart.getSubtotal());
        assertEquals(1377, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b26653", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(11048572L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other643861", 7));
        assertEquals(11048572L, cart.getSubtotal());
        assertEquals(1377, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b26653", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(11048572L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample84() {
        var coupon = new Coupon("code664386", CouponType.PERCENT, 82L, 150756L, 181475L, LocalDate.parse("2024-09-28"));
        assertEquals("code664386", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(82L, coupon.getValue());
        assertEquals(150756L, coupon.getMinOrder());
        assertEquals(181475L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-09-28"), coupon.getExpiry());
    }

    @Test
    void sample85() {
        var coupon = new Coupon("code341610", CouponType.FIXED, 129987L, 68442L, 97002L, LocalDate.parse("2024-12-15"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1108198L, coupon, LocalDate.parse("2025-02-11")));
    }

    @Test
    void sample86() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 23140, 539845L));
    }

    @Test
    void sample87() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p754727", "sku306371", 640437L, 1566, 59);
        cart.addItem(p, 1);
        var order = service.placeOrder("order639875", cart, null, Region.SUBURB, LocalDateTime.parse("2026-09-13T05:06"));
        assertEquals(58, p.getStock());
        assertEquals("order639875", order.getId());
        assertEquals(640437L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(640437L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-09-13T05:06"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p754727", order.getItems().get(0).getProduct().getId());
        assertEquals(1, order.getItems().get(0).getQuantity());
        assertEquals(640437L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(58, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(58, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(58, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(58, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown537557"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown39395"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown86642"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message727495").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message727495").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message727495").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        var p = new Product("p635565", "sku844653", 450015L, 19298, 85);
        assertEquals("p635565", p.getId());
        assertEquals("sku844653", p.getName());
        assertEquals(450015L, p.getPrice());
        assertEquals(19298, p.getWeightGrams());
        p.restoreStock(39);
        assertEquals(124, p.getStock());
        p.reduceStock(47);
        assertEquals(77, p.getStock());
        p.reduceStock(19);
        assertEquals(58, p.getStock());
    }

    @Test
    void sample90() {
        var p = new Product("p708076", "sku648226", 525930L, 1480, 18);
        var item = new CartItem(p, 2);
        assertEquals("p708076", item.getProduct().getId());
        assertEquals(2, item.getQuantity());
        assertEquals(1051860L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a611722", "sku289974", 573873L, 1118, 43);
        var b = new Product("b987050", "sku755813", 761281L, 1430, 67);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b987050"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other744335"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b987050", 49));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample92() {
        var coupon = new Coupon("code378242", CouponType.FIXED, 116988L, 248681L, 55265L, LocalDate.parse("2026-03-26"));
        assertEquals("code378242", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(116988L, coupon.getValue());
        assertEquals(248681L, coupon.getMinOrder());
        assertEquals(55265L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-03-26"), coupon.getExpiry());
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code85550", CouponType.PERCENT, 78L, 321067L, 281541L, LocalDate.parse("2025-10-19"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(230336L, coupon, LocalDate.parse("2025-10-29")));
    }

    @Test
    void sample94() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 19664, 754199L));
    }

    @Test
    void sample95() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p268048", "sku434588", 6133L, 1194, 70);
        cart.addItem(p, 18);
        var coupon = new Coupon("code25279", CouponType.FIXED, 148006L, 407033L, 242031L, LocalDate.parse("2024-10-29"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order835911", cart, coupon, Region.REMOTE, LocalDateTime.parse("2024-11-14T11:55")));
        assertEquals(70, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(70, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-11-17T03:15")));
        assertEquals(70, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-11-14T10:25")));
        assertEquals(70, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(70, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(70, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown791915"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.NullPointerException.class, () -> CouponType.valueOf(null));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message145010").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message145010").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message145010").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        var p = new Product("p242780", "sku127838", 183901L, 13263, 9);
        assertEquals("p242780", p.getId());
        assertEquals("sku127838", p.getName());
        assertEquals(183901L, p.getPrice());
        assertEquals(13263, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(57));
        assertEquals(9, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(31));
        assertEquals(9, p.getStock());
        p.restoreStock(56);
        assertEquals(65, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(0));
        assertEquals(65, p.getStock());
    }

    @Test
    void sample98() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 28));
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a933553", "sku123479", 916842L, 246, 67);
        var b = new Product("b631037", "sku786773", 648234L, 1291, 56);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b631037", 20));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 4);
        assertEquals(2592936L, cart.getSubtotal());
        assertEquals(5164, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b631037", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(2592936L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a933553"));
        assertEquals(2592936L, cart.getSubtotal());
        assertEquals(5164, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b631037", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(2592936L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a933553"));
        assertEquals(2592936L, cart.getSubtotal());
        assertEquals(5164, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b631037", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(2592936L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a933553"));
        assertEquals(2592936L, cart.getSubtotal());
        assertEquals(5164, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b631037", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(2592936L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a933553", 53));
        assertEquals(2592936L, cart.getSubtotal());
        assertEquals(5164, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b631037", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(2592936L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample100() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code179022", CouponType.PERCENT, -1L, -35772L, 204513L, LocalDate.parse("2025-02-25")));
    }
}
