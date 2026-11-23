package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p990395", "sku608674", 214636L, 394, 71);
        assertEquals("p990395", p.getId());
        assertEquals("sku608674", p.getName());
        assertEquals(214636L, p.getPrice());
        assertEquals(394, p.getWeightGrams());
        p.restoreStock(31);
        assertEquals(102, p.getStock());
        p.restoreStock(27);
        assertEquals(129, p.getStock());
        p.reduceStock(15);
        assertEquals(114, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p394109", "sku115406", 282264L, 1569, 78);
        var item = new CartItem(p, 48);
        assertEquals("p394109", item.getProduct().getId());
        assertEquals(48, item.getQuantity());
        assertEquals(13548672L, item.getLineTotal());
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a445750", "sku609157", 539203L, 1272, 41);
        var b = new Product("b194505", "sku897417", 150042L, 1499, 61);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b194505", 7));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a445750", 48));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a445750", 33));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample4() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code714238", CouponType.PERCENT, 120L, 162278L, 248502L, LocalDate.parse("2027-09-05")));
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code609539", CouponType.FIXED, 523598L, 284930L, 77687L, LocalDate.parse("2024-07-11"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(714658L, coupon, LocalDate.parse("2024-07-27")));
    }

    @Test
    void sample6() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.INNER_CITY, 20219, -22120L));
    }

    @Test
    void sample7() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p64787", "sku293673", 460318L, 1090, 7);
        cart.addItem(p, 6);
        var order = service.placeOrder("order715673", cart, null, Region.INNER_CITY, LocalDateTime.parse("2027-02-21T22:59"));
        assertEquals(1, p.getStock());
        assertEquals("order715673", order.getId());
        assertEquals(2761908L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(2761908L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-02-21T22:59"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p64787", order.getItems().get(0).getProduct().getId());
        assertEquals(6, order.getItems().get(0).getQuantity());
        assertEquals(2761908L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(1, p.getStock());
        assertEquals(2209526L, service.cancelOrder(order, LocalDateTime.parse("2027-02-23T15:35")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(7, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(7, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2027-02-23T07:29")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(7, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown199457"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample9() {
        var p = new Product("p136555", "sku709403", 189945L, 23993, 48);
        assertEquals("p136555", p.getId());
        assertEquals("sku709403", p.getName());
        assertEquals(189945L, p.getPrice());
        assertEquals(23993, p.getWeightGrams());
        p.restoreStock(39);
        assertEquals(87, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(0));
        assertEquals(87, p.getStock());
    }

    @Test
    void sample10() {
        var p = new Product("p903992", "sku990624", 805842L, 520, 80);
        var item = new CartItem(p, 10);
        assertEquals("p903992", item.getProduct().getId());
        assertEquals(10, item.getQuantity());
        assertEquals(8058420L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a854209", "sku852822", 542923L, 1517, 76);
        var b = new Product("b428692", "sku391495", 800787L, 145, 1);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 36));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b428692", 31));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b428692", 27));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a854209", 28));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code481907", null, 502136L, 517254L, 296649L, LocalDate.parse("2024-01-02")));
    }

    @Test
    void sample13() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(746261L, null, null));
    }

    @Test
    void sample14() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 12611, 601816L));
    }

    @Test
    void sample15() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p288861", "sku270918", 315047L, 1317, 74);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order924979", null, null, Region.SUBURB, null));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(74, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(74, p.getStock());
    }

    @Test
    void sample16() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown611937"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message23636").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message23636").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message23636").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p515050", "sku312433", 204089L, 23049, 72);
        assertEquals("p515050", p.getId());
        assertEquals("sku312433", p.getName());
        assertEquals(204089L, p.getPrice());
        assertEquals(23049, p.getWeightGrams());
        p.reduceStock(30);
        assertEquals(42, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(0));
        assertEquals(42, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p478212", "sku515417", 865933L, 920, 60);
        var item = new CartItem(p, 40);
        assertEquals("p478212", item.getProduct().getId());
        assertEquals(40, item.getQuantity());
        assertEquals(34637320L, item.getLineTotal());
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a776367", "sku500805", 407820L, 367, 84);
        var b = new Product("b335905", "sku312749", 79612L, 1155, 36);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a776367"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b335905"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 16);
        assertEquals(1273792L, cart.getSubtotal());
        assertEquals(18480, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b335905", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(1273792L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 36));
        assertEquals(1273792L, cart.getSubtotal());
        assertEquals(18480, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b335905", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(1273792L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 48));
        assertEquals(1273792L, cart.getSubtotal());
        assertEquals(18480, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b335905", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(1273792L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("b335905", 33);
        assertEquals(2627196L, cart.getSubtotal());
        assertEquals(38115, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b335905", cart.getItems().get(0).getProduct().getId());
        assertEquals(33, cart.getItems().get(0).getQuantity());
        assertEquals(2627196L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 24));
        assertEquals(2627196L, cart.getSubtotal());
        assertEquals(38115, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b335905", cart.getItems().get(0).getProduct().getId());
        assertEquals(33, cart.getItems().get(0).getQuantity());
        assertEquals(2627196L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample20() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code878241", CouponType.PERCENT, 14L, 294167L, -12347L, LocalDate.parse("2024-09-04")));
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code455920", CouponType.PERCENT, 65L, 89444L, 149245L, LocalDate.parse("2027-09-29"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(1225477L, coupon, null));
    }

    @Test
    void sample22() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 24546, 116466L));
    }

    @Test
    void sample23() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p289941", "sku775516", 561954L, 672, 44);
        cart.addItem(p, 14);
        var order = service.placeOrder("order256964", cart, null, Region.SUBURB, LocalDateTime.parse("2027-06-16T05:26"));
        assertEquals(30, p.getStock());
        assertEquals("order256964", order.getId());
        assertEquals(7867356L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(7867356L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-06-16T05:26"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p289941", order.getItems().get(0).getProduct().getId());
        assertEquals(14, order.getItems().get(0).getQuantity());
        assertEquals(7867356L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2027-06-15T17:45")));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(30, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2027-06-19T22:29")));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(30, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message711438").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message711438").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message711438").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        var p = new Product("p503697", "sku833553", 40389L, 17248, 71);
        assertEquals("p503697", p.getId());
        assertEquals("sku833553", p.getName());
        assertEquals(40389L, p.getPrice());
        assertEquals(17248, p.getWeightGrams());
        p.restoreStock(2);
        assertEquals(73, p.getStock());
        p.reduceStock(48);
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-2));
        assertEquals(25, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(35));
        assertEquals(25, p.getStock());
        p.restoreStock(36);
        assertEquals(61, p.getStock());
    }

    @Test
    void sample26() {
        var p = new Product("p10323", "sku112150", 325116L, 393, 63);
        var item = new CartItem(p, 59);
        assertEquals("p10323", item.getProduct().getId());
        assertEquals(59, item.getQuantity());
        assertEquals(19181844L, item.getLineTotal());
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a842078", "sku118299", 633986L, 361, 53);
        var b = new Product("b103625", "sku144407", 925917L, 1070, 66);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a842078"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a842078", 4));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 59));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 33);
        assertEquals(20921538L, cart.getSubtotal());
        assertEquals(11913, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a842078", cart.getItems().get(0).getProduct().getId());
        assertEquals(33, cart.getItems().get(0).getQuantity());
        assertEquals(20921538L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample28() {
        var coupon = new Coupon("code707587", CouponType.FIXED, 103662L, 385857L, 56938L, LocalDate.parse("2026-09-27"));
        assertEquals("code707587", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(103662L, coupon.getValue());
        assertEquals(385857L, coupon.getMinOrder());
        assertEquals(56938L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-09-27"), coupon.getExpiry());
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code599282", CouponType.PERCENT, 17L, 470507L, 135670L, LocalDate.parse("2025-11-20"));
        assertEquals(103723L, new PricingService().calculateDiscount(610136L, coupon, LocalDate.parse("2025-09-26")));
    }

    @Test
    void sample30() {
        assertEquals(27000L, new ShippingCalculator().calculate(Region.INNER_CITY, 3794, 402855L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p819654", "sku850213", 599402L, 846, 52);
        cart.addItem(p, 7);
        var order = service.placeOrder("order404099", cart, null, Region.SUBURB, LocalDateTime.parse("2026-02-21T02:07"));
        assertEquals(45, p.getStock());
        assertEquals("order404099", order.getId());
        assertEquals(4195814L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(4195814L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-02-21T02:07"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p819654", order.getItems().get(0).getProduct().getId());
        assertEquals(7, order.getItems().get(0).getQuantity());
        assertEquals(4195814L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-02-20T04:22")));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(45, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(45, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown277811"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown715519"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message531562").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message531562").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message531562").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p520913", "sku511681", 59363L, 13657, 38);
        assertEquals("p520913", p.getId());
        assertEquals("sku511681", p.getName());
        assertEquals(59363L, p.getPrice());
        assertEquals(13657, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(40));
        assertEquals(38, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-3));
        assertEquals(38, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p700324", "sku496305", 848056L, 1487, 6);
        var item = new CartItem(p, 50);
        assertEquals("p700324", item.getProduct().getId());
        assertEquals(50, item.getQuantity());
        assertEquals(42402800L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a143498", "sku202486", 358692L, 157, 54);
        var b = new Product("b287486", "sku334399", 984901L, 845, 62);
        cart.addItem(a, 3);
        assertEquals(1076076L, cart.getSubtotal());
        assertEquals(471, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a143498", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1076076L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b287486"));
        assertEquals(1076076L, cart.getSubtotal());
        assertEquals(471, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a143498", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1076076L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b287486", 54));
        assertEquals(1076076L, cart.getSubtotal());
        assertEquals(471, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a143498", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1076076L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b287486", 34));
        assertEquals(1076076L, cart.getSubtotal());
        assertEquals(471, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a143498", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1076076L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem(" "));
        assertEquals(1076076L, cart.getSubtotal());
        assertEquals(471, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a143498", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(1076076L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample36() {
        var coupon = new Coupon("code943213", CouponType.FIXED, 347076L, 278830L, 70436L, LocalDate.parse("2025-02-08"));
        assertEquals("code943213", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(347076L, coupon.getValue());
        assertEquals(278830L, coupon.getMinOrder());
        assertEquals(70436L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-02-08"), coupon.getExpiry());
    }

    @Test
    void sample37() {
        assertEquals(0L, new PricingService().calculateDiscount(243284L, null, LocalDate.parse("2026-05-02")));
    }

    @Test
    void sample38() {
        assertEquals(77000L, new ShippingCalculator().calculate(Region.INNER_CITY, 16038, 25399L));
    }

    @Test
    void sample39() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p556155", "sku900971", 89230L, 409, 12);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 13));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order834310", cart, null, Region.SUBURB, LocalDateTime.parse("2027-12-25T15:51")));
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-12-27T11:15")));
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(12, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown807085"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown191053"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message301136").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message301136").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message301136").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku307407", 305489L, 10279, 78));
    }

    @Test
    void sample42() {
        var p = new Product("p709240", "sku627737", 882997L, 240, 74);
        var item = new CartItem(p, 8);
        assertEquals("p709240", item.getProduct().getId());
        assertEquals(8, item.getQuantity());
        assertEquals(7063976L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a473853", "sku767456", 743864L, 178, 76);
        var b = new Product("b843093", "sku764665", 374914L, 471, 34);
        cart.addItem(b, 20);
        assertEquals(7498280L, cart.getSubtotal());
        assertEquals(9420, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b843093", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(7498280L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 4);
        assertEquals(10473736L, cart.getSubtotal());
        assertEquals(10132, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b843093", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(7498280L, cart.getItems().get(0).getLineTotal());
        assertEquals("a473853", cart.getItems().get(1).getProduct().getId());
        assertEquals(4, cart.getItems().get(1).getQuantity());
        assertEquals(2975456L, cart.getItems().get(1).getLineTotal());
        cart.removeItem("a473853");
        assertEquals(7498280L, cart.getSubtotal());
        assertEquals(9420, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b843093", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(7498280L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b843093", 51));
        assertEquals(7498280L, cart.getSubtotal());
        assertEquals(9420, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b843093", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(7498280L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a473853"));
        assertEquals(7498280L, cart.getSubtotal());
        assertEquals(9420, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b843093", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(7498280L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 45);
        assertEquals(40972160L, cart.getSubtotal());
        assertEquals(17430, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b843093", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(7498280L, cart.getItems().get(0).getLineTotal());
        assertEquals("a473853", cart.getItems().get(1).getProduct().getId());
        assertEquals(45, cart.getItems().get(1).getQuantity());
        assertEquals(33473880L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample44() {
        var coupon = new Coupon("code386416", CouponType.PERCENT, 11L, 347255L, 284875L, LocalDate.parse("2026-08-24"));
        assertEquals("code386416", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(11L, coupon.getValue());
        assertEquals(347255L, coupon.getMinOrder());
        assertEquals(284875L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-08-24"), coupon.getExpiry());
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code561659", CouponType.PERCENT, 78L, 36006L, 295029L, LocalDate.parse("2024-11-28"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(618144L, coupon, LocalDate.parse("2024-12-23")));
    }

    @Test
    void sample46() {
        assertEquals(77000L, new ShippingCalculator().calculate(Region.REMOTE, 6452, 123223L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p658695", "sku906553", 843158L, 1267, 82);
        cart.addItem(p, 5);
        var order = service.placeOrder("order162718", cart, null, Region.INNER_CITY, LocalDateTime.parse("2027-10-16T11:37"));
        assertEquals(77, p.getStock());
        assertEquals("order162718", order.getId());
        assertEquals(4215790L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(4215790L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-10-16T11:37"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p658695", order.getItems().get(0).getProduct().getId());
        assertEquals(5, order.getItems().get(0).getQuantity());
        assertEquals(4215790L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(77, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(77, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown582007"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown617494"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown237234"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message744750").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message744750").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message744750").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        var p = new Product("p511070", "sku682523", 726412L, 5756, 41);
        assertEquals("p511070", p.getId());
        assertEquals("sku682523", p.getName());
        assertEquals(726412L, p.getPrice());
        assertEquals(5756, p.getWeightGrams());
        p.reduceStock(6);
        assertEquals(35, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(48));
        assertEquals(35, p.getStock());
    }

    @Test
    void sample50() {
        var p = new Product("p713455", "sku492401", 754887L, 894, 71);
        var item = new CartItem(p, 38);
        assertEquals("p713455", item.getProduct().getId());
        assertEquals(38, item.getQuantity());
        assertEquals(28685706L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a526605", "sku863750", 942996L, 118, 6);
        var b = new Product("b960037", "sku85049", 751814L, 143, 61);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a526605"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 11);
        assertEquals(8269954L, cart.getSubtotal());
        assertEquals(1573, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b960037", cart.getItems().get(0).getProduct().getId());
        assertEquals(11, cart.getItems().get(0).getQuantity());
        assertEquals(8269954L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a526605"));
        assertEquals(8269954L, cart.getSubtotal());
        assertEquals(1573, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b960037", cart.getItems().get(0).getProduct().getId());
        assertEquals(11, cart.getItems().get(0).getQuantity());
        assertEquals(8269954L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 11));
        assertEquals(8269954L, cart.getSubtotal());
        assertEquals(1573, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b960037", cart.getItems().get(0).getProduct().getId());
        assertEquals(11, cart.getItems().get(0).getQuantity());
        assertEquals(8269954L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 22));
        assertEquals(8269954L, cart.getSubtotal());
        assertEquals(1573, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b960037", cart.getItems().get(0).getProduct().getId());
        assertEquals(11, cart.getItems().get(0).getQuantity());
        assertEquals(8269954L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample52() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code575572", CouponType.FIXED, 209348L, -37074L, 130022L, LocalDate.parse("2025-12-08")));
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code893308", CouponType.PERCENT, 18L, 443927L, 288833L, LocalDate.parse("2027-06-25"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1190577L, coupon, LocalDate.parse("2027-08-02")));
    }

    @Test
    void sample54() {
        assertEquals(61000L, new ShippingCalculator().calculate(Region.REMOTE, 2241, 1031848L));
    }

    @Test
    void sample55() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p540068", "sku687026", 235430L, 283, 5);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(p, 7));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order534138", cart, null, Region.INNER_CITY, LocalDateTime.parse("2024-03-15T04:11")));
        assertEquals(5, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(5, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(5, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(5, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(5, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-03-17T12:43")));
        assertEquals(5, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown612284"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message849037").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message849037").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message849037").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p727407", "sku964546", 397448L, 6645, 9);
        assertEquals("p727407", p.getId());
        assertEquals("sku964546", p.getName());
        assertEquals(397448L, p.getPrice());
        assertEquals(6645, p.getWeightGrams());
        p.restoreStock(24);
        assertEquals(33, p.getStock());
        p.reduceStock(21);
        assertEquals(12, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(32));
        assertEquals(12, p.getStock());
        p.restoreStock(14);
        assertEquals(26, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(45));
        assertEquals(26, p.getStock());
    }

    @Test
    void sample58() {
        var p = new Product("p683817", "sku157617", 399114L, 579, 39);
        var item = new CartItem(p, 56);
        assertEquals("p683817", item.getProduct().getId());
        assertEquals(56, item.getQuantity());
        assertEquals(22350384L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a178565", "sku432211", 591414L, 712, 72);
        var b = new Product("b251589", "sku812121", 910879L, 1191, 74);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a178565"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("a178565", -5));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other985607"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b251589"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 13);
        assertEquals(11841427L, cart.getSubtotal());
        assertEquals(15483, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b251589", cart.getItems().get(0).getProduct().getId());
        assertEquals(13, cart.getItems().get(0).getQuantity());
        assertEquals(11841427L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample60() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code680957", CouponType.FIXED, 305109L, 9380L, 57101L, null));
    }

    @Test
    void sample61() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(1089437L, null, null));
    }

    @Test
    void sample62() {
        assertEquals(119000L, new ShippingCalculator().calculate(Region.REMOTE, 16948, 280360L));
    }

    @Test
    void sample63() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p525533", "sku945890", 835068L, 567, 44);
        cart.addItem(p, 16);
        var coupon = new Coupon("code463516", CouponType.PERCENT, 26L, 581137L, 290573L, LocalDate.parse("2026-10-04"));
        var order = service.placeOrder("order601630", cart, coupon, Region.REMOTE, LocalDateTime.parse("2026-09-26T13:21"));
        assertEquals(28, p.getStock());
        assertEquals("order601630", order.getId());
        assertEquals(13361088L, order.getSubtotal());
        assertEquals(290573L, order.getDiscount());
        assertEquals(89000L, order.getShipping());
        assertEquals(13159515L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-09-26T13:21"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p525533", order.getItems().get(0).getProduct().getId());
        assertEquals(16, order.getItems().get(0).getQuantity());
        assertEquals(13361088L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2026-09-29T16:07")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(44, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-09-28T03:52")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(44, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-09-28T16:25")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(44, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-09-29T07:20")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(44, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown156539"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message55120").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message55120").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message55120").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        var p = new Product("p61580", "sku745360", 927705L, 2724, 41);
        assertEquals("p61580", p.getId());
        assertEquals("sku745360", p.getName());
        assertEquals(927705L, p.getPrice());
        assertEquals(2724, p.getWeightGrams());
        p.reduceStock(11);
        assertEquals(30, p.getStock());
        p.reduceStock(25);
        assertEquals(5, p.getStock());
        p.restoreStock(56);
        assertEquals(61, p.getStock());
        p.restoreStock(48);
        assertEquals(109, p.getStock());
    }

    @Test
    void sample66() {
        var p = new Product("p568557", "sku971974", 438571L, 1363, 13);
        var item = new CartItem(p, 55);
        assertEquals("p568557", item.getProduct().getId());
        assertEquals(55, item.getQuantity());
        assertEquals(24121405L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a133759", "sku608945", 891127L, 1285, 6);
        var b = new Product("b53903", "sku965712", 580393L, 1210, 73);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a133759"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b53903"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a133759", 15));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 53));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b53903"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b53903", -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b53903"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code698872", CouponType.PERCENT, 13L, 327143L, 21516L, null));
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code357691", CouponType.PERCENT, 78L, 558495L, 206829L, LocalDate.parse("2024-07-07"));
        assertEquals(206829L, new PricingService().calculateDiscount(825939L, coupon, LocalDate.parse("2024-06-29")));
    }

    @Test
    void sample70() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 3223, 970778L));
    }

    @Test
    void sample71() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p941606", "sku373282", 64619L, 122, 63);
        cart.addItem(p, 5);
        var coupon = new Coupon("code445868", CouponType.PERCENT, 50L, 152413L, 138913L, LocalDate.parse("2026-04-15"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(null, cart, coupon, Region.SUBURB, LocalDateTime.parse("2026-03-07T22:10")));
        assertEquals(63, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(63, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(63, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown831379"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message526865").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message526865").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message526865").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        var p = new Product("p921713", "sku642846", 621332L, 9718, 24);
        assertEquals("p921713", p.getId());
        assertEquals("sku642846", p.getName());
        assertEquals(621332L, p.getPrice());
        assertEquals(9718, p.getWeightGrams());
        p.reduceStock(11);
        assertEquals(13, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(58));
        assertEquals(13, p.getStock());
        p.restoreStock(39);
        assertEquals(52, p.getStock());
        p.restoreStock(2);
        assertEquals(54, p.getStock());
    }

    @Test
    void sample74() {
        var p = new Product("p724228", "sku716663", 25498L, 895, 63);
        var item = new CartItem(p, 42);
        assertEquals("p724228", item.getProduct().getId());
        assertEquals(42, item.getQuantity());
        assertEquals(1070916L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a675660", "sku508557", 908147L, 817, 59);
        var b = new Product("b165246", "sku213087", 328165L, 71, 9);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a675660"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b165246"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 39));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a675660"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b165246", 32));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        var coupon = new Coupon("code348993", CouponType.FIXED, 151640L, 463326L, 95971L, LocalDate.parse("2026-05-04"));
        assertEquals("code348993", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(151640L, coupon.getValue());
        assertEquals(463326L, coupon.getMinOrder());
        assertEquals(95971L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-05-04"), coupon.getExpiry());
    }

    @Test
    void sample77() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(716494L, null, null));
    }

    @Test
    void sample78() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 22145, 739229L));
    }

    @Test
    void sample79() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p281054", "sku979208", 14652L, 182, 84);
        cart.addItem(p, 14);
        var coupon = new Coupon("code984889", CouponType.FIXED, 149007L, 330055L, 167675L, LocalDate.parse("2027-12-26"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order148428", cart, coupon, Region.SUBURB, LocalDateTime.parse("2028-01-07T02:15")));
        assertEquals(84, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(84, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2028-01-09T16:22")));
        assertEquals(84, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown118632"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown533082"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message806720").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message806720").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message806720").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p736722", "sku867862", 745491L, 12430, 35);
        assertEquals("p736722", p.getId());
        assertEquals("sku867862", p.getName());
        assertEquals(745491L, p.getPrice());
        assertEquals(12430, p.getWeightGrams());
        p.reduceStock(20);
        assertEquals(15, p.getStock());
        p.reduceStock(2);
        assertEquals(13, p.getStock());
        p.restoreStock(16);
        assertEquals(29, p.getStock());
    }

    @Test
    void sample82() {
        var p = new Product("p256114", "sku403721", 851910L, 1527, 33);
        var item = new CartItem(p, 57);
        assertEquals("p256114", item.getProduct().getId());
        assertEquals(57, item.getQuantity());
        assertEquals(48558870L, item.getLineTotal());
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a591185", "sku173541", 103366L, 1278, 26);
        var b = new Product("b510314", "sku405519", 283113L, 1450, 43);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other22723", 34));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a591185"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 37);
        assertEquals(10475181L, cart.getSubtotal());
        assertEquals(53650, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b510314", cart.getItems().get(0).getProduct().getId());
        assertEquals(37, cart.getItems().get(0).getQuantity());
        assertEquals(10475181L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample84() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code850127", CouponType.PERCENT, 117L, 479875L, 231117L, LocalDate.parse("2024-08-28")));
    }

    @Test
    void sample85() {
        var coupon = new Coupon("code301910", CouponType.FIXED, 236558L, 175487L, 75750L, LocalDate.parse("2027-05-19"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1175995L, coupon, LocalDate.parse("2027-06-17")));
    }

    @Test
    void sample86() {
        assertEquals(89000L, new ShippingCalculator().calculate(Region.PROVINCE, 14118, 269759L));
    }

    @Test
    void sample87() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p52653", "sku23622", 605014L, 1501, 34);
        cart.addItem(p, 12);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order539689", cart, null, Region.PROVINCE, null));
        assertEquals(34, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(34, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(34, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-06-23T17:08")));
        assertEquals(34, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown631902"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown512322"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message665872").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message665872").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message665872").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        var p = new Product("p464684", "sku346805", 718530L, 6554, 41);
        assertEquals("p464684", p.getId());
        assertEquals("sku346805", p.getName());
        assertEquals(718530L, p.getPrice());
        assertEquals(6554, p.getWeightGrams());
        p.restoreStock(32);
        assertEquals(73, p.getStock());
        p.reduceStock(43);
        assertEquals(30, p.getStock());
        p.reduceStock(25);
        assertEquals(5, p.getStock());
    }

    @Test
    void sample90() {
        var p = new Product("p353780", "sku299757", 332209L, 1521, 65);
        var item = new CartItem(p, 47);
        assertEquals("p353780", item.getProduct().getId());
        assertEquals(47, item.getQuantity());
        assertEquals(15613823L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a84113", "sku107265", 298540L, 1298, 51);
        var b = new Product("b756191", "sku469766", 875251L, 1075, 58);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 20);
        assertEquals(17505020L, cart.getSubtotal());
        assertEquals(21500, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b756191", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(17505020L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a84113"));
        assertEquals(17505020L, cart.getSubtotal());
        assertEquals(21500, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b756191", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(17505020L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 54));
        assertEquals(17505020L, cart.getSubtotal());
        assertEquals(21500, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b756191", cart.getItems().get(0).getProduct().getId());
        assertEquals(20, cart.getItems().get(0).getQuantity());
        assertEquals(17505020L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b756191");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample92() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code686746", null, 82345L, 456789L, 76688L, LocalDate.parse("2026-10-17")));
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code575730", CouponType.FIXED, 186608L, 232969L, 236910L, LocalDate.parse("2027-03-14"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(679354L, coupon, LocalDate.parse("2027-03-16")));
    }

    @Test
    void sample94() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.INNER_CITY, 23896, -40832L));
    }

    @Test
    void sample95() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p645964", "sku763969", 609746L, 706, 25);
        cart.addItem(p, 2);
        var order = service.placeOrder("order742953", cart, null, Region.REMOTE, LocalDateTime.parse("2026-05-02T10:00"));
        assertEquals(23, p.getStock());
        assertEquals("order742953", order.getId());
        assertEquals(1219492L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(57000L, order.getShipping());
        assertEquals(1276492L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-05-02T10:00"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p645964", order.getItems().get(0).getProduct().getId());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(1219492L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2026-05-02T20:48")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-05-03T12:55")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-05-05T00:39")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(25, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-05-02T10:22")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(25, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown739238"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown654125"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message682249").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message682249").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message682249").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        var p = new Product("p684218", "sku548765", 506323L, 8319, 40);
        assertEquals("p684218", p.getId());
        assertEquals("sku548765", p.getName());
        assertEquals(506323L, p.getPrice());
        assertEquals(8319, p.getWeightGrams());
        p.restoreStock(48);
        assertEquals(88, p.getStock());
        p.reduceStock(9);
        assertEquals(79, p.getStock());
        p.restoreStock(36);
        assertEquals(115, p.getStock());
        p.reduceStock(32);
        assertEquals(83, p.getStock());
    }

    @Test
    void sample98() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 42));
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a615545", "sku578027", 794263L, 530, 39);
        var b = new Product("b659260", "sku433081", 356088L, 167, 78);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a615545"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b659260"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other202681", 55));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b659260"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a615545"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 5);
        assertEquals(3971315L, cart.getSubtotal());
        assertEquals(2650, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a615545", cart.getItems().get(0).getProduct().getId());
        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertEquals(3971315L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample100() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code711932", CouponType.PERCENT, -5L, 300449L, 269621L, LocalDate.parse("2026-03-05")));
    }
}
