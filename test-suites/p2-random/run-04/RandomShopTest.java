package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p733133", "sku361663", 485931L, 17041, 55);
        assertEquals("p733133", p.getId());
        assertEquals("sku361663", p.getName());
        assertEquals(485931L, p.getPrice());
        assertEquals(17041, p.getWeightGrams());
        p.restoreStock(50);
        assertEquals(105, p.getStock());
        p.reduceStock(25);
        assertEquals(80, p.getStock());
        p.reduceStock(55);
        assertEquals(25, p.getStock());
        p.reduceStock(10);
        assertEquals(15, p.getStock());
        p.reduceStock(15);
        assertEquals(0, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p351353", "sku464562", 471705L, 888, 27);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -1));
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a425177", "sku551280", 173384L, 314, 82);
        var b = new Product("b440682", "sku463650", 190262L, 419, 4);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("a425177", -2));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -5));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 19);
        assertEquals(3294296L, cart.getSubtotal());
        assertEquals(5966, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a425177", cart.getItems().get(0).getProduct().getId());
        assertEquals(19, cart.getItems().get(0).getQuantity());
        assertEquals(3294296L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 26));
        assertEquals(3294296L, cart.getSubtotal());
        assertEquals(5966, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a425177", cart.getItems().get(0).getProduct().getId());
        assertEquals(19, cart.getItems().get(0).getQuantity());
        assertEquals(3294296L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample4() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code756593", null, 573186L, 429871L, 240405L, LocalDate.parse("2025-12-23")));
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code620678", CouponType.PERCENT, 93L, 530829L, 216955L, LocalDate.parse("2024-03-09"));
        assertEquals(216955L, new PricingService().calculateDiscount(647833L, coupon, LocalDate.parse("2024-01-23")));
    }

    @Test
    void sample6() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 12378, 539119L));
    }

    @Test
    void sample7() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p402420", "sku739463", 559765L, 570, 35);
        cart.addItem(p, 17);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order950017", cart, null, null, LocalDateTime.parse("2026-04-03T02:07")));
        assertEquals(35, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(35, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(35, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-04-05T00:18")));
        assertEquals(35, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-04-02T11:38")));
        assertEquals(35, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown867847"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown346035"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown416140"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message427930").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message427930").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message427930").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku304973", 667100L, 9220, 57));
    }

    @Test
    void sample10() {
        var p = new Product("p963338", "sku151601", 662064L, 15, 36);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -2));
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a203247", "sku35050", 3082L, 1172, 42);
        var b = new Product("b104762", "sku637990", 599256L, 1246, 42);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 29));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 17));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 45));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 23);
        assertEquals(70886L, cart.getSubtotal());
        assertEquals(26956, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a203247", cart.getItems().get(0).getProduct().getId());
        assertEquals(23, cart.getItems().get(0).getQuantity());
        assertEquals(70886L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 16);
        assertEquals(9658982L, cart.getSubtotal());
        assertEquals(46892, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a203247", cart.getItems().get(0).getProduct().getId());
        assertEquals(23, cart.getItems().get(0).getQuantity());
        assertEquals(70886L, cart.getItems().get(0).getLineTotal());
        assertEquals("b104762", cart.getItems().get(1).getProduct().getId());
        assertEquals(16, cart.getItems().get(1).getQuantity());
        assertEquals(9588096L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.PERCENT, 115L, 326964L, 258642L, LocalDate.parse("2027-08-01")));
    }

    @Test
    void sample13() {
        var coupon = new Coupon("code777653", CouponType.PERCENT, 89L, 86677L, 201989L, LocalDate.parse("2026-03-11"));
        assertEquals(107186L, new PricingService().calculateDiscount(120434L, coupon, LocalDate.parse("2026-01-23")));
    }

    @Test
    void sample14() {
        assertEquals(69000L, new ShippingCalculator().calculate(Region.REMOTE, 4157, 72271L));
    }

    @Test
    void sample15() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample16() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p719717", "sku292527", 900746L, 16051, 43);
        assertEquals("p719717", p.getId());
        assertEquals("sku292527", p.getName());
        assertEquals(900746L, p.getPrice());
        assertEquals(16051, p.getWeightGrams());
        p.restoreStock(5);
        assertEquals(48, p.getStock());
        p.reduceStock(4);
        assertEquals(44, p.getStock());
        p.restoreStock(48);
        assertEquals(92, p.getStock());
    }

    @Test
    void sample18() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 19));
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a249160", "sku720363", 67881L, 1461, 55);
        var b = new Product("b144852", "sku598115", 45455L, 227, 32);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other858091", 25));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other145516", 16));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 58));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a249160", 9));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b144852", 22));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b144852"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample20() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code532567", CouponType.PERCENT, 107L, 377468L, 77096L, LocalDate.parse("2024-04-20")));
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code770679", CouponType.PERCENT, 60L, 472520L, 1222L, LocalDate.parse("2027-11-16"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(321372L, coupon, LocalDate.parse("2027-12-04")));
    }

    @Test
    void sample22() {
        assertEquals(65000L, new ShippingCalculator().calculate(Region.PROVINCE, 8474, 77528L));
    }

    @Test
    void sample23() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p670826", "sku770687", 660668L, 936, 55);
        cart.addItem(p, 8);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(" ", cart, null, Region.INNER_CITY, LocalDateTime.parse("2027-11-01T20:35")));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-11-03T10:34")));
        assertEquals(55, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown338090"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown772017"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown907979"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message74284").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message74284").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message74284").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        var p = new Product("p121553", "sku905227", 468525L, 11092, 35);
        assertEquals("p121553", p.getId());
        assertEquals("sku905227", p.getName());
        assertEquals(468525L, p.getPrice());
        assertEquals(11092, p.getWeightGrams());
        p.restoreStock(48);
        assertEquals(83, p.getStock());
        p.reduceStock(58);
        assertEquals(25, p.getStock());
        p.reduceStock(19);
        assertEquals(6, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(31));
        assertEquals(6, p.getStock());
    }

    @Test
    void sample26() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 12));
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a198569", "sku256311", 293180L, 104, 28);
        var b = new Product("b406645", "sku251181", 535634L, 413, 12);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b406645"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 43));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a198569", 31));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a198569"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 40));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a198569", 23));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample28() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code570390", null, 323859L, 514745L, 184537L, LocalDate.parse("2026-01-03")));
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code404234", CouponType.PERCENT, 6L, 435553L, 6365L, LocalDate.parse("2024-04-06"));
        assertEquals(6365L, new PricingService().calculateDiscount(908193L, coupon, LocalDate.parse("2024-03-06")));
    }

    @Test
    void sample30() {
        assertEquals(55000L, new ShippingCalculator().calculate(Region.REMOTE, 604, 1122179L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p129417", "sku12570", 253809L, 830, 18);
        cart.addItem(p, 9);
        var coupon = new Coupon("code386830", CouponType.PERCENT, 35L, 488984L, 280773L, LocalDate.parse("2024-09-05"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order831138", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2024-10-20T03:48")));
        assertEquals(18, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(18, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-10-21T23:26")));
        assertEquals(18, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown275746"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message399618").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message399618").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message399618").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p552207", "sku195200", 670769L, 10940, 19);
        assertEquals("p552207", p.getId());
        assertEquals("sku195200", p.getName());
        assertEquals(670769L, p.getPrice());
        assertEquals(10940, p.getWeightGrams());
        p.restoreStock(29);
        assertEquals(48, p.getStock());
        p.restoreStock(53);
        assertEquals(101, p.getStock());
        p.restoreStock(39);
        assertEquals(140, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p878686", "sku58796", 656514L, 1508, 68);
        var item = new CartItem(p, 5);
        assertEquals("p878686", item.getProduct().getId());
        assertEquals(5, item.getQuantity());
        assertEquals(3282570L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a813818", "sku650028", 52795L, 613, 57);
        var b = new Product("b982481", "sku716786", 819627L, 601, 77);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a813818"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a813818", 41));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 29);
        assertEquals(1531055L, cart.getSubtotal());
        assertEquals(17777, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a813818", cart.getItems().get(0).getProduct().getId());
        assertEquals(29, cart.getItems().get(0).getQuantity());
        assertEquals(1531055L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 58));
        assertEquals(1531055L, cart.getSubtotal());
        assertEquals(17777, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a813818", cart.getItems().get(0).getProduct().getId());
        assertEquals(29, cart.getItems().get(0).getQuantity());
        assertEquals(1531055L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("a813818", 24);
        assertEquals(1267080L, cart.getSubtotal());
        assertEquals(14712, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a813818", cart.getItems().get(0).getProduct().getId());
        assertEquals(24, cart.getItems().get(0).getQuantity());
        assertEquals(1267080L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("a813818", 15);
        assertEquals(791925L, cart.getSubtotal());
        assertEquals(9195, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a813818", cart.getItems().get(0).getProduct().getId());
        assertEquals(15, cart.getItems().get(0).getQuantity());
        assertEquals(791925L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample36() {
        var coupon = new Coupon("code240807", CouponType.FIXED, 240734L, 492857L, 89996L, LocalDate.parse("2025-10-25"));
        assertEquals("code240807", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(240734L, coupon.getValue());
        assertEquals(492857L, coupon.getMinOrder());
        assertEquals(89996L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-10-25"), coupon.getExpiry());
    }

    @Test
    void sample37() {
        assertEquals(0L, new PricingService().calculateDiscount(858379L, null, LocalDate.parse("2024-03-04")));
    }

    @Test
    void sample38() {
        assertEquals(83000L, new ShippingCalculator().calculate(Region.PROVINCE, 12509, 1055853L));
    }

    @Test
    void sample39() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p794246", "sku178694", 646921L, 942, 55);
        cart.addItem(p, 5);
        var coupon = new Coupon("code14005", CouponType.PERCENT, 72L, 374504L, 206142L, LocalDate.parse("2027-10-31"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order421288", cart, coupon, null, LocalDateTime.parse("2027-09-26T20:37")));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-09-27T20:15")));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-09-28T19:14")));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(55, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown460282"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown686675"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown978758"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message318428").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message318428").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message318428").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p974176", "sku175562", 167472L, 12178, -4));
    }

    @Test
    void sample42() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 29));
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a570758", "sku350803", 664955L, 958, 12);
        var b = new Product("b832855", "sku29658", 452014L, 753, 36);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b832855"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a570758"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other356892"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b832855", 8));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b832855", 53));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b832855", 23));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b832855"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample44() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code326664", null, 531355L, 499218L, 280780L, LocalDate.parse("2027-07-30")));
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code799009", CouponType.FIXED, 63649L, 72213L, 296261L, LocalDate.parse("2025-09-20"));
        assertEquals(63649L, new PricingService().calculateDiscount(314513L, coupon, LocalDate.parse("2025-09-15")));
    }

    @Test
    void sample46() {
        assertEquals(99000L, new ShippingCalculator().calculate(Region.PROVINCE, 16815, 571942L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p416930", "sku306449", 637995L, 292, 67);
        cart.addItem(p, 18);
        var order = service.placeOrder("order414563", cart, null, Region.SUBURB, LocalDateTime.parse("2026-06-25T20:06"));
        assertEquals(49, p.getStock());
        assertEquals("order414563", order.getId());
        assertEquals(11483910L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(11483910L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-06-25T20:06"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p416930", order.getItems().get(0).getProduct().getId());
        assertEquals(18, order.getItems().get(0).getQuantity());
        assertEquals(11483910L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-06-25T04:59")));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(49, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(49, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(49, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(49, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown929852"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown396022"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message643347").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message643347").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message643347").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        var p = new Product("p837293", "sku263469", 768109L, 10425, 46);
        assertEquals("p837293", p.getId());
        assertEquals("sku263469", p.getName());
        assertEquals(768109L, p.getPrice());
        assertEquals(10425, p.getWeightGrams());
        p.reduceStock(2);
        assertEquals(44, p.getStock());
        p.restoreStock(32);
        assertEquals(76, p.getStock());
        p.reduceStock(57);
        assertEquals(19, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(25));
        assertEquals(19, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(55));
        assertEquals(19, p.getStock());
    }

    @Test
    void sample50() {
        var p = new Product("p381308", "sku619544", 471582L, 858, 64);
        var item = new CartItem(p, 7);
        assertEquals("p381308", item.getProduct().getId());
        assertEquals(7, item.getQuantity());
        assertEquals(3301074L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a665336", "sku417023", 512329L, 119, 59);
        var b = new Product("b520059", "sku803535", 808025L, 1283, 76);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b520059", 11));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b520059"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a665336"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 51));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a665336", 16));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b520059", 33));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample52() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.FIXED, 188296L, 265547L, 121328L, LocalDate.parse("2025-03-28")));
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code193159", CouponType.FIXED, 228774L, 565750L, 13480L, LocalDate.parse("2026-05-13"));
        assertEquals(228774L, new PricingService().calculateDiscount(1083973L, coupon, LocalDate.parse("2026-04-25")));
    }

    @Test
    void sample54() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 1398, 162118L));
    }

    @Test
    void sample55() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p369547", "sku396041", 311391L, 583, 70);
        cart.addItem(p, 2);
        var order = service.placeOrder("order967454", cart, null, Region.INNER_CITY, LocalDateTime.parse("2025-05-27T01:18"));
        assertEquals(68, p.getStock());
        assertEquals("order967454", order.getId());
        assertEquals(622782L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(622782L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-05-27T01:18"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p369547", order.getItems().get(0).getProduct().getId());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(622782L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(68, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(68, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.NullPointerException.class, () -> CouponType.valueOf(null));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown919426"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message346455").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message346455").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message346455").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p538674", "sku729679", 10671L, 15743, 20);
        assertEquals("p538674", p.getId());
        assertEquals("sku729679", p.getName());
        assertEquals(10671L, p.getPrice());
        assertEquals(15743, p.getWeightGrams());
        p.restoreStock(44);
        assertEquals(64, p.getStock());
        p.reduceStock(38);
        assertEquals(26, p.getStock());
    }

    @Test
    void sample58() {
        var p = new Product("p683725", "sku820951", 64921L, 1465, 61);
        var item = new CartItem(p, 18);
        assertEquals("p683725", item.getProduct().getId());
        assertEquals(18, item.getQuantity());
        assertEquals(1168578L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a145146", "sku500666", 241911L, 786, 32);
        var b = new Product("b622093", "sku249006", 155170L, 1331, 66);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b622093", 2));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b622093"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other371323", 53));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample60() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 283389L, 496064L, 272389L, LocalDate.parse("2025-04-12")));
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code557400", CouponType.PERCENT, 74L, 528381L, 52401L, LocalDate.parse("2027-07-05"));
        assertEquals(52401L, new PricingService().calculateDiscount(625879L, coupon, LocalDate.parse("2027-05-10")));
    }

    @Test
    void sample62() {
        assertEquals(35000L, new ShippingCalculator().calculate(Region.SUBURB, 3190, 285229L));
    }

    @Test
    void sample63() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p912486", "sku596843", 400446L, 267, 32);
        cart.addItem(p, 12);
        var order = service.placeOrder("order705213", cart, null, Region.REMOTE, LocalDateTime.parse("2025-02-18T17:37"));
        assertEquals(20, p.getStock());
        assertEquals("order705213", order.getId());
        assertEquals(4805352L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(65000L, order.getShipping());
        assertEquals(4870352L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-02-18T17:37"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p912486", order.getItems().get(0).getProduct().getId());
        assertEquals(12, order.getItems().get(0).getQuantity());
        assertEquals(4805352L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(20, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(20, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(20, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(20, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown83810"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown566621"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample65() {
        var p = new Product("p248083", "sku81622", 810894L, 6478, 17);
        assertEquals("p248083", p.getId());
        assertEquals("sku81622", p.getName());
        assertEquals(810894L, p.getPrice());
        assertEquals(6478, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-5));
        assertEquals(17, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(36));
        assertEquals(17, p.getStock());
    }

    @Test
    void sample66() {
        var p = new Product("p93661", "sku529905", 458021L, 1585, 23);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -2));
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a707402", "sku809239", 752241L, 1465, 44);
        var b = new Product("b860389", "sku321679", 314180L, 1508, 4);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a707402", 24));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 34);
        assertEquals(25576194L, cart.getSubtotal());
        assertEquals(49810, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a707402", cart.getItems().get(0).getProduct().getId());
        assertEquals(34, cart.getItems().get(0).getQuantity());
        assertEquals(25576194L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b860389", -3));
        assertEquals(25576194L, cart.getSubtotal());
        assertEquals(49810, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a707402", cart.getItems().get(0).getProduct().getId());
        assertEquals(34, cart.getItems().get(0).getQuantity());
        assertEquals(25576194L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("a707402");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 56));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 16));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code999211", CouponType.PERCENT, 101L, 39451L, 65159L, LocalDate.parse("2027-08-11")));
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code648793", CouponType.FIXED, 225823L, 143596L, 60069L, LocalDate.parse("2025-09-22"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-19787L, coupon, LocalDate.parse("2025-10-02")));
    }

    @Test
    void sample70() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 9122, 750926L));
    }

    @Test
    void sample71() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p963397", "sku602687", 247279L, 1408, 4);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 14));
        var coupon = new Coupon("code904534", CouponType.PERCENT, 2L, 495524L, 190952L, LocalDate.parse("2027-12-20"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order50292", cart, coupon, Region.SUBURB, LocalDateTime.parse("2027-12-27T11:45")));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(4, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown943552"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown728940"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown349736"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message81243").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message81243").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message81243").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku614892", 266388L, 6296, 75));
    }

    @Test
    void sample74() {
        var p = new Product("p367503", "sku926957", 639265L, 1400, 17);
        var item = new CartItem(p, 56);
        assertEquals("p367503", item.getProduct().getId());
        assertEquals(56, item.getQuantity());
        assertEquals(35798840L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a450374", "sku668662", 881281L, 1364, 52);
        var b = new Product("b802252", "sku93208", 453595L, 1342, 47);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other944504", 33));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 55));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b802252"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code455189", CouponType.PERCENT, 121L, 236305L, 92582L, LocalDate.parse("2024-09-04")));
    }

    @Test
    void sample77() {
        assertEquals(0L, new PricingService().calculateDiscount(1090142L, null, LocalDate.parse("2026-08-08")));
    }

    @Test
    void sample78() {
        assertEquals(51000L, new ShippingCalculator().calculate(Region.SUBURB, 7109, 112389L));
    }

    @Test
    void sample79() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p849825", "sku670034", 682153L, 957, 25);
        cart.addItem(p, 17);
        var order = service.placeOrder("order140163", cart, null, Region.REMOTE, LocalDateTime.parse("2025-07-31T22:57"));
        assertEquals(8, p.getStock());
        assertEquals("order140163", order.getId());
        assertEquals(11596601L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(117000L, order.getShipping());
        assertEquals(11713601L, order.getTotal());
        assertEquals(LocalDateTime.parse("2025-07-31T22:57"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p849825", order.getItems().get(0).getProduct().getId());
        assertEquals(17, order.getItems().get(0).getQuantity());
        assertEquals(11596601L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(8, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(8, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown347971"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown41412"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown527868"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message202067").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message202067").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message202067").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p725940", "sku770852", 16995L, 11772, 74);
        assertEquals("p725940", p.getId());
        assertEquals("sku770852", p.getName());
        assertEquals(16995L, p.getPrice());
        assertEquals(11772, p.getWeightGrams());
        p.reduceStock(57);
        assertEquals(17, p.getStock());
        p.restoreStock(23);
        assertEquals(40, p.getStock());
    }

    @Test
    void sample82() {
        var p = new Product("p379402", "sku565601", 784150L, 978, 20);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -2));
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a415729", "sku199844", 840975L, 774, 8);
        var b = new Product("b128306", "sku885274", 502503L, 324, 6);
        cart.addItem(a, 8);
        assertEquals(6727800L, cart.getSubtotal());
        assertEquals(6192, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a415729", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(6727800L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.updateQuantity("a415729", 16));
        assertEquals(6727800L, cart.getSubtotal());
        assertEquals(6192, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a415729", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(6727800L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("a415729", 8);
        assertEquals(6727800L, cart.getSubtotal());
        assertEquals(6192, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a415729", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(6727800L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b128306"));
        assertEquals(6727800L, cart.getSubtotal());
        assertEquals(6192, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a415729", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(6727800L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 53));
        assertEquals(6727800L, cart.getSubtotal());
        assertEquals(6192, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a415729", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(6727800L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample84() {
        var coupon = new Coupon("code449872", CouponType.PERCENT, 41L, 420192L, 111558L, LocalDate.parse("2024-12-20"));
        assertEquals("code449872", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(41L, coupon.getValue());
        assertEquals(420192L, coupon.getMinOrder());
        assertEquals(111558L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-12-20"), coupon.getExpiry());
    }

    @Test
    void sample85() {
        var coupon = new Coupon("code621139", CouponType.PERCENT, 92L, 413083L, 280763L, LocalDate.parse("2024-01-13"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-34535L, coupon, LocalDate.parse("2024-02-21")));
    }

    @Test
    void sample86() {
        assertEquals(41000L, new ShippingCalculator().calculate(Region.INNER_CITY, 7283, 69355L));
    }

    @Test
    void sample87() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p630858", "sku42142", 503724L, 1279, 47);
        cart.addItem(p, 2);
        var coupon = new Coupon("code137340", CouponType.PERCENT, 4L, 4482L, 115539L, LocalDate.parse("2027-01-06"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order129828", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2027-02-12T02:37")));
        assertEquals(47, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-02-15T09:20")));
        assertEquals(47, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-02-14T15:37")));
        assertEquals(47, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown636532"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertEquals("InsufficientStockException", new InsufficientStockException("message720124").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message720124").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message720124").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        var p = new Product("p487114", "sku444657", 978110L, 20055, 78);
        assertEquals("p487114", p.getId());
        assertEquals("sku444657", p.getName());
        assertEquals(978110L, p.getPrice());
        assertEquals(20055, p.getWeightGrams());
        p.reduceStock(24);
        assertEquals(54, p.getStock());
        p.restoreStock(19);
        assertEquals(73, p.getStock());
        p.reduceStock(25);
        assertEquals(48, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-1));
        assertEquals(48, p.getStock());
        p.reduceStock(47);
        assertEquals(1, p.getStock());
    }

    @Test
    void sample90() {
        var p = new Product("p381616", "sku71863", 95760L, 787, 85);
        var item = new CartItem(p, 53);
        assertEquals("p381616", item.getProduct().getId());
        assertEquals(53, item.getQuantity());
        assertEquals(5075280L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a843818", "sku313159", 797928L, 1338, 78);
        var b = new Product("b262369", "sku19374", 391230L, 1102, 47);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b262369"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b262369", 50));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b262369", -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a843818"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b262369"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample92() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code705184", CouponType.PERCENT, 82L, -20435L, 89854L, LocalDate.parse("2024-11-04")));
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code750616", CouponType.FIXED, 99068L, 589131L, 39021L, LocalDate.parse("2027-01-30"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(928241L, coupon, LocalDate.parse("2027-03-23")));
    }

    @Test
    void sample94() {
        assertEquals(27000L, new ShippingCalculator().calculate(Region.SUBURB, 1021, 489574L));
    }

    @Test
    void sample95() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p967901", "sku627429", 986397L, 539, 41);
        cart.addItem(p, 18);
        var coupon = new Coupon("code535644", CouponType.FIXED, 462661L, 5214L, 152511L, LocalDate.parse("2025-01-12"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order305452", cart, coupon, Region.REMOTE, null));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-01-23T16:04")));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(41, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-01-23T05:01")));
        assertEquals(41, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown354368"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown869185"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message207522").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message207522").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message207522").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p657498", null, 740003L, 9093, 74));
    }

    @Test
    void sample98() {
        var p = new Product("p959567", "sku832953", 995105L, 841, 33);
        var item = new CartItem(p, 56);
        assertEquals("p959567", item.getProduct().getId());
        assertEquals(56, item.getQuantity());
        assertEquals(55725880L, item.getLineTotal());
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a841276", "sku739489", 816335L, 1285, 72);
        var b = new Product("b62749", "sku63631", 274338L, 82, 63);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a841276", 26));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 32);
        assertEquals(8778816L, cart.getSubtotal());
        assertEquals(2624, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b62749", cart.getItems().get(0).getProduct().getId());
        assertEquals(32, cart.getItems().get(0).getQuantity());
        assertEquals(8778816L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b62749");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b62749", 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other733230", 45));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample100() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code640863", CouponType.PERCENT, -4L, -19912L, -23400L, LocalDate.parse("2024-10-31")));
    }
}
