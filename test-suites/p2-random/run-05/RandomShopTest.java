package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p475999", "sku215102", 889152L, 8232, 27);
        assertEquals("p475999", p.getId());
        assertEquals("sku215102", p.getName());
        assertEquals(889152L, p.getPrice());
        assertEquals(8232, p.getWeightGrams());
        p.restoreStock(26);
        assertEquals(53, p.getStock());
        p.reduceStock(52);
        assertEquals(1, p.getStock());
        p.restoreStock(49);
        assertEquals(50, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-1));
        assertEquals(50, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p965303", "sku974148", 454938L, 939, 69);
        var item = new CartItem(p, 10);
        assertEquals("p965303", item.getProduct().getId());
        assertEquals(10, item.getQuantity());
        assertEquals(4549380L, item.getLineTotal());
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a251390", "sku848355", 500085L, 926, 32);
        var b = new Product("b138433", "sku491173", 417377L, 448, 5);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a251390"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a251390"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other755144", 43));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 4);
        assertEquals(1669508L, cart.getSubtotal());
        assertEquals(1792, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b138433", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(1669508L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 10);
        assertEquals(6670358L, cart.getSubtotal());
        assertEquals(11052, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b138433", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(1669508L, cart.getItems().get(0).getLineTotal());
        assertEquals("a251390", cart.getItems().get(1).getProduct().getId());
        assertEquals(10, cart.getItems().get(1).getQuantity());
        assertEquals(5000850L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample4() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code598704", CouponType.FIXED, 407704L, -46118L, 50278L, LocalDate.parse("2026-08-10")));
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code679747", CouponType.FIXED, 295205L, 358401L, 242408L, LocalDate.parse("2027-06-04"));
        assertEquals(295205L, new PricingService().calculateDiscount(663922L, coupon, LocalDate.parse("2027-04-16")));
    }

    @Test
    void sample6() {
        assertEquals(121000L, new ShippingCalculator().calculate(Region.REMOTE, 17209, 56019L));
    }

    @Test
    void sample7() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p704864", "sku373738", 496532L, 715, 82);
        cart.addItem(p, 15);
        var order = service.placeOrder("order823837", cart, null, Region.SUBURB, LocalDateTime.parse("2024-06-22T14:00"));
        assertEquals(67, p.getStock());
        assertEquals("order823837", order.getId());
        assertEquals(7447980L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(7447980L, order.getTotal());
        assertEquals(LocalDateTime.parse("2024-06-22T14:00"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p704864", order.getItems().get(0).getProduct().getId());
        assertEquals(15, order.getItems().get(0).getQuantity());
        assertEquals(7447980L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2024-06-24T09:27")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2024-06-24T04:42")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(82, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown602348"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown439583"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message233410").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message233410").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message233410").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku490004", 36013L, 24596, 27));
    }

    @Test
    void sample10() {
        var p = new Product("p410005", "sku997377", 179614L, 859, 73);
        var item = new CartItem(p, 59);
        assertEquals("p410005", item.getProduct().getId());
        assertEquals(59, item.getQuantity());
        assertEquals(10597226L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a705090", "sku810307", 872440L, 1596, 4);
        var b = new Product("b483625", "sku301364", 992464L, 410, 61);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b483625", 3));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 3);
        assertEquals(2977392L, cart.getSubtotal());
        assertEquals(1230, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b483625", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(2977392L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 55));
        assertEquals(2977392L, cart.getSubtotal());
        assertEquals(1230, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b483625", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(2977392L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a705090"));
        assertEquals(2977392L, cart.getSubtotal());
        assertEquals(1230, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b483625", cart.getItems().get(0).getProduct().getId());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(2977392L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 378059L, 499200L, 172743L, LocalDate.parse("2027-01-20")));
    }

    @Test
    void sample13() {
        assertEquals(0L, new PricingService().calculateDiscount(358429L, null, LocalDate.parse("2026-09-14")));
    }

    @Test
    void sample14() {
        assertEquals(73000L, new ShippingCalculator().calculate(Region.REMOTE, 5280, 1038724L));
    }

    @Test
    void sample15() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
    }

    @Test
    void sample16() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message934884").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message934884").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message934884").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p931181", "sku245371", 598328L, 3734, 4);
        assertEquals("p931181", p.getId());
        assertEquals("sku245371", p.getName());
        assertEquals(598328L, p.getPrice());
        assertEquals(3734, p.getWeightGrams());
        p.restoreStock(53);
        assertEquals(57, p.getStock());
        p.restoreStock(31);
        assertEquals(88, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p351518", "sku834869", 296931L, 415, 66);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -3));
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a490969", "sku841830", 605671L, 552, 81);
        var b = new Product("b659328", "sku944875", 916181L, 1437, 50);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b659328", 51));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 43));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample20() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code159522", CouponType.PERCENT, 42L, -45799L, 172950L, LocalDate.parse("2027-12-30")));
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code402876", CouponType.FIXED, 245059L, 343647L, 226471L, LocalDate.parse("2025-10-05"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1179811L, coupon, LocalDate.parse("2025-11-30")));
    }

    @Test
    void sample22() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.SUBURB, 12599, 608584L));
    }

    @Test
    void sample23() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p717621", "sku587442", 815894L, 81, 46);
        var coupon = new Coupon("code548059", CouponType.FIXED, 473258L, 497848L, 28986L, LocalDate.parse("2027-06-18"));
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order645298", null, coupon, Region.REMOTE, LocalDateTime.parse("2027-05-21T02:23")));
        assertEquals(46, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-05-20T17:28")));
        assertEquals(46, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(46, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown336748"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message870119").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message870119").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message870119").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p758206", "sku74107", -4825L, 568, 10));
    }

    @Test
    void sample26() {
        var p = new Product("p202575", "sku157743", 474742L, 389, 43);
        var item = new CartItem(p, 48);
        assertEquals("p202575", item.getProduct().getId());
        assertEquals(48, item.getQuantity());
        assertEquals(22787616L, item.getLineTotal());
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a583097", "sku732803", 102784L, 422, 47);
        var b = new Product("b74838", "sku219589", 352933L, 494, 5);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b74838", 30));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 23));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 16);
        assertEquals(1644544L, cart.getSubtotal());
        assertEquals(6752, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a583097", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(1644544L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample28() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.FIXED, 437478L, 302306L, 36481L, LocalDate.parse("2027-04-23")));
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code338944", CouponType.PERCENT, 16L, 165710L, 185321L, LocalDate.parse("2025-05-18"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(871501L, coupon, null));
    }

    @Test
    void sample30() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.PROVINCE, 20916, 580468L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p59395", "sku731385", 406963L, 561, 45);
        cart.addItem(p, 1);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order914127", cart, null, null, LocalDateTime.parse("2027-05-07T06:15")));
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-05-10T20:00")));
        assertEquals(45, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-05-06T14:08")));
        assertEquals(45, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown641676"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message580415").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message580415").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message580415").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p642533", " ", 558366L, -56, 50));
    }

    @Test
    void sample34() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, -4));
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a622645", "sku737222", 928118L, 1312, 49);
        var b = new Product("b640076", "sku790132", 59579L, 56, 39);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 52));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 6);
        assertEquals(357474L, cart.getSubtotal());
        assertEquals(336, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b640076", cart.getItems().get(0).getProduct().getId());
        assertEquals(6, cart.getItems().get(0).getQuantity());
        assertEquals(357474L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 15);
        assertEquals(1251159L, cart.getSubtotal());
        assertEquals(1176, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b640076", cart.getItems().get(0).getProduct().getId());
        assertEquals(21, cart.getItems().get(0).getQuantity());
        assertEquals(1251159L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 54));
        assertEquals(1251159L, cart.getSubtotal());
        assertEquals(1176, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b640076", cart.getItems().get(0).getProduct().getId());
        assertEquals(21, cart.getItems().get(0).getQuantity());
        assertEquals(1251159L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample36() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code946792", CouponType.PERCENT, 102L, 183852L, 168751L, LocalDate.parse("2027-06-07")));
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code87772", CouponType.PERCENT, 36L, 186393L, 181769L, LocalDate.parse("2026-04-14"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(-11598L, coupon, LocalDate.parse("2026-02-22")));
    }

    @Test
    void sample38() {
        assertEquals(75000L, new ShippingCalculator().calculate(Region.SUBURB, 13484, 334419L));
    }

    @Test
    void sample39() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p485756", "sku412629", 478439L, 949, 30);
        cart.addItem(p, 13);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(null, cart, null, Region.REMOTE, LocalDateTime.parse("2027-09-10T07:47")));
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-09-12T01:14")));
        assertEquals(30, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown474714"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown606756"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf(" "));
        assertEquals("InsufficientStockException", new InsufficientStockException("message190272").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message190272").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message190272").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        var p = new Product("p910557", "sku972055", 545148L, 20230, 19);
        assertEquals("p910557", p.getId());
        assertEquals("sku972055", p.getName());
        assertEquals(545148L, p.getPrice());
        assertEquals(20230, p.getWeightGrams());
        p.reduceStock(4);
        assertEquals(15, p.getStock());
        p.restoreStock(60);
        assertEquals(75, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-5));
        assertEquals(75, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(0));
        assertEquals(75, p.getStock());
    }

    @Test
    void sample42() {
        var p = new Product("p498700", "sku736376", 373868L, 569, 70);
        var item = new CartItem(p, 54);
        assertEquals("p498700", item.getProduct().getId());
        assertEquals(54, item.getQuantity());
        assertEquals(20188872L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a571791", "sku501171", 514914L, 201, 3);
        var b = new Product("b973742", "sku248766", 901482L, 555, 31);
        cart.addItem(b, 22);
        assertEquals(19832604L, cart.getSubtotal());
        assertEquals(12210, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b973742", cart.getItems().get(0).getProduct().getId());
        assertEquals(22, cart.getItems().get(0).getQuantity());
        assertEquals(19832604L, cart.getItems().get(0).getLineTotal());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 30));
        assertEquals(19832604L, cart.getSubtotal());
        assertEquals(12210, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b973742", cart.getItems().get(0).getProduct().getId());
        assertEquals(22, cart.getItems().get(0).getQuantity());
        assertEquals(19832604L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -3));
        assertEquals(19832604L, cart.getSubtotal());
        assertEquals(12210, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b973742", cart.getItems().get(0).getProduct().getId());
        assertEquals(22, cart.getItems().get(0).getQuantity());
        assertEquals(19832604L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a571791"));
        assertEquals(19832604L, cart.getSubtotal());
        assertEquals(12210, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b973742", cart.getItems().get(0).getProduct().getId());
        assertEquals(22, cart.getItems().get(0).getQuantity());
        assertEquals(19832604L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b973742");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a571791"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 57));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample44() {
        var coupon = new Coupon("code372351", CouponType.FIXED, 179247L, 155640L, 295344L, LocalDate.parse("2025-08-17"));
        assertEquals("code372351", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(179247L, coupon.getValue());
        assertEquals(155640L, coupon.getMinOrder());
        assertEquals(295344L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-08-17"), coupon.getExpiry());
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code942137", CouponType.PERCENT, 97L, 449030L, 265799L, LocalDate.parse("2025-02-18"));
        assertEquals(265799L, new PricingService().calculateDiscount(637421L, coupon, LocalDate.parse("2025-02-15")));
    }

    @Test
    void sample46() {
        assertEquals(101000L, new ShippingCalculator().calculate(Region.PROVINCE, 17297, 828959L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p403724", "sku813603", 421762L, 959, 67);
        cart.addItem(p, 11);
        var order = service.placeOrder("order464389", cart, null, Region.REMOTE, LocalDateTime.parse("2026-02-04T07:21"));
        assertEquals(56, p.getStock());
        assertEquals("order464389", order.getId());
        assertEquals(4639382L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(95000L, order.getShipping());
        assertEquals(4734382L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-02-04T07:21"), order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p403724", order.getItems().get(0).getProduct().getId());
        assertEquals(11, order.getItems().get(0).getQuantity());
        assertEquals(4639382L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2026-02-04T19:53")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(67, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-02-03T15:35")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(67, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(67, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf(" "));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown359879"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf(" "));
        assertEquals("InsufficientStockException", new InsufficientStockException("message669331").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message669331").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message669331").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku431563", 933270L, 10118, 48));
    }

    @Test
    void sample50() {
        var p = new Product("p341623", "sku836083", 128445L, 636, 61);
        var item = new CartItem(p, 49);
        assertEquals("p341623", item.getProduct().getId());
        assertEquals(49, item.getQuantity());
        assertEquals(6293805L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a817006", "sku445578", 648410L, 885, 40);
        var b = new Product("b453481", "sku820224", 819286L, 925, 63);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b453481", 1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 7);
        assertEquals(5735002L, cart.getSubtotal());
        assertEquals(6475, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b453481", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(5735002L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other616183", 4));
        assertEquals(5735002L, cart.getSubtotal());
        assertEquals(6475, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b453481", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(5735002L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a817006", 60));
        assertEquals(5735002L, cart.getSubtotal());
        assertEquals(6475, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b453481", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(5735002L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("b453481", 57));
        assertEquals(5735002L, cart.getSubtotal());
        assertEquals(6475, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b453481", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(5735002L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, -4));
        assertEquals(5735002L, cart.getSubtotal());
        assertEquals(6475, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b453481", cart.getItems().get(0).getProduct().getId());
        assertEquals(7, cart.getItems().get(0).getQuantity());
        assertEquals(5735002L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample52() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code540447", CouponType.FIXED, 278630L, -45231L, 121649L, null));
    }

    @Test
    void sample53() {
        assertEquals(0L, new PricingService().calculateDiscount(540873L, null, LocalDate.parse("2024-08-17")));
    }

    @Test
    void sample54() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.REMOTE, 1253, -26226L));
    }

    @Test
    void sample55() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p974600", "sku719646", 790591L, 1537, 37);
        cart.addItem(p, 15);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order152814", cart, null, null, LocalDateTime.parse("2024-08-21T03:06")));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(37, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown221449"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown97287"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message552671").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message552671").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message552671").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p947725", "sku485270", 312183L, 9564, 68);
        assertEquals("p947725", p.getId());
        assertEquals("sku485270", p.getName());
        assertEquals(312183L, p.getPrice());
        assertEquals(9564, p.getWeightGrams());
        p.reduceStock(39);
        assertEquals(29, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-3));
        assertEquals(29, p.getStock());
        p.restoreStock(46);
        assertEquals(75, p.getStock());
        p.restoreStock(32);
        assertEquals(107, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-2));
        assertEquals(107, p.getStock());
    }

    @Test
    void sample58() {
        var p = new Product("p723671", "sku885300", 654340L, 52, 77);
        var item = new CartItem(p, 40);
        assertEquals("p723671", item.getProduct().getId());
        assertEquals(40, item.getQuantity());
        assertEquals(26173600L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a399556", "sku112929", 912573L, 1553, 80);
        var b = new Product("b65784", "sku739179", 31829L, 1431, 46);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a399556"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a399556", 27));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 13);
        assertEquals(11863449L, cart.getSubtotal());
        assertEquals(20189, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a399556", cart.getItems().get(0).getProduct().getId());
        assertEquals(13, cart.getItems().get(0).getQuantity());
        assertEquals(11863449L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample60() {
        var coupon = new Coupon("code378021", CouponType.PERCENT, 89L, 442701L, 255795L, LocalDate.parse("2027-12-18"));
        assertEquals("code378021", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(89L, coupon.getValue());
        assertEquals(442701L, coupon.getMinOrder());
        assertEquals(255795L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-12-18"), coupon.getExpiry());
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code461558", CouponType.FIXED, 493474L, 124616L, 205770L, LocalDate.parse("2025-10-18"));
        assertEquals(493474L, new PricingService().calculateDiscount(1029668L, coupon, LocalDate.parse("2025-10-08")));
    }

    @Test
    void sample62() {
        assertEquals(107000L, new ShippingCalculator().calculate(Region.REMOTE, 13773, 479124L));
    }

    @Test
    void sample63() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p86371", "sku32381", 243958L, 1332, 18);
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order524009", null, null, Region.REMOTE, LocalDateTime.parse("2025-06-16T13:41")));
        assertEquals(18, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(18, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(18, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(18, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(18, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(18, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown310098"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown245637"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message850727").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message850727").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message850727").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p757728", null, 397735L, 10223, 61));
    }

    @Test
    void sample66() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 38));
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a527608", "sku395076", 692099L, 593, 69);
        var b = new Product("b12473", "sku631437", 650983L, 1585, 46);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b12473", 2));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b12473"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other45757", 13));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code970243", null, 444939L, 191114L, 228100L, null));
    }

    @Test
    void sample69() {
        assertEquals(0L, new PricingService().calculateDiscount(107070L, null, LocalDate.parse("2027-05-20")));
    }

    @Test
    void sample70() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 13718, 542078L));
    }

    @Test
    void sample71() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p911638", "sku822700", 169610L, 1242, 50);
        cart.addItem(p, 4);
        var coupon = new Coupon("code854934", CouponType.PERCENT, 75L, 543808L, 16624L, LocalDate.parse("2026-11-17"));
        var order = service.placeOrder("order884433", cart, coupon, Region.SUBURB, LocalDateTime.parse("2026-10-10T08:56"));
        assertEquals(46, p.getStock());
        assertEquals("order884433", order.getId());
        assertEquals(678440L, order.getSubtotal());
        assertEquals(16624L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(661816L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-10-10T08:56"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p911638", order.getItems().get(0).getProduct().getId());
        assertEquals(4, order.getItems().get(0).getQuantity());
        assertEquals(678440L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(46, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(46, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2026-10-11T21:38")));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(46, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown567048"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown80749"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message725492").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message725492").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message725492").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        var p = new Product("p817725", "sku570519", 542492L, 6522, 8);
        assertEquals("p817725", p.getId());
        assertEquals("sku570519", p.getName());
        assertEquals(542492L, p.getPrice());
        assertEquals(6522, p.getWeightGrams());
        p.reduceStock(6);
        assertEquals(2, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(14));
        assertEquals(2, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-3));
        assertEquals(2, p.getStock());
        p.restoreStock(51);
        assertEquals(53, p.getStock());
        p.restoreStock(49);
        assertEquals(102, p.getStock());
    }

    @Test
    void sample74() {
        var p = new Product("p486083", "sku103478", 339012L, 1488, 58);
        var item = new CartItem(p, 9);
        assertEquals("p486083", item.getProduct().getId());
        assertEquals(9, item.getQuantity());
        assertEquals(3051108L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a241419", "sku681013", 950632L, 905, 43);
        var b = new Product("b929735", "sku801150", 887211L, 21, 44);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 53));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other939145"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b929735", 1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b929735"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        var coupon = new Coupon("code855287", CouponType.FIXED, 448097L, 252155L, 48045L, LocalDate.parse("2024-06-24"));
        assertEquals("code855287", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(448097L, coupon.getValue());
        assertEquals(252155L, coupon.getMinOrder());
        assertEquals(48045L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-06-24"), coupon.getExpiry());
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code916512", CouponType.PERCENT, 60L, 38543L, 56621L, LocalDate.parse("2024-09-10"));
        assertEquals(56621L, new PricingService().calculateDiscount(461737L, coupon, LocalDate.parse("2024-09-01")));
    }

    @Test
    void sample78() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 5316, 1110096L));
    }

    @Test
    void sample79() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p536325", "sku220012", 458949L, 1070, 68);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order226677", null, null, null, LocalDateTime.parse("2025-10-10T06:07")));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-10-11T11:42")));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(68, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown594892"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message485182").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message485182").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message485182").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p468943", "sku840987", 813830L, 21285, 58);
        assertEquals("p468943", p.getId());
        assertEquals("sku840987", p.getName());
        assertEquals(813830L, p.getPrice());
        assertEquals(21285, p.getWeightGrams());
        p.restoreStock(12);
        assertEquals(70, p.getStock());
        p.reduceStock(22);
        assertEquals(48, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(57));
        assertEquals(48, p.getStock());
        p.reduceStock(44);
        assertEquals(4, p.getStock());
    }

    @Test
    void sample82() {
        var p = new Product("p986043", "sku394060", 601192L, 46, 22);
        var item = new CartItem(p, 2);
        assertEquals("p986043", item.getProduct().getId());
        assertEquals(2, item.getQuantity());
        assertEquals(1202384L, item.getLineTotal());
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a485753", "sku444532", 644523L, 1170, 18);
        var b = new Product("b248028", "sku388676", 714870L, 568, 53);
        cart.addItem(b, 33);
        assertEquals(23590710L, cart.getSubtotal());
        assertEquals(18744, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b248028", cart.getItems().get(0).getProduct().getId());
        assertEquals(33, cart.getItems().get(0).getQuantity());
        assertEquals(23590710L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b248028");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("a485753", -3));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample84() {
        var coupon = new Coupon("code965731", CouponType.PERCENT, 4L, 20961L, 171758L, LocalDate.parse("2027-06-06"));
        assertEquals("code965731", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(4L, coupon.getValue());
        assertEquals(20961L, coupon.getMinOrder());
        assertEquals(171758L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-06-06"), coupon.getExpiry());
    }

    @Test
    void sample85() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(1064192L, null, null));
    }

    @Test
    void sample86() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 4746, 1171534L));
    }

    @Test
    void sample87() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p214773", "sku840471", 154989L, 1333, 8);
        cart.addItem(p, 3);
        var coupon = new Coupon("code996851", CouponType.FIXED, 187276L, 364652L, 163030L, LocalDate.parse("2026-03-21"));
        var order = service.placeOrder("order842667", cart, coupon, Region.PROVINCE, LocalDateTime.parse("2026-03-19T08:41"));
        assertEquals(5, p.getStock());
        assertEquals("order842667", order.getId());
        assertEquals(464967L, order.getSubtotal());
        assertEquals(187276L, order.getDiscount());
        assertEquals(47000L, order.getShipping());
        assertEquals(324691L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-03-19T08:41"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p214773", order.getItems().get(0).getProduct().getId());
        assertEquals(3, order.getItems().get(0).getQuantity());
        assertEquals(464967L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(5, p.getStock());
        assertEquals(222152L, service.cancelOrder(order, LocalDateTime.parse("2026-03-23T04:24")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown789397"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample89() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku714009", 553494L, 2227, 52));
    }

    @Test
    void sample90() {
        var p = new Product("p540785", "sku808865", 516695L, 971, 54);
        var item = new CartItem(p, 41);
        assertEquals("p540785", item.getProduct().getId());
        assertEquals(41, item.getQuantity());
        assertEquals(21184495L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a969882", "sku303657", 29362L, 931, 41);
        var b = new Product("b593258", "sku623964", 535934L, 1181, 33);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b593258", 13));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b593258", 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 4);
        assertEquals(117448L, cart.getSubtotal());
        assertEquals(3724, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a969882", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(117448L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b593258"));
        assertEquals(117448L, cart.getSubtotal());
        assertEquals(3724, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a969882", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(117448L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, -1));
        assertEquals(117448L, cart.getSubtotal());
        assertEquals(3724, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a969882", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(117448L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("a969882", 28);
        assertEquals(822136L, cart.getSubtotal());
        assertEquals(26068, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a969882", cart.getItems().get(0).getProduct().getId());
        assertEquals(28, cart.getItems().get(0).getQuantity());
        assertEquals(822136L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample92() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code963470", CouponType.FIXED, 346165L, -31330L, 257029L, LocalDate.parse("2027-11-27")));
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code410192", CouponType.FIXED, 392839L, 442589L, 71688L, LocalDate.parse("2025-10-27"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(525303L, coupon, LocalDate.parse("2025-12-25")));
    }

    @Test
    void sample94() {
        assertEquals(77000L, new ShippingCalculator().calculate(Region.SUBURB, 13591, 268393L));
    }

    @Test
    void sample95() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p489900", "sku528789", 766617L, 359, 82);
        cart.addItem(p, 9);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(null, cart, null, Region.INNER_CITY, LocalDateTime.parse("2026-07-08T16:57")));
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(82, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(82, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown137498"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown80929"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample97() {
        var p = new Product("p752355", "sku40918", 953914L, 15315, 3);
        assertEquals("p752355", p.getId());
        assertEquals("sku40918", p.getName());
        assertEquals(953914L, p.getPrice());
        assertEquals(15315, p.getWeightGrams());
        p.restoreStock(12);
        assertEquals(15, p.getStock());
        p.reduceStock(12);
        assertEquals(3, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(4));
        assertEquals(3, p.getStock());
    }

    @Test
    void sample98() {
        var p = new Product("p864479", "sku75601", 904664L, 345, 5);
        var item = new CartItem(p, 38);
        assertEquals("p864479", item.getProduct().getId());
        assertEquals(38, item.getQuantity());
        assertEquals(34377232L, item.getLineTotal());
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a44139", "sku514253", 203480L, 1432, 5);
        var b = new Product("b314318", "sku549185", 490536L, 1230, 24);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 30));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a44139", 54));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a44139"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a44139"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 9));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 37));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 28));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample100() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code397923", CouponType.FIXED, 536413L, 498284L, 211740L, null));
    }
}
