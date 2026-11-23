package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p990267", null, 314313L, 8055, 29));
    }

    @Test
    void sample2() {
        var p = new Product("p138678", "sku761441", 362503L, 595, 33);
        var item = new CartItem(p, 3);
        assertEquals("p138678", item.getProduct().getId());
        assertEquals(3, item.getQuantity());
        assertEquals(1087509L, item.getLineTotal());
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a168129", "sku965781", 336172L, 65, 65);
        var b = new Product("b245817", "sku483862", 259908L, 901, 55);
        cart.addItem(a, 7);
        assertEquals(2353204L, cart.getSubtotal());
        assertEquals(455, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a168129", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(2353204L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 49);
        assertEquals(15088696L, cart.getSubtotal());
        assertEquals(44604, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a168129", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(2353204L, cart.getItems().get(0).getLineTotal());
        assertEquals("b245817", cart.getItems().get(1).getProduct().getId());
        assertEquals(49, cart.getItems().get(1).getQuantity());
        assertEquals(12735492L, cart.getItems().get(1).getLineTotal());
        cart.updateQuantity("b245817", 27);
        assertEquals(9370720L, cart.getSubtotal());
        assertEquals(24782, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a168129", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(2353204L, cart.getItems().get(0).getLineTotal());
        assertEquals("b245817", cart.getItems().get(1).getProduct().getId());
        assertEquals(27, cart.getItems().get(1).getQuantity());
        assertEquals(7017516L, cart.getItems().get(1).getLineTotal());
        cart.removeItem("b245817");
        assertEquals(2353204L, cart.getSubtotal());
        assertEquals(455, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a168129", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(2353204L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b245817", 52));
        assertEquals(2353204L, cart.getSubtotal());
        assertEquals(455, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a168129", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(2353204L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b245817", 19));
        assertEquals(2353204L, cart.getSubtotal());
        assertEquals(455, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a168129", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(2353204L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 18));
        assertEquals(2353204L, cart.getSubtotal());
        assertEquals(455, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a168129", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(2353204L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample4() {
        var coupon = new Coupon("code516428", CouponType.FIXED, 398401L, 150280L, 51871L, LocalDate.parse("2024-08-22"));
        assertEquals("code516428", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(398401L, coupon.getValue());
        assertEquals(150280L, coupon.getMinOrder());
        assertEquals(51871L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-08-22"), coupon.getExpiry());
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code126849", CouponType.PERCENT, 64L, 468573L, 33502L, LocalDate.parse("2027-07-15"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(495192L, coupon, null));
    }

    @Test
    void sample6() {
        assertEquals(73000L, new ShippingCalculator().calculate(Region.PROVINCE, 10274, 94290L));
    }

    @Test
    void sample7() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p460220", "sku730082", 185580L, 1297, 41);
        cart.addItem(p, 8);
        var coupon = new Coupon("code465046", CouponType.PERCENT, 65L, 332667L, 279869L, LocalDate.parse("2027-10-16"));
        var order = service.placeOrder("order35635", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2027-09-12T02:30"));
        assertEquals(33, p.getStock());
        assertEquals("order35635", order.getId());
        assertEquals(1484640L, order.getSubtotal());
        assertEquals(279869L, order.getDiscount());
        assertEquals(73000L, order.getShipping());
        assertEquals(1277771L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-09-12T02:30"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p460220", order.getItems().get(0).getProduct().getId());
        assertEquals(8, order.getItems().get(0).getQuantity());
        assertEquals(1484640L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(33, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(33, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown68891"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertEquals("InsufficientStockException", new InsufficientStockException("message438747").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message438747").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message438747").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        var p = new Product("p722898", "sku809678", 946511L, 7214, 6);
        assertEquals("p722898", p.getId());
        assertEquals("sku809678", p.getName());
        assertEquals(946511L, p.getPrice());
        assertEquals(7214, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(55));
        assertEquals(6, p.getStock());
        p.restoreStock(6);
        assertEquals(12, p.getStock());
        p.restoreStock(34);
        assertEquals(46, p.getStock());
        p.restoreStock(35);
        assertEquals(81, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-5));
        assertEquals(81, p.getStock());
    }

    @Test
    void sample10() {
        var p = new Product("p245636", "sku336268", 820103L, 938, 46);
        var item = new CartItem(p, 14);
        assertEquals("p245636", item.getProduct().getId());
        assertEquals(14, item.getQuantity());
        assertEquals(11481442L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a786200", "sku249523", 738900L, 74, 35);
        var b = new Product("b297144", "sku284123", 625042L, 1177, 34);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a786200"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other902268", 44));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a786200"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 79070L, 485920L, 297381L, LocalDate.parse("2025-01-21")));
    }

    @Test
    void sample13() {
        var coupon = new Coupon("code831240", CouponType.FIXED, 227549L, 34366L, 190049L, LocalDate.parse("2026-12-27"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(1089982L, coupon, null));
    }

    @Test
    void sample14() {
        assertEquals(83000L, new ShippingCalculator().calculate(Region.REMOTE, 7901, 820560L));
    }

    @Test
    void sample15() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p329436", "sku385532", 783096L, 784, 74);
        cart.addItem(p, 15);
        var coupon = new Coupon("code136873", CouponType.PERCENT, 25L, 29172L, 176946L, LocalDate.parse("2025-05-07"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order170769", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2025-07-04T07:01")));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-07-04T19:33")));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-07-07T08:55")));
        assertEquals(74, p.getStock());
    }

    @Test
    void sample16() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown695375"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown498540"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message369776").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message369776").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message369776").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p931980", "sku161243", 895760L, 17505, 62);
        assertEquals("p931980", p.getId());
        assertEquals("sku161243", p.getName());
        assertEquals(895760L, p.getPrice());
        assertEquals(17505, p.getWeightGrams());
        p.reduceStock(28);
        assertEquals(34, p.getStock());
        p.reduceStock(16);
        assertEquals(18, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p404732", "sku365722", 856404L, 163, 39);
        var item = new CartItem(p, 9);
        assertEquals("p404732", item.getProduct().getId());
        assertEquals(9, item.getQuantity());
        assertEquals(7707636L, item.getLineTotal());
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a523422", "sku955654", 447734L, 303, 24);
        var b = new Product("b98917", "sku427631", 464894L, 1286, 14);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 33));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b98917"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b98917"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a523422"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample20() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code574879", CouponType.PERCENT, 121L, -47144L, 34862L, LocalDate.parse("2027-04-14")));
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code698979", CouponType.PERCENT, 73L, 69160L, 17942L, LocalDate.parse("2025-05-15"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(13731L, coupon, LocalDate.parse("2025-06-15")));
    }

    @Test
    void sample22() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 16957, 193339L));
    }

    @Test
    void sample23() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p491505", "sku157122", 444254L, 1572, 11);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 12));
        var coupon = new Coupon("code159931", CouponType.PERCENT, 75L, 191835L, 276915L, LocalDate.parse("2024-11-23"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order237865", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2024-10-05T01:34")));
        assertEquals(11, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(11, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-10-08T03:48")));
        assertEquals(11, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(11, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(11, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(11, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown867781"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown923230"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample25() {
        var p = new Product("p30313", "sku257260", 33686L, 9876, 78);
        assertEquals("p30313", p.getId());
        assertEquals("sku257260", p.getName());
        assertEquals(33686L, p.getPrice());
        assertEquals(9876, p.getWeightGrams());
        p.restoreStock(5);
        assertEquals(83, p.getStock());
        p.reduceStock(41);
        assertEquals(42, p.getStock());
        p.reduceStock(12);
        assertEquals(30, p.getStock());
    }

    @Test
    void sample26() {
        var p = new Product("p915703", "sku771658", 358508L, 448, 26);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -4));
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a824217", "sku699055", 340958L, 728, 48);
        var b = new Product("b220424", "sku187259", 839363L, 1071, 65);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b220424", 21));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample28() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.FIXED, 109506L, 568272L, 80436L, LocalDate.parse("2027-07-19")));
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code482381", CouponType.FIXED, 338965L, 284969L, 197087L, LocalDate.parse("2027-01-22"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(67045L, coupon, LocalDate.parse("2027-01-28")));
    }

    @Test
    void sample30() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 1895, 1159676L));
    }

    @Test
    void sample31() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p121585", "sku662651", 109175L, 759, 13);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 15));
        var coupon = new Coupon("code240390", CouponType.FIXED, 233258L, 44386L, 278510L, LocalDate.parse("2026-10-23"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order68503", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2026-08-28T16:28")));
        assertEquals(13, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-08-30T17:48")));
        assertEquals(13, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-08-28T01:25")));
        assertEquals(13, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(13, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(13, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf(" "));
        assertEquals("InsufficientStockException", new InsufficientStockException("message920927").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message920927").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message920927").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p780928", "sku215007", 453065L, 2567, 76);
        assertEquals("p780928", p.getId());
        assertEquals("sku215007", p.getName());
        assertEquals(453065L, p.getPrice());
        assertEquals(2567, p.getWeightGrams());
        p.reduceStock(32);
        assertEquals(44, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(52));
        assertEquals(44, p.getStock());
        p.restoreStock(27);
        assertEquals(71, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p996913", "sku408577", 522095L, 368, 9);
        var item = new CartItem(p, 31);
        assertEquals("p996913", item.getProduct().getId());
        assertEquals(31, item.getQuantity());
        assertEquals(16184945L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a246030", "sku153434", 874408L, 553, 16);
        var b = new Product("b923201", "sku574844", 492648L, 125, 15);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other904019"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b923201", 45));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b923201", 55));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a246030"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a246030"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample36() {
        var coupon = new Coupon("code316457", CouponType.FIXED, 452910L, 393568L, 279877L, LocalDate.parse("2024-06-02"));
        assertEquals("code316457", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(452910L, coupon.getValue());
        assertEquals(393568L, coupon.getMinOrder());
        assertEquals(279877L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-06-02"), coupon.getExpiry());
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code263471", CouponType.FIXED, 548203L, 206437L, 234832L, LocalDate.parse("2025-12-23"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(280205L, coupon, LocalDate.parse("2026-01-10")));
    }

    @Test
    void sample38() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 3676, 607064L));
    }

    @Test
    void sample39() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p844265", "sku24785", 409821L, 1561, 37);
        cart.addItem(p, 7);
        var coupon = new Coupon("code545196", CouponType.PERCENT, 56L, 316520L, 16986L, LocalDate.parse("2027-08-06"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order756366", cart, coupon, Region.SUBURB, null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(37, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown136722"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown558524"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message691344").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message691344").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message691344").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        var p = new Product("p489036", "sku728502", 758206L, 2423, 82);
        assertEquals("p489036", p.getId());
        assertEquals("sku728502", p.getName());
        assertEquals(758206L, p.getPrice());
        assertEquals(2423, p.getWeightGrams());
        p.restoreStock(30);
        assertEquals(112, p.getStock());
        p.reduceStock(16);
        assertEquals(96, p.getStock());
        p.reduceStock(46);
        assertEquals(50, p.getStock());
    }

    @Test
    void sample42() {
        var p = new Product("p276787", "sku305641", 892833L, 291, 20);
        var item = new CartItem(p, 2);
        assertEquals("p276787", item.getProduct().getId());
        assertEquals(2, item.getQuantity());
        assertEquals(1785666L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a43699", "sku485262", 365615L, 1316, 63);
        var b = new Product("b240370", "sku992943", 569737L, 868, 25);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b240370"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a43699"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other107795"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a43699"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a43699", 23));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a43699"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample44() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code291019", CouponType.FIXED, 34887L, 391897L, 272137L, null));
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code471542", CouponType.FIXED, 516906L, 379254L, 188563L, LocalDate.parse("2025-09-12"));
        assertThrows(shop.CouponNotApplicableException.class, () -> new PricingService().calculateDiscount(291097L, coupon, LocalDate.parse("2025-08-29")));
    }

    @Test
    void sample46() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.SUBURB, 20731, -997L));
    }

    @Test
    void sample47() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample48() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown562616"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message762460").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message762460").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message762460").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        var p = new Product("p548582", "sku203800", 127853L, 8494, 24);
        assertEquals("p548582", p.getId());
        assertEquals("sku203800", p.getName());
        assertEquals(127853L, p.getPrice());
        assertEquals(8494, p.getWeightGrams());
        p.reduceStock(4);
        assertEquals(20, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-1));
        assertEquals(20, p.getStock());
        p.restoreStock(37);
        assertEquals(57, p.getStock());
        p.reduceStock(27);
        assertEquals(30, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(40));
        assertEquals(30, p.getStock());
    }

    @Test
    void sample50() {
        var p = new Product("p962130", "sku993779", 452600L, 435, 29);
        var item = new CartItem(p, 22);
        assertEquals("p962130", item.getProduct().getId());
        assertEquals(22, item.getQuantity());
        assertEquals(9957200L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a11973", "sku859444", 306438L, 47, 22);
        var b = new Product("b849621", "sku952260", 264899L, 1208, 45);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b849621", 28));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b849621"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 48));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample52() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code975154", CouponType.PERCENT, 25L, 310243L, -18620L, LocalDate.parse("2024-03-19")));
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code654316", CouponType.FIXED, 486425L, 298632L, 132480L, LocalDate.parse("2024-09-21"));
        assertEquals(486425L, new PricingService().calculateDiscount(1133709L, coupon, LocalDate.parse("2024-08-17")));
    }

    @Test
    void sample54() {
        assertEquals(61000L, new ShippingCalculator().calculate(Region.SUBURB, 9963, 383751L));
    }

    @Test
    void sample55() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p658155", "sku892700", 117504L, 1378, 8);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order112985", null, null, Region.REMOTE, null));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(8, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf(" "));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown332155"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message884060").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message884060").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message884060").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p769372", "sku245704", 337674L, 636, 70);
        assertEquals("p769372", p.getId());
        assertEquals("sku245704", p.getName());
        assertEquals(337674L, p.getPrice());
        assertEquals(636, p.getWeightGrams());
        p.reduceStock(38);
        assertEquals(32, p.getStock());
        p.reduceStock(17);
        assertEquals(15, p.getStock());
        p.restoreStock(42);
        assertEquals(57, p.getStock());
    }

    @Test
    void sample58() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 42));
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a229184", "sku89247", 244509L, 1062, 43);
        var b = new Product("b11525", "sku571545", 703611L, 375, 45);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a229184", 31));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b11525", 52));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a229184", 20));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a229184", 7));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a229184", 46));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a229184"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample60() {
        var coupon = new Coupon("code563534", CouponType.PERCENT, 85L, 362614L, 33741L, LocalDate.parse("2027-11-10"));
        assertEquals("code563534", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(85L, coupon.getValue());
        assertEquals(362614L, coupon.getMinOrder());
        assertEquals(33741L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-11-10"), coupon.getExpiry());
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code963090", CouponType.PERCENT, 43L, 311583L, 191195L, LocalDate.parse("2026-01-21"));
        assertEquals(191195L, new PricingService().calculateDiscount(456258L, coupon, LocalDate.parse("2025-12-06")));
    }

    @Test
    void sample62() {
        assertEquals(75000L, new ShippingCalculator().calculate(Region.SUBURB, 13120, 27167L));
    }

    @Test
    void sample63() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
    }

    @Test
    void sample64() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException(" ").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(" ").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(" ").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(null, "sku821775", 355587L, 203, 71));
    }

    @Test
    void sample66() {
        var p = new Product("p151830", "sku395803", 368869L, 648, 12);
        var item = new CartItem(p, 9);
        assertEquals("p151830", item.getProduct().getId());
        assertEquals(9, item.getQuantity());
        assertEquals(3319821L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a915143", "sku385329", 50043L, 530, 2);
        var b = new Product("b415437", "sku866659", 660593L, 786, 34);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 3));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a915143", 7));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b415437"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b415437", 45));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b415437", 11));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        var coupon = new Coupon("code537380", CouponType.PERCENT, 97L, 154205L, 19253L, LocalDate.parse("2027-06-06"));
        assertEquals("code537380", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(97L, coupon.getValue());
        assertEquals(154205L, coupon.getMinOrder());
        assertEquals(19253L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-06-06"), coupon.getExpiry());
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code728799", CouponType.FIXED, 484847L, 232288L, 149414L, LocalDate.parse("2027-01-01"));
        assertEquals(484847L, new PricingService().calculateDiscount(1138118L, coupon, LocalDate.parse("2026-12-28")));
    }

    @Test
    void sample70() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.INNER_CITY, 22291, 646197L));
    }

    @Test
    void sample71() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p107530", "sku933503", 937019L, 84, 54);
        cart.addItem(p, 17);
        var coupon = new Coupon("code246286", CouponType.PERCENT, 56L, 362994L, 224449L, LocalDate.parse("2025-03-04"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(null, cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2025-02-10T06:11")));
        assertEquals(54, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-02-09T12:31")));
        assertEquals(54, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(54, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(54, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown289775"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.NullPointerException.class, () -> CouponType.valueOf(null));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown493299"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample73() {
        var p = new Product("p543574", "sku146576", 666272L, 13713, 6);
        assertEquals("p543574", p.getId());
        assertEquals("sku146576", p.getName());
        assertEquals(666272L, p.getPrice());
        assertEquals(13713, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(46));
        assertEquals(6, p.getStock());
        p.restoreStock(39);
        assertEquals(45, p.getStock());
        p.reduceStock(20);
        assertEquals(25, p.getStock());
    }

    @Test
    void sample74() {
        var p = new Product("p755689", "sku401704", 483675L, 672, 47);
        var item = new CartItem(p, 48);
        assertEquals("p755689", item.getProduct().getId());
        assertEquals(48, item.getQuantity());
        assertEquals(23216400L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a679914", "sku683540", 763279L, 1033, 36);
        var b = new Product("b347188", "sku832646", 574416L, 1067, 37);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a679914"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b347188"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a679914", 9));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 6);
        assertEquals(3446496L, cart.getSubtotal());
        assertEquals(6402, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b347188", cart.getItems().get(0).getProduct().getId());
        assertEquals(6, cart.getItems().get(0).getQuantity());
        assertEquals(3446496L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b347188");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        var coupon = new Coupon("code929452", CouponType.FIXED, 119341L, 55793L, 139851L, LocalDate.parse("2027-04-24"));
        assertEquals("code929452", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(119341L, coupon.getValue());
        assertEquals(55793L, coupon.getMinOrder());
        assertEquals(139851L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-04-24"), coupon.getExpiry());
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code310953", CouponType.FIXED, 187314L, 176171L, 242568L, LocalDate.parse("2025-10-25"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1216609L, coupon, LocalDate.parse("2025-11-27")));
    }

    @Test
    void sample78() {
        assertEquals(63000L, new ShippingCalculator().calculate(Region.PROVINCE, 7593, 884989L));
    }

    @Test
    void sample79() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p622566", "sku79950", 452903L, 574, 9);
        cart.addItem(p, 3);
        var order = service.placeOrder("order676665", cart, null, Region.PROVINCE, LocalDateTime.parse("2025-03-14T00:59"));
        assertEquals(6, p.getStock());
        assertEquals("order676665", order.getId());
        assertEquals(1358709L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(39000L, order.getShipping());
        assertEquals(1397709L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-03-14T00:59"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p622566", order.getItems().get(0).getProduct().getId());
        assertEquals(3, order.getItems().get(0).getQuantity());
        assertEquals(1358709L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2025-03-14T03:34")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(9, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(9, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown975064"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message328231").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message328231").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message328231").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p314305", "sku426266", 95541L, 18249, 23);
        assertEquals("p314305", p.getId());
        assertEquals("sku426266", p.getName());
        assertEquals(95541L, p.getPrice());
        assertEquals(18249, p.getWeightGrams());
        p.restoreStock(36);
        assertEquals(59, p.getStock());
        p.restoreStock(33);
        assertEquals(92, p.getStock());
        p.restoreStock(2);
        assertEquals(94, p.getStock());
    }

    @Test
    void sample82() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 48));
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a825131", "sku608180", 32808L, 146, 61);
        var b = new Product("b363957", "sku812785", 638064L, 941, 52);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 36));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 7);
        assertEquals(4466448L, cart.getSubtotal());
        assertEquals(6587, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b363957", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(4466448L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other753323"));
        assertEquals(4466448L, cart.getSubtotal());
        assertEquals(6587, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b363957", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(4466448L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("b363957", 2);
        assertEquals(1276128L, cart.getSubtotal());
        assertEquals(1882, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b363957", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(1276128L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a825131", 48));
        assertEquals(1276128L, cart.getSubtotal());
        assertEquals(1882, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b363957", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(1276128L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a825131"));
        assertEquals(1276128L, cart.getSubtotal());
        assertEquals(1882, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b363957", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(1276128L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b363957");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample84() {
        var coupon = new Coupon("code204348", CouponType.FIXED, 468264L, 136807L, 67948L, LocalDate.parse("2024-09-12"));
        assertEquals("code204348", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(468264L, coupon.getValue());
        assertEquals(136807L, coupon.getMinOrder());
        assertEquals(67948L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-09-12"), coupon.getExpiry());
    }

    @Test
    void sample85() {
        assertEquals(0L, new PricingService().calculateDiscount(997409L, null, LocalDate.parse("2025-03-07")));
    }

    @Test
    void sample86() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.INNER_CITY, 22606, 828316L));
    }

    @Test
    void sample87() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p415025", "sku291065", 133372L, 1338, 21);
        cart.addItem(p, 4);
        var coupon = new Coupon("code443633", CouponType.FIXED, 270961L, 87745L, 56874L, LocalDate.parse("2027-09-03"));
        var order = service.placeOrder("order410679", cart, coupon, Region.REMOTE, LocalDateTime.parse("2027-07-09T14:40"));
        assertEquals(17, p.getStock());
        assertEquals("order410679", order.getId());
        assertEquals(533488L, order.getSubtotal());
        assertEquals(270961L, order.getDiscount());
        assertEquals(73000L, order.getShipping());
        assertEquals(335527L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-07-09T14:40"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p415025", order.getItems().get(0).getProduct().getId());
        assertEquals(4, order.getItems().get(0).getQuantity());
        assertEquals(533488L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(17, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(17, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(17, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(17, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown155722"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown26573"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message908737").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message908737").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message908737").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        var p = new Product("p130616", "sku37828", 13786L, 18372, 66);
        assertEquals("p130616", p.getId());
        assertEquals("sku37828", p.getName());
        assertEquals(13786L, p.getPrice());
        assertEquals(18372, p.getWeightGrams());
        p.reduceStock(20);
        assertEquals(46, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-5));
        assertEquals(46, p.getStock());
        p.restoreStock(2);
        assertEquals(48, p.getStock());
    }

    @Test
    void sample90() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 15));
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a24831", "sku38919", 321077L, 65, 78);
        var b = new Product("b609684", "sku652339", 19637L, 51, 23);
        cart.addItem(b, 2);
        assertEquals(39274L, cart.getSubtotal());
        assertEquals(102, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b609684", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(39274L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 46));
        assertEquals(39274L, cart.getSubtotal());
        assertEquals(102, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b609684", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(39274L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b609684");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b609684"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 35));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample92() {
        var coupon = new Coupon("code486005", CouponType.FIXED, 109052L, 390798L, 236025L, LocalDate.parse("2025-01-11"));
        assertEquals("code486005", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(109052L, coupon.getValue());
        assertEquals(390798L, coupon.getMinOrder());
        assertEquals(236025L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-01-11"), coupon.getExpiry());
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code614720", CouponType.FIXED, 373360L, 492325L, 257268L, LocalDate.parse("2024-01-27"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(709326L, coupon, LocalDate.parse("2024-02-27")));
    }

    @Test
    void sample94() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.PROVINCE, 21849, 486849L));
    }

    @Test
    void sample95() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p295854", "sku966940", 604806L, 678, 66);
        cart.addItem(p, 5);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order810373", cart, null, Region.REMOTE, null));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-07-01T17:22")));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-07-02T03:42")));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(66, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message852126").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message852126").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message852126").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku584853", 860995L, 20178, 85));
    }

    @Test
    void sample98() {
        var p = new Product("p931052", "sku93631", 786877L, 1395, 48);
        var item = new CartItem(p, 10);
        assertEquals("p931052", item.getProduct().getId());
        assertEquals(10, item.getQuantity());
        assertEquals(7868770L, item.getLineTotal());
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a326733", "sku429865", 40208L, 1454, 41);
        var b = new Product("b980081", "sku762011", 364137L, 707, 27);
        cart.addItem(a, 17);
        assertEquals(683536L, cart.getSubtotal());
        assertEquals(24718, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a326733", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(683536L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b980081"));
        assertEquals(683536L, cart.getSubtotal());
        assertEquals(24718, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a326733", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(683536L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("other317944", -2));
        assertEquals(683536L, cart.getSubtotal());
        assertEquals(24718, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a326733", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(683536L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 5);
        assertEquals(2504221L, cart.getSubtotal());
        assertEquals(28253, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a326733", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(683536L, cart.getItems().get(0).getLineTotal());
        assertEquals("b980081", cart.getItems().get(1).getProduct().getId());
        assertEquals(5, cart.getItems().get(1).getQuantity());
        assertEquals(1820685L, cart.getItems().get(1).getLineTotal());
        cart.removeItem("b980081");
        assertEquals(683536L, cart.getSubtotal());
        assertEquals(24718, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a326733", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(683536L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b980081"));
        assertEquals(683536L, cart.getSubtotal());
        assertEquals(24718, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a326733", cart.getItems().get(0).getProduct().getId());
        assertEquals(17, cart.getItems().get(0).getQuantity());
        assertEquals(683536L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample100() {
        var coupon = new Coupon("code182927", CouponType.PERCENT, 82L, 399823L, 45304L, LocalDate.parse("2027-08-27"));
        assertEquals("code182927", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(82L, coupon.getValue());
        assertEquals(399823L, coupon.getMinOrder());
        assertEquals(45304L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-08-27"), coupon.getExpiry());
    }
}
