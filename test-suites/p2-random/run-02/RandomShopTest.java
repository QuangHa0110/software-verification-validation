package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p247530", "sku755235", 43017L, 9203, 45);
        assertEquals("p247530", p.getId());
        assertEquals("sku755235", p.getName());
        assertEquals(43017L, p.getPrice());
        assertEquals(9203, p.getWeightGrams());
        p.restoreStock(55);
        assertEquals(100, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-1));
        assertEquals(100, p.getStock());
        p.restoreStock(21);
        assertEquals(121, p.getStock());
        p.restoreStock(12);
        assertEquals(133, p.getStock());
    }

    @Test
    void sample2() {
        var p = new Product("p83026", "sku614724", 688152L, 157, 71);
        var item = new CartItem(p, 13);
        assertEquals("p83026", item.getProduct().getId());
        assertEquals(13, item.getQuantity());
        assertEquals(8945976L, item.getLineTotal());
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a582870", "sku725781", 2897L, 909, 6);
        var b = new Product("b228304", "sku738837", 995867L, 382, 55);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a582870", 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 8);
        assertEquals(7966936L, cart.getSubtotal());
        assertEquals(3056, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b228304", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(7966936L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("b228304", 45);
        assertEquals(44814015L, cart.getSubtotal());
        assertEquals(17190, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b228304", cart.getItems().get(0).getProduct().getId());
        assertEquals(45, cart.getItems().get(0).getQuantity());
        assertEquals(44814015L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a582870", 6));
        assertEquals(44814015L, cart.getSubtotal());
        assertEquals(17190, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b228304", cart.getItems().get(0).getProduct().getId());
        assertEquals(45, cart.getItems().get(0).getQuantity());
        assertEquals(44814015L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a582870", 33));
        assertEquals(44814015L, cart.getSubtotal());
        assertEquals(17190, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b228304", cart.getItems().get(0).getProduct().getId());
        assertEquals(45, cart.getItems().get(0).getQuantity());
        assertEquals(44814015L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample4() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code643979", CouponType.PERCENT, 26L, 325891L, 139632L, null));
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code123437", CouponType.PERCENT, 54L, 416852L, 275183L, LocalDate.parse("2025-12-27"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(641801L, coupon, LocalDate.parse("2026-01-15")));
    }

    @Test
    void sample6() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 344, 1018825L));
    }

    @Test
    void sample7() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p565279", "sku410306", 519262L, 1499, 68);
        cart.addItem(p, 4);
        var coupon = new Coupon("code85302", CouponType.FIXED, 457214L, 80785L, 211225L, LocalDate.parse("2027-01-28"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order767869", cart, coupon, Region.SUBURB, LocalDateTime.parse("2027-02-20T02:24")));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-02-23T00:34")));
        assertEquals(68, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(68, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown362429"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertEquals("InsufficientStockException", new InsufficientStockException("message409440").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message409440").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message409440").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p100729", "sku50168", 317337L, 14470, -2));
    }

    @Test
    void sample10() {
        var p = new Product("p575730", "sku20980", 993779L, 293, 3);
        var item = new CartItem(p, 4);
        assertEquals("p575730", item.getProduct().getId());
        assertEquals(4, item.getQuantity());
        assertEquals(3975116L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a690995", "sku573036", 346980L, 1202, 11);
        var b = new Product("b870345", "sku325171", 856348L, 311, 43);
        cart.addItem(b, 28);
        assertEquals(23977744L, cart.getSubtotal());
        assertEquals(8708, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b870345", cart.getItems().get(0).getProduct().getId());
        assertEquals(28, cart.getItems().get(0).getQuantity());
        assertEquals(23977744L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 10);
        assertEquals(32541224L, cart.getSubtotal());
        assertEquals(11818, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b870345", cart.getItems().get(0).getProduct().getId());
        assertEquals(38, cart.getItems().get(0).getQuantity());
        assertEquals(32541224L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b870345");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 4);
        assertEquals(3425392L, cart.getSubtotal());
        assertEquals(1244, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b870345", cart.getItems().get(0).getProduct().getId());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(3425392L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample12() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code421367", CouponType.PERCENT, 89L, -45215L, 289330L, LocalDate.parse("2025-09-10")));
    }

    @Test
    void sample13() {
        var coupon = new Coupon("code201628", CouponType.FIXED, 199702L, 415199L, 296000L, LocalDate.parse("2025-09-28"));
        assertEquals(199702L, new PricingService().calculateDiscount(467253L, coupon, LocalDate.parse("2025-08-23")));
    }

    @Test
    void sample14() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 19489, 670595L));
    }

    @Test
    void sample15() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(new PricingService(), null));
    }

    @Test
    void sample16() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown924067"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown671627"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message387709").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message387709").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message387709").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p568878", "sku876980", 335397L, 12256, 70);
        assertEquals("p568878", p.getId());
        assertEquals("sku876980", p.getName());
        assertEquals(335397L, p.getPrice());
        assertEquals(12256, p.getWeightGrams());
        p.restoreStock(18);
        assertEquals(88, p.getStock());
        p.restoreStock(27);
        assertEquals(115, p.getStock());
        p.reduceStock(11);
        assertEquals(104, p.getStock());
        p.restoreStock(17);
        assertEquals(121, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p683774", "sku3368", 924267L, 520, 58);
        var item = new CartItem(p, 5);
        assertEquals("p683774", item.getProduct().getId());
        assertEquals(5, item.getQuantity());
        assertEquals(4621335L, item.getLineTotal());
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a681389", "sku992825", 922157L, 858, 72);
        var b = new Product("b536335", "sku248727", 169128L, 239, 48);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a681389", 1));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b536335"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other529939", 34));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b536335"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b536335"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other109661"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b536335"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample20() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code323866", CouponType.PERCENT, 126L, 546466L, 270691L, LocalDate.parse("2026-10-16")));
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code614685", CouponType.PERCENT, 47L, 546478L, 234340L, LocalDate.parse("2024-05-14"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(235107L, coupon, LocalDate.parse("2024-05-19")));
    }

    @Test
    void sample22() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 2568, 1125518L));
    }

    @Test
    void sample23() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p29136", "sku680673", 952127L, 691, 66);
        cart.addItem(p, 17);
        var coupon = new Coupon("code109556", CouponType.PERCENT, 60L, 316977L, 285147L, LocalDate.parse("2027-11-10"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order798260", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2027-12-21T20:56")));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-12-22T04:04")));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(66, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(66, p.getStock());
    }

    @Test
    void sample24() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown99835"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown939841"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown762248"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message42966").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message42966").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message42966").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        var p = new Product("p356149", "sku953545", 8478L, 20206, 84);
        assertEquals("p356149", p.getId());
        assertEquals("sku953545", p.getName());
        assertEquals(8478L, p.getPrice());
        assertEquals(20206, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-2));
        assertEquals(84, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-3));
        assertEquals(84, p.getStock());
        p.restoreStock(56);
        assertEquals(140, p.getStock());
        p.restoreStock(33);
        assertEquals(173, p.getStock());
        p.reduceStock(37);
        assertEquals(136, p.getStock());
    }

    @Test
    void sample26() {
        var p = new Product("p251871", "sku443567", 303121L, 746, 79);
        var item = new CartItem(p, 33);
        assertEquals("p251871", item.getProduct().getId());
        assertEquals(33, item.getQuantity());
        assertEquals(10002993L, item.getLineTotal());
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a489282", "sku121447", 323009L, 1413, 58);
        var b = new Product("b672512", "sku298726", 545240L, 1124, 57);
        cart.addItem(b, 18);
        assertEquals(9814320L, cart.getSubtotal());
        assertEquals(20232, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b672512", cart.getItems().get(0).getProduct().getId());
        assertEquals(18, cart.getItems().get(0).getQuantity());
        assertEquals(9814320L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 38);
        assertEquals(22088662L, cart.getSubtotal());
        assertEquals(73926, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b672512", cart.getItems().get(0).getProduct().getId());
        assertEquals(18, cart.getItems().get(0).getQuantity());
        assertEquals(9814320L, cart.getItems().get(0).getLineTotal());
        assertEquals("a489282", cart.getItems().get(1).getProduct().getId());
        assertEquals(38, cart.getItems().get(1).getQuantity());
        assertEquals(12274342L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, 49));
        assertEquals(22088662L, cart.getSubtotal());
        assertEquals(73926, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b672512", cart.getItems().get(0).getProduct().getId());
        assertEquals(18, cart.getItems().get(0).getQuantity());
        assertEquals(9814320L, cart.getItems().get(0).getLineTotal());
        assertEquals("a489282", cart.getItems().get(1).getProduct().getId());
        assertEquals(38, cart.getItems().get(1).getQuantity());
        assertEquals(12274342L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other88438", 60));
        assertEquals(22088662L, cart.getSubtotal());
        assertEquals(73926, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b672512", cart.getItems().get(0).getProduct().getId());
        assertEquals(18, cart.getItems().get(0).getQuantity());
        assertEquals(9814320L, cart.getItems().get(0).getLineTotal());
        assertEquals("a489282", cart.getItems().get(1).getProduct().getId());
        assertEquals(38, cart.getItems().get(1).getQuantity());
        assertEquals(12274342L, cart.getItems().get(1).getLineTotal());
        cart.updateQuantity("b672512", 42);
        assertEquals(35174422L, cart.getSubtotal());
        assertEquals(100902, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b672512", cart.getItems().get(0).getProduct().getId());
        assertEquals(42, cart.getItems().get(0).getQuantity());
        assertEquals(22900080L, cart.getItems().get(0).getLineTotal());
        assertEquals("a489282", cart.getItems().get(1).getProduct().getId());
        assertEquals(38, cart.getItems().get(1).getQuantity());
        assertEquals(12274342L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample28() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.PERCENT, 32L, 539790L, -1148L, LocalDate.parse("2027-08-13")));
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code965741", CouponType.PERCENT, 43L, 14427L, 273979L, LocalDate.parse("2025-02-20"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1097485L, coupon, LocalDate.parse("2025-04-13")));
    }

    @Test
    void sample30() {
        assertEquals(25000L, new ShippingCalculator().calculate(Region.SUBURB, 476, 441654L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p416378", "sku978509", 712827L, 1091, 48);
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order107549", null, null, Region.REMOTE, LocalDateTime.parse("2024-12-24T21:01")));
        assertEquals(48, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(48, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(48, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(48, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown100082"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown392936"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown223323"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message945570").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message945570").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message945570").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p686673", "sku639787", 519858L, 7341, 51);
        assertEquals("p686673", p.getId());
        assertEquals("sku639787", p.getName());
        assertEquals(519858L, p.getPrice());
        assertEquals(7341, p.getWeightGrams());
        p.reduceStock(31);
        assertEquals(20, p.getStock());
        p.restoreStock(4);
        assertEquals(24, p.getStock());
        p.restoreStock(21);
        assertEquals(45, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p588573", "sku146019", 461860L, 1382, 32);
        var item = new CartItem(p, 59);
        assertEquals("p588573", item.getProduct().getId());
        assertEquals(59, item.getQuantity());
        assertEquals(27249740L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a679866", "sku896964", 463390L, 1373, 63);
        var b = new Product("b229875", "sku360241", 35289L, 27, 78);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a679866"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem(" "));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a679866"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a679866", 29));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a679866"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b229875", 17));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample36() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code106470", CouponType.FIXED, 285577L, 4589L, 130250L, null));
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code938624", CouponType.PERCENT, 89L, 233163L, 76844L, LocalDate.parse("2027-10-08"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(969447L, coupon, LocalDate.parse("2027-11-18")));
    }

    @Test
    void sample38() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 1223, 949117L));
    }

    @Test
    void sample39() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p258386", "sku492030", 534232L, 1051, 33);
        cart.addItem(p, 2);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder(" ", cart, null, null, LocalDateTime.parse("2024-02-22T14:54")));
        assertEquals(33, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-02-25T07:04")));
        assertEquals(33, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(33, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-02-23T09:26")));
        assertEquals(33, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-02-26T12:21")));
        assertEquals(33, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-02-23T17:13")));
        assertEquals(33, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown462877"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown831203"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message365487").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message365487").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message365487").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        var p = new Product("p645181", "sku492933", 902554L, 4427, 58);
        assertEquals("p645181", p.getId());
        assertEquals("sku492933", p.getName());
        assertEquals(902554L, p.getPrice());
        assertEquals(4427, p.getWeightGrams());
        p.restoreStock(48);
        assertEquals(106, p.getStock());
        p.reduceStock(27);
        assertEquals(79, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-2));
        assertEquals(79, p.getStock());
        p.reduceStock(52);
        assertEquals(27, p.getStock());
        p.reduceStock(12);
        assertEquals(15, p.getStock());
    }

    @Test
    void sample42() {
        var p = new Product("p694305", "sku924561", 65453L, 764, 22);
        var item = new CartItem(p, 48);
        assertEquals("p694305", item.getProduct().getId());
        assertEquals(48, item.getQuantity());
        assertEquals(3141744L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a294654", "sku469959", 532904L, 574, 2);
        var b = new Product("b12945", "sku312221", 232517L, 1076, 42);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a294654"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a294654"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other668932"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b12945", 54));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem(null));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample44() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, null, 357903L, -45828L, 152436L, null));
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code293266", CouponType.PERCENT, 32L, 139161L, 284433L, LocalDate.parse("2027-12-28"));
        assertThrows(shop.CouponNotApplicableException.class, () -> new PricingService().calculateDiscount(34417L, coupon, LocalDate.parse("2027-12-22")));
    }

    @Test
    void sample46() {
        assertEquals(37000L, new ShippingCalculator().calculate(Region.PROVINCE, 1213, 521478L));
    }

    @Test
    void sample47() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, null));
    }

    @Test
    void sample48() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown643495"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message76618").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message76618").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message76618").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        var p = new Product("p391249", "sku526943", 868388L, 8454, 21);
        assertEquals("p391249", p.getId());
        assertEquals("sku526943", p.getName());
        assertEquals(868388L, p.getPrice());
        assertEquals(8454, p.getWeightGrams());
        p.restoreStock(28);
        assertEquals(49, p.getStock());
        p.restoreStock(5);
        assertEquals(54, p.getStock());
        p.restoreStock(37);
        assertEquals(91, p.getStock());
        p.restoreStock(42);
        assertEquals(133, p.getStock());
        p.reduceStock(32);
        assertEquals(101, p.getStock());
    }

    @Test
    void sample50() {
        var p = new Product("p810482", "sku695512", 398298L, 923, 38);
        var item = new CartItem(p, 44);
        assertEquals("p810482", item.getProduct().getId());
        assertEquals(44, item.getQuantity());
        assertEquals(17525112L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a245701", "sku500809", 430543L, 646, 5);
        var b = new Product("b517902", "sku888511", 870282L, 197, 6);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a245701", 24));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a245701", 27));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 12));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample52() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code952596", null, 323160L, -47260L, 135791L, LocalDate.parse("2025-03-15")));
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code35557", CouponType.PERCENT, 74L, 192300L, 213755L, LocalDate.parse("2026-10-30"));
        assertEquals(213755L, new PricingService().calculateDiscount(945233L, coupon, LocalDate.parse("2026-10-19")));
    }

    @Test
    void sample54() {
        assertEquals(109000L, new ShippingCalculator().calculate(Region.PROVINCE, 19106, 828013L));
    }

    @Test
    void sample55() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p390406", "sku948543", 905547L, 1289, 30);
        cart.addItem(p, 12);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order886160", cart, null, null, LocalDateTime.parse("2027-06-11T04:38")));
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(30, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2027-06-14T19:25")));
        assertEquals(30, p.getStock());
    }

    @Test
    void sample56() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown944751"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException(null).getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException(null).getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException(null).getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p626126", "sku340947", 365543L, 1682, 53);
        assertEquals("p626126", p.getId());
        assertEquals("sku340947", p.getName());
        assertEquals(365543L, p.getPrice());
        assertEquals(1682, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(57));
        assertEquals(53, p.getStock());
        p.reduceStock(41);
        assertEquals(12, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(19));
        assertEquals(12, p.getStock());
        p.restoreStock(9);
        assertEquals(21, p.getStock());
        p.restoreStock(58);
        assertEquals(79, p.getStock());
    }

    @Test
    void sample58() {
        var p = new Product("p493405", "sku255941", 189767L, 992, 34);
        var item = new CartItem(p, 56);
        assertEquals("p493405", item.getProduct().getId());
        assertEquals(56, item.getQuantity());
        assertEquals(10626952L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a794169", "sku778717", 221821L, 1069, 43);
        var b = new Product("b542530", "sku473863", 449627L, 465, 33);
        cart.addItem(a, 2);
        assertEquals(443642L, cart.getSubtotal());
        assertEquals(2138, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a794169", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(443642L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other52559", 14));
        assertEquals(443642L, cart.getSubtotal());
        assertEquals(2138, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a794169", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(443642L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b542530", 44));
        assertEquals(443642L, cart.getSubtotal());
        assertEquals(2138, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a794169", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(443642L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b542530", 33));
        assertEquals(443642L, cart.getSubtotal());
        assertEquals(2138, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a794169", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(443642L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, -5));
        assertEquals(443642L, cart.getSubtotal());
        assertEquals(2138, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a794169", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(443642L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 10);
        assertEquals(4939912L, cart.getSubtotal());
        assertEquals(6788, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a794169", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(443642L, cart.getItems().get(0).getLineTotal());
        assertEquals("b542530", cart.getItems().get(1).getProduct().getId());
        assertEquals(10, cart.getItems().get(1).getQuantity());
        assertEquals(4496270L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other287189"));
        assertEquals(4939912L, cart.getSubtotal());
        assertEquals(6788, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a794169", cart.getItems().get(0).getProduct().getId());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(443642L, cart.getItems().get(0).getLineTotal());
        assertEquals("b542530", cart.getItems().get(1).getProduct().getId());
        assertEquals(10, cart.getItems().get(1).getQuantity());
        assertEquals(4496270L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample60() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code482031", CouponType.FIXED, 304810L, 574014L, 34812L, null));
    }

    @Test
    void sample61() {
        assertEquals(0L, new PricingService().calculateDiscount(86006L, null, LocalDate.parse("2025-05-21")));
    }

    @Test
    void sample62() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.SUBURB, 12535, 692752L));
    }

    @Test
    void sample63() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p876077", "sku946189", 451893L, 750, 36);
        cart.addItem(p, 14);
        var coupon = new Coupon("code88105", CouponType.FIXED, 536917L, 590321L, 34846L, LocalDate.parse("2025-11-03"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order130570", cart, coupon, null, LocalDateTime.parse("2025-11-23T09:14")));
        assertEquals(36, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(36, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(36, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, null));
        assertEquals(36, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-11-22T16:57")));
        assertEquals(36, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(36, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.NullPointerException.class, () -> CouponType.valueOf(null));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message857216").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message857216").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message857216").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        var p = new Product("p677287", "sku107529", 512700L, 11873, 38);
        assertEquals("p677287", p.getId());
        assertEquals("sku107529", p.getName());
        assertEquals(512700L, p.getPrice());
        assertEquals(11873, p.getWeightGrams());
        p.restoreStock(44);
        assertEquals(82, p.getStock());
        p.restoreStock(4);
        assertEquals(86, p.getStock());
    }

    @Test
    void sample66() {
        var p = new Product("p260018", "sku356937", 594377L, 170, 24);
        var item = new CartItem(p, 9);
        assertEquals("p260018", item.getProduct().getId());
        assertEquals(9, item.getQuantity());
        assertEquals(5349393L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a314704", "sku975286", 966895L, 1343, 54);
        var b = new Product("b715844", "sku233462", 569116L, 1085, 27);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a314704"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 50));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b715844", 18));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 60));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a314704"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a314704"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b715844"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code535875", CouponType.PERCENT, 46L, 59269L, -14032L, null));
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code341102", CouponType.PERCENT, 30L, 131871L, 133566L, LocalDate.parse("2027-10-09"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> new PricingService().calculateDiscount(27058L, coupon, null));
    }

    @Test
    void sample70() {
        assertEquals(57000L, new ShippingCalculator().calculate(Region.REMOTE, 1372, 1051738L));
    }

    @Test
    void sample71() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p256834", "sku59503", 240834L, 325, 83);
        cart.addItem(p, 6);
        var coupon = new Coupon("code405668", CouponType.FIXED, 439874L, 82250L, 180980L, LocalDate.parse("2024-05-12"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order78560", cart, coupon, Region.SUBURB, LocalDateTime.parse("2024-07-06T14:20")));
        assertEquals(83, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(83, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-07-07T02:24")));
        assertEquals(83, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown464695"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown264406"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message553211").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message553211").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message553211").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        var p = new Product("p289749", "sku927722", 157391L, 19827, 56);
        assertEquals("p289749", p.getId());
        assertEquals("sku927722", p.getName());
        assertEquals(157391L, p.getPrice());
        assertEquals(19827, p.getWeightGrams());
        p.restoreStock(56);
        assertEquals(112, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-5));
        assertEquals(112, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-3));
        assertEquals(112, p.getStock());
        p.restoreStock(19);
        assertEquals(131, p.getStock());
    }

    @Test
    void sample74() {
        var p = new Product("p194144", "sku347214", 854730L, 710, 76);
        var item = new CartItem(p, 50);
        assertEquals("p194144", item.getProduct().getId());
        assertEquals(50, item.getQuantity());
        assertEquals(42736500L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a293358", "sku868142", 543944L, 1590, 57);
        var b = new Product("b413384", "sku601920", 629680L, 1570, 78);
        cart.addItem(a, 16);
        assertEquals(8703104L, cart.getSubtotal());
        assertEquals(25440, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a293358", cart.getItems().get(0).getProduct().getId());
        assertEquals(16, cart.getItems().get(0).getQuantity());
        assertEquals(8703104L, cart.getItems().get(0).getLineTotal());
        cart.updateQuantity("a293358", 38);
        assertEquals(20669872L, cart.getSubtotal());
        assertEquals(60420, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a293358", cart.getItems().get(0).getProduct().getId());
        assertEquals(38, cart.getItems().get(0).getQuantity());
        assertEquals(20669872L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other770814"));
        assertEquals(20669872L, cart.getSubtotal());
        assertEquals(60420, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a293358", cart.getItems().get(0).getProduct().getId());
        assertEquals(38, cart.getItems().get(0).getQuantity());
        assertEquals(20669872L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b413384"));
        assertEquals(20669872L, cart.getSubtotal());
        assertEquals(60420, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a293358", cart.getItems().get(0).getProduct().getId());
        assertEquals(38, cart.getItems().get(0).getQuantity());
        assertEquals(20669872L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 14);
        assertEquals(29485392L, cart.getSubtotal());
        assertEquals(82400, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a293358", cart.getItems().get(0).getProduct().getId());
        assertEquals(38, cart.getItems().get(0).getQuantity());
        assertEquals(20669872L, cart.getItems().get(0).getLineTotal());
        assertEquals("b413384", cart.getItems().get(1).getProduct().getId());
        assertEquals(14, cart.getItems().get(1).getQuantity());
        assertEquals(8815520L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample76() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(null, CouponType.FIXED, 103831L, 180343L, 174463L, LocalDate.parse("2027-10-03")));
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code123553", CouponType.FIXED, 138076L, 468291L, 35024L, LocalDate.parse("2027-01-01"));
        assertEquals(138076L, new PricingService().calculateDiscount(851552L, coupon, LocalDate.parse("2026-11-13")));
    }

    @Test
    void sample78() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 7536, 1217203L));
    }

    @Test
    void sample79() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p457546", "sku718452", 904527L, 1345, 16);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order233414", null, null, null, LocalDateTime.parse("2027-08-25T00:43")));
        assertEquals(16, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(16, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(16, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(16, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(16, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown71351"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown662365"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message475910").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message475910").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message475910").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        var p = new Product("p6138", "sku389581", 772368L, 12069, 26);
        assertEquals("p6138", p.getId());
        assertEquals("sku389581", p.getName());
        assertEquals(772368L, p.getPrice());
        assertEquals(12069, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(35));
        assertEquals(26, p.getStock());
        p.restoreStock(9);
        assertEquals(35, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(42));
        assertEquals(35, p.getStock());
        p.restoreStock(19);
        assertEquals(54, p.getStock());
    }

    @Test
    void sample82() {
        var p = new Product("p213078", "sku956550", 521996L, 1186, 71);
        var item = new CartItem(p, 25);
        assertEquals("p213078", item.getProduct().getId());
        assertEquals(25, item.getQuantity());
        assertEquals(13049900L, item.getLineTotal());
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a115600", "sku818729", 226114L, 1407, 15);
        var b = new Product("b164092", "sku23347", 438872L, 578, 32);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other429460"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b164092"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b164092", 57));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other481977"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 10);
        assertEquals(4388720L, cart.getSubtotal());
        assertEquals(5780, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b164092", cart.getItems().get(0).getProduct().getId());
        assertEquals(10, cart.getItems().get(0).getQuantity());
        assertEquals(4388720L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample84() {
        var coupon = new Coupon("code669706", CouponType.PERCENT, 77L, 65003L, 165377L, LocalDate.parse("2027-11-06"));
        assertEquals("code669706", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(77L, coupon.getValue());
        assertEquals(65003L, coupon.getMinOrder());
        assertEquals(165377L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-11-06"), coupon.getExpiry());
    }

    @Test
    void sample85() {
        var coupon = new Coupon("code421136", CouponType.PERCENT, 45L, 361337L, 235021L, LocalDate.parse("2025-10-26"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(475707L, coupon, LocalDate.parse("2025-11-04")));
    }

    @Test
    void sample86() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.SUBURB, 17120, 725162L));
    }

    @Test
    void sample87() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p972936", "sku565583", 73785L, 1024, 78);
        cart.addItem(p, 8);
        var coupon = new Coupon("code456101", CouponType.PERCENT, 76L, 550553L, 204381L, LocalDate.parse("2025-11-29"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order203513", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2026-01-15T13:27")));
        assertEquals(78, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-01-15T06:17")));
        assertEquals(78, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(78, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-01-16T18:17")));
        assertEquals(78, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(78, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-01-17T10:00")));
        assertEquals(78, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown246458"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message442638").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message442638").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message442638").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        var p = new Product("p420965", "sku352549", 289163L, 2295, 43);
        assertEquals("p420965", p.getId());
        assertEquals("sku352549", p.getName());
        assertEquals(289163L, p.getPrice());
        assertEquals(2295, p.getWeightGrams());
        p.reduceStock(3);
        assertEquals(40, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-3));
        assertEquals(40, p.getStock());
        p.reduceStock(19);
        assertEquals(21, p.getStock());
        p.restoreStock(6);
        assertEquals(27, p.getStock());
        p.restoreStock(42);
        assertEquals(69, p.getStock());
    }

    @Test
    void sample90() {
        var p = new Product("p776495", "sku913257", 849894L, 891, 21);
        var item = new CartItem(p, 35);
        assertEquals("p776495", item.getProduct().getId());
        assertEquals(35, item.getQuantity());
        assertEquals(29746290L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a777457", "sku778133", 684757L, 140, 59);
        var b = new Product("b110427", "sku799125", 329324L, 1201, 48);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a777457"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 36);
        assertEquals(11855664L, cart.getSubtotal());
        assertEquals(43236, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b110427", cart.getItems().get(0).getProduct().getId());
        assertEquals(36, cart.getItems().get(0).getQuantity());
        assertEquals(11855664L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 15);
        assertEquals(22127019L, cart.getSubtotal());
        assertEquals(45336, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b110427", cart.getItems().get(0).getProduct().getId());
        assertEquals(36, cart.getItems().get(0).getQuantity());
        assertEquals(11855664L, cart.getItems().get(0).getLineTotal());
        assertEquals("a777457", cart.getItems().get(1).getProduct().getId());
        assertEquals(15, cart.getItems().get(1).getQuantity());
        assertEquals(10271355L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity(null, 13));
        assertEquals(22127019L, cart.getSubtotal());
        assertEquals(45336, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b110427", cart.getItems().get(0).getProduct().getId());
        assertEquals(36, cart.getItems().get(0).getQuantity());
        assertEquals(11855664L, cart.getItems().get(0).getLineTotal());
        assertEquals("a777457", cart.getItems().get(1).getProduct().getId());
        assertEquals(15, cart.getItems().get(1).getQuantity());
        assertEquals(10271355L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 36));
        assertEquals(22127019L, cart.getSubtotal());
        assertEquals(45336, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b110427", cart.getItems().get(0).getProduct().getId());
        assertEquals(36, cart.getItems().get(0).getQuantity());
        assertEquals(11855664L, cart.getItems().get(0).getLineTotal());
        assertEquals("a777457", cart.getItems().get(1).getProduct().getId());
        assertEquals(15, cart.getItems().get(1).getQuantity());
        assertEquals(10271355L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 40));
        assertEquals(22127019L, cart.getSubtotal());
        assertEquals(45336, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b110427", cart.getItems().get(0).getProduct().getId());
        assertEquals(36, cart.getItems().get(0).getQuantity());
        assertEquals(11855664L, cart.getItems().get(0).getLineTotal());
        assertEquals("a777457", cart.getItems().get(1).getProduct().getId());
        assertEquals(15, cart.getItems().get(1).getQuantity());
        assertEquals(10271355L, cart.getItems().get(1).getLineTotal());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.updateQuantity("a777457", 54));
        assertEquals(22127019L, cart.getSubtotal());
        assertEquals(45336, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b110427", cart.getItems().get(0).getProduct().getId());
        assertEquals(36, cart.getItems().get(0).getQuantity());
        assertEquals(11855664L, cart.getItems().get(0).getLineTotal());
        assertEquals("a777457", cart.getItems().get(1).getProduct().getId());
        assertEquals(15, cart.getItems().get(1).getQuantity());
        assertEquals(10271355L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample92() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon("code433006", CouponType.PERCENT, -1L, 399050L, 31752L, LocalDate.parse("2025-10-21")));
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code717678", CouponType.PERCENT, 24L, 283056L, 282167L, LocalDate.parse("2025-07-27"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(208451L, coupon, LocalDate.parse("2025-08-19")));
    }

    @Test
    void sample94() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 5385, 629522L));
    }

    @Test
    void sample95() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p104350", "sku330396", 353695L, 615, 39);
        cart.addItem(p, 7);
        var order = service.placeOrder("order585514", cart, null, Region.PROVINCE, LocalDateTime.parse("2027-03-31T10:53"));
        assertEquals(32, p.getStock());
        assertEquals("order585514", order.getId());
        assertEquals(2475865L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(49000L, order.getShipping());
        assertEquals(2524865L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-03-31T10:53"), order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p104350", order.getItems().get(0).getProduct().getId());
        assertEquals(7, order.getItems().get(0).getQuantity());
        assertEquals(2475865L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(32, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(32, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(32, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(32, p.getStock());
    }

    @Test
    void sample96() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown101897"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown347027"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message55257").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message55257").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message55257").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        var p = new Product("p442698", "sku191315", 814823L, 3247, 24);
        assertEquals("p442698", p.getId());
        assertEquals("sku191315", p.getName());
        assertEquals(814823L, p.getPrice());
        assertEquals(3247, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(26));
        assertEquals(24, p.getStock());
        p.reduceStock(12);
        assertEquals(12, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-5));
        assertEquals(12, p.getStock());
        p.restoreStock(5);
        assertEquals(17, p.getStock());
        p.restoreStock(43);
        assertEquals(60, p.getStock());
    }

    @Test
    void sample98() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 36));
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a102742", "sku154032", 974646L, 1572, 14);
        var b = new Product("b706505", "sku429502", 763309L, 483, 18);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("other984328", 27));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 8);
        assertEquals(6106472L, cart.getSubtotal());
        assertEquals(3864, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b706505", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(6106472L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a102742", 50));
        assertEquals(6106472L, cart.getSubtotal());
        assertEquals(3864, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b706505", cart.getItems().get(0).getProduct().getId());
        assertEquals(8, cart.getItems().get(0).getQuantity());
        assertEquals(6106472L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("b706505");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a102742"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b706505"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other456230"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample100() {
        var coupon = new Coupon("code347420", CouponType.PERCENT, 57L, 12191L, 69819L, LocalDate.parse("2024-04-25"));
        assertEquals("code347420", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(57L, coupon.getValue());
        assertEquals(12191L, coupon.getMinOrder());
        assertEquals(69819L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-04-25"), coupon.getExpiry());
    }
}
