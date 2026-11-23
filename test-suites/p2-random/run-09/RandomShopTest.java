package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p76328", "sku404046", 654616L, 12116, 44);
        assertEquals("p76328", p.getId());
        assertEquals("sku404046", p.getName());
        assertEquals(654616L, p.getPrice());
        assertEquals(12116, p.getWeightGrams());
        p.reduceStock(10);
        assertEquals(34, p.getStock());
        p.restoreStock(43);
        assertEquals(77, p.getStock());
        p.reduceStock(4);
        assertEquals(73, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p563830", "sku376805", 920726L, 904, 24);
        var item = new CartItem(p, 35);
        assertEquals("p563830", item.getProduct().getId());
        assertEquals(35, item.getQuantity());
        assertEquals(32225410L, item.getLineTotal());
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a918718", "sku362503", 547263L, 183, 45);
        var b = new Product("b270151", "sku356786", 496811L, 748, 53);
        cart.addItem(b, 24);
        assertEquals(11923464L, cart.getSubtotal());
        assertEquals(17952, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b270151", cart.getItems().get(0).getProduct().getId());
        assertEquals(24, cart.getItems().get(0).getQuantity());
        assertEquals(11923464L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b270151");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a918718"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample4() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 8L, 251283L, 116057L, LocalDate.parse("2024-08-23")));
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code17355", CouponType.FIXED, 74449L, 214377L, 69429L, LocalDate.parse("2025-04-16"));
        assertEquals(74449L, new PricingService().calculateDiscount(940626L, coupon, LocalDate.parse("2025-02-25")));
    }

    @Test
    void sample6() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.SUBURB, 21737, 1029676L));
    }

    @Test
    void sample7() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
    }

    @Test
    void sample8() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown742374"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message969438").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message969438").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message969438").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p435165", "sku425716", 187356L, 8976, -1));
    }

    @Test
    void sample10() {
        var p = new Product("p599496", "sku567374", 40672L, 1483, 2);
        var item = new CartItem(p, 11);
        assertEquals("p599496", item.getProduct().getId());
        assertEquals(11, item.getQuantity());
        assertEquals(447392L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a246950", "sku593235", 370226L, 478, 65);
        var b = new Product("b435759", "sku489930", 371714L, 1363, 1);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b435759"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a246950"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a246950"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code957420", null, 70338L, 221316L, -19642L, LocalDate.parse("2027-12-07")));
    }

    @Test
    void sample13() {
        var coupon = new Coupon("code557913", CouponType.FIXED, 304709L, 466246L, 81969L, LocalDate.parse("2025-06-07"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(724671L, coupon, LocalDate.parse("2025-06-11")));
    }

    @Test
    void sample14() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.PROVINCE, 24995, 882045L));
    }

    @Test
    void sample15() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p505503", "sku386336", 424551L, 796, 55);
        cart.addItem(p, 1);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(null, cart, null, Region.SUBURB, LocalDateTime.parse("2025-12-02T17:04")));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(55, p.getStock());
    }

    @Test
    void sample16() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown782228"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown194395"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown874010"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message803207").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message803207").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message803207").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p687308", "sku192755", 648833L, 9893, 14);
        assertEquals("p687308", p.getId());
        assertEquals("sku192755", p.getName());
        assertEquals(648833L, p.getPrice());
        assertEquals(9893, p.getWeightGrams());
        p.restoreStock(27);
        assertEquals(41, p.getStock());
        p.reduceStock(13);
        assertEquals(28, p.getStock());
        p.restoreStock(25);
        assertEquals(53, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p183023", "sku245784", 62526L, 672, 50);
        var item = new CartItem(p, 15);
        assertEquals("p183023", item.getProduct().getId());
        assertEquals(15, item.getQuantity());
        assertEquals(937890L, item.getLineTotal());
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a233095", "sku782793", 128847L, 303, 52);
        var b = new Product("b29720", "sku345761", 172760L, 1516, 64);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b29720"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b29720", 46));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other715281"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample20() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code615710", null, 497515L, 172611L, 102675L, LocalDate.parse("2026-05-09")));
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code932565", CouponType.PERCENT, 85L, 457741L, 81760L, LocalDate.parse("2024-06-24"));
        assertThrows(shop.CouponNotApplicableException.class, () -> new PricingService().calculateDiscount(411597L, coupon, LocalDate.parse("2024-06-02")));
    }

    @Test
    void sample22() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 19641, 632537L));
    }

    @Test
    void sample23() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p425335", "sku299282", 724478L, 902, 41);
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order173394", null, null, Region.REMOTE, LocalDateTime.parse("2025-04-07T20:37")));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(41, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown106221"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown57085"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message552406").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message552406").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message552406").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(null, "sku997785", 602080L, 19490, 34));
    }

    @Test
    void sample26() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 36));
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a411379", "sku235538", 119021L, 1418, 17);
        var b = new Product("b49979", "sku441693", 831326L, 114, 38);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a411379"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b49979", 44));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 21));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 24));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample28() {
        var coupon = new Coupon("code662536", CouponType.FIXED, 204968L, 192660L, 232459L, LocalDate.parse("2025-12-25"));
        assertEquals("code662536", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(204968L, coupon.getValue());
        assertEquals(192660L, coupon.getMinOrder());
        assertEquals(232459L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-12-25"), coupon.getExpiry());
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code53660", CouponType.FIXED, 524611L, 64093L, 153644L, LocalDate.parse("2025-03-13"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1232636L, coupon, LocalDate.parse("2025-03-24")));
    }

    @Test
    void sample30() {
        assertEquals(79000L, new ShippingCalculator().calculate(Region.INNER_CITY, 16789, 62939L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p371044", "sku487520", 587685L, 1382, 9);
        var coupon = new Coupon("code956765", CouponType.PERCENT, 50L, 166713L, 272741L, LocalDate.parse("2026-01-11"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order490401", null, coupon, Region.SUBURB, LocalDateTime.parse("2026-03-11T01:54")));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-03-11T08:01")));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-03-14T09:24")));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-03-11T02:00")));
        assertEquals(9, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message49047").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message49047").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message49047").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p828441", "sku872425", 560808L, 13791, 11);
        assertEquals("p828441", p.getId());
        assertEquals("sku872425", p.getName());
        assertEquals(560808L, p.getPrice());
        assertEquals(13791, p.getWeightGrams());
        p.restoreStock(9);
        assertEquals(20, p.getStock());
        p.restoreStock(11);
        assertEquals(31, p.getStock());
        p.reduceStock(19);
        assertEquals(12, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(60));
        assertEquals(12, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p436773", "sku368555", 15664L, 325, 10);
        var item = new CartItem(p, 28);
        assertEquals("p436773", item.getProduct().getId());
        assertEquals(28, item.getQuantity());
        assertEquals(438592L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a156926", "sku505799", 845301L, 1403, 80);
        var b = new Product("b520390", "sku293081", 852393L, 1403, 64);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b520390", 46));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a156926", 50));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample36() {
        var coupon = new Coupon("code949160", CouponType.PERCENT, 70L, 206685L, 174486L, LocalDate.parse("2027-01-25"));
        assertEquals("code949160", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(70L, coupon.getValue());
        assertEquals(206685L, coupon.getMinOrder());
        assertEquals(174486L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-01-25"), coupon.getExpiry());
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code828671", CouponType.PERCENT, 59L, 133156L, 23343L, LocalDate.parse("2027-10-11"));
        assertEquals(23343L, new PricingService().calculateDiscount(997290L, coupon, LocalDate.parse("2027-09-28")));
    }

    @Test
    void sample38() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 18376, 1098311L));
    }

    @Test
    void sample39() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p98736", "sku761536", 994428L, 567, 76);
        cart.addItem(p, 12);
        var coupon = new Coupon("code372336", CouponType.FIXED, 477080L, 517489L, 142040L, LocalDate.parse("2024-09-28"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order947249", cart, coupon, Region.SUBURB, null));
        assertEquals(76, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-09-04T05:08")));
        assertEquals(76, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(76, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-09-03T11:13")));
        assertEquals(76, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(76, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-09-04T00:46")));
        assertEquals(76, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown717519"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message658385").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message658385").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message658385").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        var p = new Product("p230551", "sku826117", 188938L, 16183, 73);
        assertEquals("p230551", p.getId());
        assertEquals("sku826117", p.getName());
        assertEquals(188938L, p.getPrice());
        assertEquals(16183, p.getWeightGrams());
        p.reduceStock(27);
        assertEquals(46, p.getStock());
        p.reduceStock(45);
        assertEquals(1, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(32));
        assertEquals(1, p.getStock());
        p.restoreStock(35);
        assertEquals(36, p.getStock());
        p.reduceStock(19);
        assertEquals(17, p.getStock());
    }

    @Test
    void sample42() {
        var p = new Product("p279395", "sku479653", 448056L, 545, 70);
        var item = new CartItem(p, 9);
        assertEquals("p279395", item.getProduct().getId());
        assertEquals(9, item.getQuantity());
        assertEquals(4032504L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a159587", "sku580939", 765636L, 268, 1);
        var b = new Product("b260364", "sku913756", 852675L, 408, 40);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 0));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 24);
        assertEquals(20464200L, cart.getSubtotal());
        assertEquals(9792, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b260364", cart.getItems().get(0).getProduct().getId());
        assertEquals(24, cart.getItems().get(0).getQuantity());
        assertEquals(20464200L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 58));
        assertEquals(20464200L, cart.getSubtotal());
        assertEquals(9792, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b260364", cart.getItems().get(0).getProduct().getId());
        assertEquals(24, cart.getItems().get(0).getQuantity());
        assertEquals(20464200L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 46));
        assertEquals(20464200L, cart.getSubtotal());
        assertEquals(9792, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b260364", cart.getItems().get(0).getProduct().getId());
        assertEquals(24, cart.getItems().get(0).getQuantity());
        assertEquals(20464200L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample44() {
        var coupon = new Coupon("code740764", CouponType.PERCENT, 52L, 203972L, 145358L, LocalDate.parse("2027-08-16"));
        assertEquals("code740764", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(52L, coupon.getValue());
        assertEquals(203972L, coupon.getMinOrder());
        assertEquals(145358L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-08-16"), coupon.getExpiry());
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code450395", CouponType.FIXED, 176174L, 38366L, 258508L, LocalDate.parse("2025-01-08"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(949578L, coupon, LocalDate.parse("2025-02-25")));
    }

    @Test
    void sample46() {
        assertEquals(65000L, new ShippingCalculator().calculate(Region.INNER_CITY, 13068, 433748L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p743111", "sku245108", 597068L, 91, 58);
        cart.addItem(p, 11);
        var order = service.placeOrder("order976649", cart, null, Region.INNER_CITY, LocalDateTime.parse("2027-07-19T13:56"));
        assertEquals(47, p.getStock());
        assertEquals("order976649", order.getId());
        assertEquals(6567748L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(6567748L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-07-19T13:56"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p743111", order.getItems().get(0).getProduct().getId());
        assertEquals(11, order.getItems().get(0).getQuantity());
        assertEquals(6567748L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(47, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(47, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(47, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message552454").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message552454").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message552454").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        var p = new Product("p352766", "sku523694", 227889L, 14081, 27);
        assertEquals("p352766", p.getId());
        assertEquals("sku523694", p.getName());
        assertEquals(227889L, p.getPrice());
        assertEquals(14081, p.getWeightGrams());
        p.restoreStock(38);
        assertEquals(65, p.getStock());
        p.reduceStock(46);
        assertEquals(19, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(32));
        assertEquals(19, p.getStock());
        p.restoreStock(59);
        assertEquals(78, p.getStock());
    }

    @Test
    void sample50() {
        var p = new Product("p458394", "sku759445", 471072L, 731, 17);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -5));
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a645482", "sku266454", 106073L, 839, 44);
        var b = new Product("b400372", "sku323727", 216732L, 42, 29);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem(" "));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a645482"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a645482"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 40);
        assertEquals(4242920L, cart.getSubtotal());
        assertEquals(33560, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a645482", cart.getItems().get(0).getProduct().getId());
        assertEquals(40, cart.getItems().get(0).getQuantity());
        assertEquals(4242920L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b400372", 0));
        assertEquals(4242920L, cart.getSubtotal());
        assertEquals(33560, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a645482", cart.getItems().get(0).getProduct().getId());
        assertEquals(40, cart.getItems().get(0).getQuantity());
        assertEquals(4242920L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 12));
        assertEquals(4242920L, cart.getSubtotal());
        assertEquals(33560, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a645482", cart.getItems().get(0).getProduct().getId());
        assertEquals(40, cart.getItems().get(0).getQuantity());
        assertEquals(4242920L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("a645482", 14);
        assertEquals(1485022L, cart.getSubtotal());
        assertEquals(11746, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a645482", cart.getItems().get(0).getProduct().getId());
        assertEquals(14, cart.getItems().get(0).getQuantity());
        assertEquals(1485022L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample52() {
        var coupon = new Coupon("code299654", CouponType.PERCENT, 57L, 524051L, 192164L, LocalDate.parse("2026-06-05"));
        assertEquals("code299654", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(57L, coupon.getValue());
        assertEquals(524051L, coupon.getMinOrder());
        assertEquals(192164L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-06-05"), coupon.getExpiry());
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code449530", CouponType.PERCENT, 21L, 364894L, 44455L, LocalDate.parse("2027-01-10"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-41575L, coupon, LocalDate.parse("2027-02-14")));
    }

    @Test
    void sample54() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.SUBURB, 20385, 871374L));
    }

    @Test
    void sample55() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p273566", "sku304619", 16501L, 452, 44);
        cart.addItem(p, 3);
        var order = service.placeOrder("order802333", cart, null, Region.PROVINCE, LocalDateTime.parse("2025-07-08T00:43"));
        assertEquals(41, p.getStock());
        assertEquals("order802333", order.getId());
        assertEquals(49503L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(37000L, order.getShipping());
        assertEquals(86503L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-07-08T00:43"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p273566", order.getItems().get(0).getProduct().getId());
        assertEquals(3, order.getItems().get(0).getQuantity());
        assertEquals(49503L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2025-07-07T17:23")));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(41, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(41, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown696303"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown897543"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message643878").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message643878").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message643878").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(null, "sku427344", 220263L, 9896, 0));
    }

    @Test
    void sample58() {
        var p = new Product("p566566", "sku249536", 632427L, 537, 72);
        var item = new CartItem(p, 40);
        assertEquals("p566566", item.getProduct().getId());
        assertEquals(40, item.getQuantity());
        assertEquals(25297080L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a936057", "sku307923", 391829L, 1164, 3);
        var b = new Product("b258904", "sku416891", 301049L, 1057, 21);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b258904"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a936057"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a936057"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample60() {
        var coupon = new Coupon("code798046", CouponType.PERCENT, 84L, 390481L, 164000L, LocalDate.parse("2026-11-05"));
        assertEquals("code798046", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(84L, coupon.getValue());
        assertEquals(390481L, coupon.getMinOrder());
        assertEquals(164000L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-11-05"), coupon.getExpiry());
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code933695", CouponType.PERCENT, 34L, 389950L, 22089L, LocalDate.parse("2024-01-12"));
        assertEquals(22089L, new PricingService().calculateDiscount(694371L, coupon, LocalDate.parse("2023-11-20")));
    }

    @Test
    void sample62() {
        assertEquals(113000L, new ShippingCalculator().calculate(Region.REMOTE, 15207, 772040L));
    }

    @Test
    void sample63() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p742374", "sku326550", 502076L, 1211, 9);
        var coupon = new Coupon("code964643", CouponType.PERCENT, 35L, 310527L, 88444L, LocalDate.parse("2024-09-02"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order651225", null, coupon, Region.PROVINCE, LocalDateTime.parse("2024-09-08T19:22")));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-09-09T04:36")));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-09-09T18:53")));
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(9, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown21265"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown203818"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message475337").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message475337").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message475337").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        var p = new Product("p449994", "sku300799", 594599L, 17667, 17);
        assertEquals("p449994", p.getId());
        assertEquals("sku300799", p.getName());
        assertEquals(594599L, p.getPrice());
        assertEquals(17667, p.getWeightGrams());
        p.reduceStock(6);
        assertEquals(11, p.getStock());
        p.restoreStock(60);
        assertEquals(71, p.getStock());
        p.restoreStock(29);
        assertEquals(100, p.getStock());
    }

    @Test
    void sample66() {
        var p = new Product("p24811", "sku664472", 798067L, 698, 43);
        var item = new CartItem(p, 4);
        assertEquals("p24811", item.getProduct().getId());
        assertEquals(4, item.getQuantity());
        assertEquals(3192268L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a478483", "sku202972", 559462L, 986, 68);
        var b = new Product("b123763", "sku497137", 670506L, 1584, 70);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b123763", 33));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b123763"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a478483", 26));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 14);
        assertEquals(9387084L, cart.getSubtotal());
        assertEquals(22176, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b123763", cart.getItems().get(0).getProduct().getId());
        assertEquals(14, cart.getItems().get(0).getQuantity());
        assertEquals(9387084L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 44);
        assertEquals(34003412L, cart.getSubtotal());
        assertEquals(65560, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b123763", cart.getItems().get(0).getProduct().getId());
        assertEquals(14, cart.getItems().get(0).getQuantity());
        assertEquals(9387084L, cart.getItems().get(0).getLineTotal());
        assertEquals("a478483", cart.getItems().get(1).getProduct().getId());
        assertEquals(44, cart.getItems().get(1).getQuantity());
        assertEquals(24616328L, cart.getItems().get(1).getLineTotal());
        cart.updateQuantity("b123763", 42);
        assertEquals(52777580L, cart.getSubtotal());
        assertEquals(109912, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b123763", cart.getItems().get(0).getProduct().getId());
        assertEquals(42, cart.getItems().get(0).getQuantity());
        assertEquals(28161252L, cart.getItems().get(0).getLineTotal());
        assertEquals("a478483", cart.getItems().get(1).getProduct().getId());
        assertEquals(44, cart.getItems().get(1).getQuantity());
        assertEquals(24616328L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample68() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code492403", CouponType.PERCENT, 106L, 97782L, 293958L, LocalDate.parse("2027-12-29")));
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code254163", CouponType.PERCENT, 78L, 76310L, 18186L, LocalDate.parse("2024-03-29"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1200984L, coupon, LocalDate.parse("2024-04-27")));
    }

    @Test
    void sample70() {
        assertEquals(49000L, new ShippingCalculator().calculate(Region.PROVINCE, 4283, 503492L));
    }

    @Test
    void sample71() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p61506", "sku792295", 257310L, 731, 37);
        cart.addItem(p, 9);
        var order = service.placeOrder("order957900", cart, null, Region.INNER_CITY, LocalDateTime.parse("2025-05-11T02:42"));
        assertEquals(28, p.getStock());
        assertEquals("order957900", order.getId());
        assertEquals(2315790L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(2315790L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-05-11T02:42"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p61506", order.getItems().get(0).getProduct().getId());
        assertEquals(9, order.getItems().get(0).getQuantity());
        assertEquals(2315790L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(28, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2025-05-10T15:22")));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(28, p.getStock());
        assertEquals(1852632L, service.cancelOrder(order, LocalDateTime.parse("2025-05-14T19:43")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2025-05-12T03:37")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2025-05-10T19:44")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(37, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown599399"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown972507"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message322488").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message322488").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message322488").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        var p = new Product("p776772", "sku747549", 415952L, 1444, 85);
        assertEquals("p776772", p.getId());
        assertEquals("sku747549", p.getName());
        assertEquals(415952L, p.getPrice());
        assertEquals(1444, p.getWeightGrams());
        p.reduceStock(16);
        assertEquals(69, p.getStock());
        p.restoreStock(9);
        assertEquals(78, p.getStock());
    }

    @Test
    void sample74() {
        var p = new Product("p163429", "sku452292", 61078L, 752, 61);
        var item = new CartItem(p, 38);
        assertEquals("p163429", item.getProduct().getId());
        assertEquals(38, item.getQuantity());
        assertEquals(2320964L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a498099", "sku461472", 277237L, 990, 31);
        var b = new Product("b695919", "sku618562", 193250L, 864, 26);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b695919", 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b695919", 32));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b695919"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 43));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 10);
        assertEquals(2772370L, cart.getSubtotal());
        assertEquals(9900, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a498099", cart.getItems().get(0).getProduct().getId());
        assertEquals(10, cart.getItems().get(0).getQuantity());
        assertEquals(2772370L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample76() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 136892L, 563716L, 152978L, LocalDate.parse("2026-11-12")));
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code126107", CouponType.FIXED, 232087L, 547476L, 68694L, LocalDate.parse("2024-03-31"));
        assertEquals(232087L, new PricingService().calculateDiscount(1243934L, coupon, LocalDate.parse("2024-02-04")));
    }

    @Test
    void sample78() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.REMOTE, 21384, -45861L));
    }

    @Test
    void sample79() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p812770", "sku524558", 205311L, 870, 68);
        cart.addItem(p, 9);
        var coupon = new Coupon("code884972", CouponType.PERCENT, 62L, 24538L, 146367L, LocalDate.parse("2025-07-18"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order113187", cart, coupon, null, LocalDateTime.parse("2025-07-09T05:07")));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-07-08T17:09")));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-07-13T04:31")));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-07-09T11:24")));
        assertEquals(68, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown593669"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown377180"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown79620"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message973988").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message973988").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message973988").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p978508", "sku896721", 348325L, 17144, 13);
        assertEquals("p978508", p.getId());
        assertEquals("sku896721", p.getName());
        assertEquals(348325L, p.getPrice());
        assertEquals(17144, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(0));
        assertEquals(13, p.getStock());
        p.restoreStock(13);
        assertEquals(26, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(36));
        assertEquals(26, p.getStock());
        p.reduceStock(15);
        assertEquals(11, p.getStock());
    }

    @Test
    void sample82() {
        var p = new Product("p912488", "sku244321", 864044L, 310, 41);
        var item = new CartItem(p, 58);
        assertEquals("p912488", item.getProduct().getId());
        assertEquals(58, item.getQuantity());
        assertEquals(50114552L, item.getLineTotal());
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a779017", "sku887413", 105803L, 1415, 10);
        var b = new Product("b342192", "sku558419", 528583L, 53, 38);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other879903", 9));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 3);
        assertEquals(1585749L, cart.getSubtotal());
        assertEquals(159, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b342192", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1585749L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other809766", 43));
        assertEquals(1585749L, cart.getSubtotal());
        assertEquals(159, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b342192", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1585749L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 3));
        assertEquals(1585749L, cart.getSubtotal());
        assertEquals(159, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b342192", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1585749L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 4));
        assertEquals(1585749L, cart.getSubtotal());
        assertEquals(159, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b342192", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1585749L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 53));
        assertEquals(1585749L, cart.getSubtotal());
        assertEquals(159, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b342192", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1585749L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("b342192", 31);
        assertEquals(16386073L, cart.getSubtotal());
        assertEquals(1643, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b342192", cart.getItems().get(0).getProduct().getId());
        assertEquals(31, cart.getItems().get(0).getQuantity());
        assertEquals(16386073L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample84() {
        var coupon = new Coupon("code221493", CouponType.FIXED, 48465L, 96419L, 160917L, LocalDate.parse("2027-03-06"));
        assertEquals("code221493", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(48465L, coupon.getValue());
        assertEquals(96419L, coupon.getMinOrder());
        assertEquals(160917L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-03-06"), coupon.getExpiry());
    }

    @Test
    void sample85() {
        var coupon = new Coupon("code493425", CouponType.FIXED, 180166L, 282515L, 218837L, LocalDate.parse("2027-09-12"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(560361L, coupon, null));
    }

    @Test
    void sample86() {
        assertEquals(47000L, new ShippingCalculator().calculate(Region.PROVINCE, 3978, 388771L));
    }

    @Test
    void sample87() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample88() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message923634").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message923634").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message923634").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        var p = new Product("p176265", "sku918019", 188161L, 17468, 73);
        assertEquals("p176265", p.getId());
        assertEquals("sku918019", p.getName());
        assertEquals(188161L, p.getPrice());
        assertEquals(17468, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-1));
        assertEquals(73, p.getStock());
        p.restoreStock(33);
        assertEquals(106, p.getStock());
    }

    @Test
    void sample90() {
        var p = new Product("p807740", "sku519428", 738327L, 445, 5);
        var item = new CartItem(p, 1);
        assertEquals("p807740", item.getProduct().getId());
        assertEquals(1, item.getQuantity());
        assertEquals(738327L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a995291", "sku674116", 337268L, 924, 28);
        var b = new Product("b108156", "sku484376", 115108L, 515, 4);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 27));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b108156", 15));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a995291"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 18);
        assertEquals(6070824L, cart.getSubtotal());
        assertEquals(16632, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a995291", cart.getItems().get(0).getProduct().getId());
        assertEquals(18, cart.getItems().get(0).getQuantity());
        assertEquals(6070824L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 8);
        assertEquals(8768968L, cart.getSubtotal());
        assertEquals(24024, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a995291", cart.getItems().get(0).getProduct().getId());
        assertEquals(26, cart.getItems().get(0).getQuantity());
        assertEquals(8768968L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample92() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, null, 540904L, 213562L, 296412L, LocalDate.parse("2027-05-04")));
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code578968", CouponType.PERCENT, 52L, 182635L, 39969L, LocalDate.parse("2026-01-25"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(192756L, coupon, LocalDate.parse("2026-01-31")));
    }

    @Test
    void sample94() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 10049, 804913L));
    }

    @Test
    void sample95() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p994831", "sku240340", 484045L, 1196, 18);
        cart.addItem(p, 16);
        var coupon = new Coupon("code33884", CouponType.FIXED, 156672L, 368949L, 42767L, LocalDate.parse("2026-10-02"));
        var order = service.placeOrder("order252652", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2026-10-02T15:41"));
        assertEquals(2, p.getStock());
        assertEquals("order252652", order.getId());
        assertEquals(7744720L, order.getSubtotal());
        assertEquals(156672L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(7588048L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-10-02T15:41"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p994831", order.getItems().get(0).getProduct().getId());
        assertEquals(16, order.getItems().get(0).getQuantity());
        assertEquals(7744720L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-10-01T23:50")));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(2, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(2, p.getStock());
        assertEquals(6070438L, service.cancelOrder(order, LocalDateTime.parse("2026-10-06T09:19")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(18, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf(" "));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertEquals("InsufficientStockException", new InsufficientStockException("message634598").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message634598").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message634598").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p867176", null, 580524L, 10749, 67));
    }

    @Test
    void sample98() {
        var p = new Product("p647353", "sku709855", 738986L, 369, 27);
        var item = new CartItem(p, 34);
        assertEquals("p647353", item.getProduct().getId());
        assertEquals(34, item.getQuantity());
        assertEquals(25125524L, item.getLineTotal());
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a901769", "sku737996", 497138L, 1388, 8);
        var b = new Product("b79665", "sku810490", 457399L, 641, 25);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a901769"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b79665", 48));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b79665", 17));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b79665", 1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a901769", 54));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -4));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample100() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code733473", CouponType.FIXED, 402729L, -7794L, 216829L, LocalDate.parse("2027-05-19")));
    }
}
