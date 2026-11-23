package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RandomShopTest {

    @Test
    void sample1() {
        var p = new Product("p819257", "sku550607", 251395L, 20924, 19);
        assertEquals("p819257", p.getId());
        assertEquals("sku550607", p.getName());
        assertEquals(251395L, p.getPrice());
        assertEquals(20924, p.getWeightGrams());
        p.restoreStock(32);
        assertEquals(51, p.getStock());
        p.restoreStock(15);
        assertEquals(66, p.getStock());
        p.restoreStock(11);
        assertEquals(77, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-4));
        assertEquals(77, p.getStock());
        p.reduceStock(22);
        assertEquals(55, p.getStock());
    }

    @Test
    void sample2() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 33));
    }

    @Test
    void sample3() {
        var cart = new Cart();
        var a = new Product("a345875", "sku221984", 875954L, 1311, 61);
        var b = new Product("b953663", "sku993113", 397338L, 1265, 20);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a345875", 29));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 56));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b953663", 17));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 44);
        assertEquals(38541976L, cart.getSubtotal());
        assertEquals(57684, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a345875", cart.getItems().get(0).getProduct().getId());
        assertEquals(44, cart.getItems().get(0).getQuantity());
        assertEquals(38541976L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("a345875");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample4() {
        var coupon = new Coupon("code526512", CouponType.PERCENT, 79L, 50637L, 148567L, LocalDate.parse("2027-04-06"));
        assertEquals("code526512", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(79L, coupon.getValue());
        assertEquals(50637L, coupon.getMinOrder());
        assertEquals(148567L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-04-06"), coupon.getExpiry());
    }

    @Test
    void sample5() {
        var coupon = new Coupon("code900956", CouponType.FIXED, 348403L, 34447L, 53266L, LocalDate.parse("2027-04-05"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(232613L, coupon, LocalDate.parse("2027-05-03")));
    }

    @Test
    void sample6() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 5598, 1224067L));
    }

    @Test
    void sample7() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p236002", "sku606880", 689149L, 518, 60);
        cart.addItem(p, 3);
        var order = service.placeOrder("order506348", cart, null, Region.SUBURB, LocalDateTime.parse("2027-05-17T13:20"));
        assertEquals(57, p.getStock());
        assertEquals("order506348", order.getId());
        assertEquals(2067447L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(2067447L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-05-17T13:20"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p236002", order.getItems().get(0).getProduct().getId());
        assertEquals(3, order.getItems().get(0).getQuantity());
        assertEquals(2067447L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(57, p.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(57, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(57, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(57, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(57, p.getStock());
    }

    @Test
    void sample8() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown483114"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown758193"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message86633").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message86633").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message86633").getClass().getSimpleName());
    }

    @Test
    void sample9() {
        var p = new Product("p447251", "sku936061", 413818L, 6218, 39);
        assertEquals("p447251", p.getId());
        assertEquals("sku936061", p.getName());
        assertEquals(413818L, p.getPrice());
        assertEquals(6218, p.getWeightGrams());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(-1));
        assertEquals(39, p.getStock());
        p.restoreStock(10);
        assertEquals(49, p.getStock());
        p.restoreStock(2);
        assertEquals(51, p.getStock());
    }

    @Test
    void sample10() {
        var p = new Product("p586070", "sku72597", 708119L, 308, 21);
        var item = new CartItem(p, 53);
        assertEquals("p586070", item.getProduct().getId());
        assertEquals(53, item.getQuantity());
        assertEquals(37530307L, item.getLineTotal());
    }

    @Test
    void sample11() {
        var cart = new Cart();
        var a = new Product("a56144", "sku193600", 100060L, 1531, 75);
        var b = new Product("b386428", "sku783880", 915414L, 857, 15);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a56144", 29));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 28));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a56144", 12));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b386428"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(a, 40);
        assertEquals(4002400L, cart.getSubtotal());
        assertEquals(61240, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a56144", cart.getItems().get(0).getProduct().getId());
        assertEquals(40, cart.getItems().get(0).getQuantity());
        assertEquals(4002400L, cart.getItems().get(0).getLineTotal());
    }

    @Test
    void sample12() {
        var coupon = new Coupon("code103596", CouponType.FIXED, 323865L, 322848L, 35431L, LocalDate.parse("2026-10-13"));
        assertEquals("code103596", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(323865L, coupon.getValue());
        assertEquals(322848L, coupon.getMinOrder());
        assertEquals(35431L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-10-13"), coupon.getExpiry());
    }

    @Test
    void sample13() {
        var coupon = new Coupon("code596383", CouponType.FIXED, 373787L, 595277L, 197506L, LocalDate.parse("2024-09-27"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1244921L, coupon, LocalDate.parse("2024-09-30")));
    }

    @Test
    void sample14() {
        assertEquals(75000L, new ShippingCalculator().calculate(Region.REMOTE, 5965, 1148068L));
    }

    @Test
    void sample15() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p932977", "sku904312", 112759L, 892, 22);
        cart.addItem(p, 18);
        var coupon = new Coupon("code508147", CouponType.FIXED, 264628L, 2063L, 226394L, LocalDate.parse("2027-03-18"));
        var order = service.placeOrder("order822222", cart, coupon, Region.SUBURB, LocalDateTime.parse("2027-02-28T12:16"));
        assertEquals(4, p.getStock());
        assertEquals("order822222", order.getId());
        assertEquals(2029662L, order.getSubtotal());
        assertEquals(264628L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(1765034L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-02-28T12:16"), order.getCreatedAt());
        assertEquals(Region.SUBURB, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p932977", order.getItems().get(0).getProduct().getId());
        assertEquals(18, order.getItems().get(0).getQuantity());
        assertEquals(2029662L, order.getItems().get(0).getLineTotal());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(4, p.getStock());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2027-03-03T21:46")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(22, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(22, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(22, p.getStock());
    }

    @Test
    void sample16() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown160399"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message866486").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message866486").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message866486").getClass().getSimpleName());
    }

    @Test
    void sample17() {
        var p = new Product("p758527", "sku618493", 44538L, 18398, 12);
        assertEquals("p758527", p.getId());
        assertEquals("sku618493", p.getName());
        assertEquals(44538L, p.getPrice());
        assertEquals(18398, p.getWeightGrams());
        p.restoreStock(42);
        assertEquals(54, p.getStock());
        p.reduceStock(51);
        assertEquals(3, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(40));
        assertEquals(3, p.getStock());
        p.restoreStock(15);
        assertEquals(18, p.getStock());
    }

    @Test
    void sample18() {
        var p = new Product("p676325", "sku92692", 376632L, 982, 52);
        var item = new CartItem(p, 60);
        assertEquals("p676325", item.getProduct().getId());
        assertEquals(60, item.getQuantity());
        assertEquals(22597920L, item.getLineTotal());
    }

    @Test
    void sample19() {
        var cart = new Cart();
        var a = new Product("a273533", "sku854293", 627327L, 128, 25);
        var b = new Product("b511309", "sku648784", 781434L, 1229, 28);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b511309", 56));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b511309", 3));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b511309", 32));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        cart.addItem(b, 21);
        assertEquals(16410114L, cart.getSubtotal());
        assertEquals(25809, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b511309", cart.getItems().get(0).getProduct().getId());
        assertEquals(21, cart.getItems().get(0).getQuantity());
        assertEquals(16410114L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a273533", 34));
        assertEquals(16410114L, cart.getSubtotal());
        assertEquals(25809, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b511309", cart.getItems().get(0).getProduct().getId());
        assertEquals(21, cart.getItems().get(0).getQuantity());
        assertEquals(16410114L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 9);
        assertEquals(22056057L, cart.getSubtotal());
        assertEquals(26961, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b511309", cart.getItems().get(0).getProduct().getId());
        assertEquals(21, cart.getItems().get(0).getQuantity());
        assertEquals(16410114L, cart.getItems().get(0).getLineTotal());
        assertEquals("a273533", cart.getItems().get(1).getProduct().getId());
        assertEquals(9, cart.getItems().get(1).getQuantity());
        assertEquals(5645943L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample20() {
        var coupon = new Coupon("code147424", CouponType.FIXED, 247749L, 556280L, 246968L, LocalDate.parse("2025-01-03"));
        assertEquals("code147424", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(247749L, coupon.getValue());
        assertEquals(556280L, coupon.getMinOrder());
        assertEquals(246968L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-01-03"), coupon.getExpiry());
    }

    @Test
    void sample21() {
        var coupon = new Coupon("code146062", CouponType.PERCENT, 13L, 436605L, 62014L, LocalDate.parse("2025-03-20"));
        assertThrows(shop.CouponNotApplicableException.class, () -> new PricingService().calculateDiscount(380537L, coupon, LocalDate.parse("2025-03-19")));
    }

    @Test
    void sample22() {
        assertEquals(43000L, new ShippingCalculator().calculate(Region.PROVINCE, 2647, 326189L));
    }

    @Test
    void sample23() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample24() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown288999"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message456844").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message456844").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message456844").getClass().getSimpleName());
    }

    @Test
    void sample25() {
        var p = new Product("p250196", "sku537518", 281172L, 19865, 15);
        assertEquals("p250196", p.getId());
        assertEquals("sku537518", p.getName());
        assertEquals(281172L, p.getPrice());
        assertEquals(19865, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(30));
        assertEquals(15, p.getStock());
        p.restoreStock(39);
        assertEquals(54, p.getStock());
        p.reduceStock(18);
        assertEquals(36, p.getStock());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(40));
        assertEquals(36, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-5));
        assertEquals(36, p.getStock());
    }

    @Test
    void sample26() {
        var p = new Product("p331629", "sku191918", 642283L, 1472, 9);
        var item = new CartItem(p, 35);
        assertEquals("p331629", item.getProduct().getId());
        assertEquals(35, item.getQuantity());
        assertEquals(22479905L, item.getLineTotal());
    }

    @Test
    void sample27() {
        var cart = new Cart();
        var a = new Product("a359101", "sku211805", 667858L, 253, 15);
        var b = new Product("b940314", "sku10529", 805102L, 1263, 23);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 57));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a359101", 24));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b940314"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample28() {
        var coupon = new Coupon("code134614", CouponType.PERCENT, 18L, 27859L, 280659L, LocalDate.parse("2027-09-13"));
        assertEquals("code134614", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(18L, coupon.getValue());
        assertEquals(27859L, coupon.getMinOrder());
        assertEquals(280659L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-09-13"), coupon.getExpiry());
    }

    @Test
    void sample29() {
        var coupon = new Coupon("code288976", CouponType.PERCENT, 57L, 161271L, 29586L, LocalDate.parse("2025-09-21"));
        assertEquals(29586L, new PricingService().calculateDiscount(1171779L, coupon, LocalDate.parse("2025-09-06")));
    }

    @Test
    void sample30() {
        assertEquals(15000L, new ShippingCalculator().calculate(Region.INNER_CITY, 641, 186259L));
    }

    @Test
    void sample31() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p622241", "sku165058", 124044L, 1072, 27);
        cart.addItem(p, 2);
        var order = service.placeOrder("order535235", cart, null, Region.INNER_CITY, LocalDateTime.parse("2026-02-11T08:35"));
        assertEquals(25, p.getStock());
        assertEquals("order535235", order.getId());
        assertEquals(248088L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(21000L, order.getShipping());
        assertEquals(269088L, order.getTotal());
        assertEquals(LocalDateTime.parse("2026-02-11T08:35"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p622241", order.getItems().get(0).getProduct().getId());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(248088L, order.getItems().get(0).getLineTotal());
        assertEquals(0L, service.cancelOrder(order, LocalDateTime.parse("2026-02-14T23:46")));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(27, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(27, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(27, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(27, p.getStock());
    }

    @Test
    void sample32() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf(" "));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message302853").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message302853").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message302853").getClass().getSimpleName());
    }

    @Test
    void sample33() {
        var p = new Product("p909076", "sku372791", 443076L, 2986, 38);
        assertEquals("p909076", p.getId());
        assertEquals("sku372791", p.getName());
        assertEquals(443076L, p.getPrice());
        assertEquals(2986, p.getWeightGrams());
        assertThrows(shop.InsufficientStockException.class, () -> p.reduceStock(44));
        assertEquals(38, p.getStock());
        p.reduceStock(4);
        assertEquals(34, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.reduceStock(-3));
        assertEquals(34, p.getStock());
        p.restoreStock(51);
        assertEquals(85, p.getStock());
    }

    @Test
    void sample34() {
        var p = new Product("p205530", "sku418812", 926168L, 454, 23);
        var item = new CartItem(p, 12);
        assertEquals("p205530", item.getProduct().getId());
        assertEquals(12, item.getQuantity());
        assertEquals(11114016L, item.getLineTotal());
    }

    @Test
    void sample35() {
        var cart = new Cart();
        var a = new Product("a24637", "sku276588", 272972L, 1150, 14);
        var b = new Product("b145803", "sku251843", 38605L, 1095, 5);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a24637"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b145803"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b145803"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other465241"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b145803"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample36() {
        var coupon = new Coupon("code850199", CouponType.FIXED, 135422L, 446407L, 250683L, LocalDate.parse("2027-02-01"));
        assertEquals("code850199", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(135422L, coupon.getValue());
        assertEquals(446407L, coupon.getMinOrder());
        assertEquals(250683L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-02-01"), coupon.getExpiry());
    }

    @Test
    void sample37() {
        var coupon = new Coupon("code61897", CouponType.FIXED, 212816L, 157064L, 143789L, LocalDate.parse("2026-04-22"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(1138137L, coupon, LocalDate.parse("2026-06-15")));
    }

    @Test
    void sample38() {
        assertEquals(23000L, new ShippingCalculator().calculate(Region.INNER_CITY, 2634, 433189L));
    }

    @Test
    void sample39() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p821166", "sku9257", 43085L, 1092, 44);
        cart.addItem(p, 4);
        var coupon = new Coupon("code444690", CouponType.PERCENT, 94L, 174890L, 37673L, LocalDate.parse("2024-04-09"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order20346", cart, coupon, Region.SUBURB, null));
        assertEquals(44, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(44, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2024-02-13T22:46")));
        assertEquals(44, p.getStock());
    }

    @Test
    void sample40() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown264016"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message731805").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message731805").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message731805").getClass().getSimpleName());
    }

    @Test
    void sample41() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p268071", " ", 635891L, 23754, 45));
    }

    @Test
    void sample42() {
        var p = new Product("p14625", "sku390592", 419570L, 344, 53);
        var item = new CartItem(p, 53);
        assertEquals("p14625", item.getProduct().getId());
        assertEquals(53, item.getQuantity());
        assertEquals(22237210L, item.getLineTotal());
    }

    @Test
    void sample43() {
        var cart = new Cart();
        var a = new Product("a945034", "sku783911", 825957L, 50, 43);
        var b = new Product("b170058", "sku935476", 919562L, 1048, 58);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("a945034", 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a945034"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 54));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(b, 53));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample44() {
        var coupon = new Coupon("code302947", CouponType.FIXED, 298943L, 283982L, 297794L, LocalDate.parse("2027-02-06"));
        assertEquals("code302947", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(298943L, coupon.getValue());
        assertEquals(283982L, coupon.getMinOrder());
        assertEquals(297794L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2027-02-06"), coupon.getExpiry());
    }

    @Test
    void sample45() {
        var coupon = new Coupon("code628604", CouponType.PERCENT, 61L, 558647L, 128347L, LocalDate.parse("2025-08-30"));
        assertThrows(shop.CouponNotApplicableException.class, () -> new PricingService().calculateDiscount(189404L, coupon, LocalDate.parse("2025-07-04")));
    }

    @Test
    void sample46() {
        assertEquals(85000L, new ShippingCalculator().calculate(Region.PROVINCE, 13037, 584620L));
    }

    @Test
    void sample47() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p894693", "sku529115", 901168L, 1161, 39);
        cart.addItem(p, 12);
        var coupon = new Coupon("code476774", CouponType.FIXED, 441038L, 47778L, 120273L, LocalDate.parse("2025-10-18"));
        assertThrows(shop.CouponExpiredException.class, () -> service.placeOrder("order181438", cart, coupon, Region.INNER_CITY, LocalDateTime.parse("2025-12-10T08:34")));
        assertEquals(39, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(39, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-12-13T19:22")));
        assertEquals(39, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.ship(null));
        assertEquals(39, p.getStock());
    }

    @Test
    void sample48() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.FIXED, CouponType.values()[1]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown172837"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message191504").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message191504").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message191504").getClass().getSimpleName());
    }

    @Test
    void sample49() {
        var p = new Product("p338987", "sku464120", 14641L, 8488, 54);
        assertEquals("p338987", p.getId());
        assertEquals("sku464120", p.getName());
        assertEquals(14641L, p.getPrice());
        assertEquals(8488, p.getWeightGrams());
        p.reduceStock(39);
        assertEquals(15, p.getStock());
        p.restoreStock(47);
        assertEquals(62, p.getStock());
        p.restoreStock(58);
        assertEquals(120, p.getStock());
        p.reduceStock(6);
        assertEquals(114, p.getStock());
    }

    @Test
    void sample50() {
        var p = new Product("p491553", "sku505287", 155506L, 715, 13);
        var item = new CartItem(p, 23);
        assertEquals("p491553", item.getProduct().getId());
        assertEquals(23, item.getQuantity());
        assertEquals(3576638L, item.getLineTotal());
    }

    @Test
    void sample51() {
        var cart = new Cart();
        var a = new Product("a95948", "sku345162", 644388L, 317, 71);
        var b = new Product("b137399", "sku312270", 48822L, 1171, 27);
        cart.addItem(a, 27);
        assertEquals(17398476L, cart.getSubtotal());
        assertEquals(8559, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a95948", cart.getItems().get(0).getProduct().getId());
        assertEquals(27, cart.getItems().get(0).getQuantity());
        assertEquals(17398476L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b137399"));
        assertEquals(17398476L, cart.getSubtotal());
        assertEquals(8559, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a95948", cart.getItems().get(0).getProduct().getId());
        assertEquals(27, cart.getItems().get(0).getQuantity());
        assertEquals(17398476L, cart.getItems().get(0).getLineTotal());
        cart.removeItem("a95948");
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample52() {
        var coupon = new Coupon("code665961", CouponType.PERCENT, 13L, 315500L, 224874L, LocalDate.parse("2024-09-10"));
        assertEquals("code665961", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(13L, coupon.getValue());
        assertEquals(315500L, coupon.getMinOrder());
        assertEquals(224874L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-09-10"), coupon.getExpiry());
    }

    @Test
    void sample53() {
        var coupon = new Coupon("code536125", CouponType.FIXED, 414000L, 140056L, 60409L, LocalDate.parse("2027-11-25"));
        assertEquals(359339L, new PricingService().calculateDiscount(359339L, coupon, LocalDate.parse("2027-09-29")));
    }

    @Test
    void sample54() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(Region.PROVINCE, 22581, 1187377L));
    }

    @Test
    void sample55() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample56() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.INNER_CITY, Region.valueOf("INNER_CITY"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown573582"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message943223").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message943223").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message943223").getClass().getSimpleName());
    }

    @Test
    void sample57() {
        var p = new Product("p325125", "sku266107", 500398L, 6523, 6);
        assertEquals("p325125", p.getId());
        assertEquals("sku266107", p.getName());
        assertEquals(500398L, p.getPrice());
        assertEquals(6523, p.getWeightGrams());
        p.restoreStock(26);
        assertEquals(32, p.getStock());
        p.reduceStock(10);
        assertEquals(22, p.getStock());
    }

    @Test
    void sample58() {
        var p = new Product("p799532", "sku859031", 703091L, 1306, 29);
        var item = new CartItem(p, 10);
        assertEquals("p799532", item.getProduct().getId());
        assertEquals(10, item.getQuantity());
        assertEquals(7030910L, item.getLineTotal());
    }

    @Test
    void sample59() {
        var cart = new Cart();
        var a = new Product("a657373", "sku812813", 867520L, 105, 20);
        var b = new Product("b110057", "sku850151", 844341L, 909, 25);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 50));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(a, -5));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other777721"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b110057"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 49));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample60() {
        var coupon = new Coupon("code771951", CouponType.FIXED, 567121L, 400578L, 210931L, LocalDate.parse("2026-04-25"));
        assertEquals("code771951", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(567121L, coupon.getValue());
        assertEquals(400578L, coupon.getMinOrder());
        assertEquals(210931L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-04-25"), coupon.getExpiry());
    }

    @Test
    void sample61() {
        var coupon = new Coupon("code184496", CouponType.FIXED, 586548L, 34323L, 132238L, LocalDate.parse("2024-10-31"));
        assertThrows(shop.CouponExpiredException.class, () -> new PricingService().calculateDiscount(409146L, coupon, LocalDate.parse("2024-11-02")));
    }

    @Test
    void sample62() {
        assertEquals(53000L, new ShippingCalculator().calculate(Region.PROVINCE, 5069, 948353L));
    }

    @Test
    void sample63() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p375212", "sku514144", 401663L, 259, 4);
        cart.addItem(p, 4);
        var coupon = new Coupon("code500498", CouponType.PERCENT, 76L, 177073L, 157192L, LocalDate.parse("2025-02-19"));
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order563601", cart, coupon, Region.INNER_CITY, null));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2025-04-01T04:00")));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(4, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(4, p.getStock());
    }

    @Test
    void sample64() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.REMOTE, Region.valueOf("REMOTE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown978592"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.values()[3]);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message863297").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message863297").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message863297").getClass().getSimpleName());
    }

    @Test
    void sample65() {
        var p = new Product("p634324", "sku93030", 361507L, 3062, 61);
        assertEquals("p634324", p.getId());
        assertEquals("sku93030", p.getName());
        assertEquals(361507L, p.getPrice());
        assertEquals(3062, p.getWeightGrams());
        p.restoreStock(42);
        assertEquals(103, p.getStock());
        p.restoreStock(4);
        assertEquals(107, p.getStock());
    }

    @Test
    void sample66() {
        var p = new Product("p306589", "sku625335", 606333L, 562, 55);
        var item = new CartItem(p, 8);
        assertEquals("p306589", item.getProduct().getId());
        assertEquals(8, item.getQuantity());
        assertEquals(4850664L, item.getLineTotal());
    }

    @Test
    void sample67() {
        var cart = new Cart();
        var a = new Product("a610162", "sku730764", 540600L, 1036, 77);
        var b = new Product("b901028", "sku289826", 636836L, 1388, 19);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b901028"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b901028"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("other804992"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a610162"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample68() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", CouponType.FIXED, 525984L, 15155L, 39665L, LocalDate.parse("2025-06-11")));
    }

    @Test
    void sample69() {
        var coupon = new Coupon("code879678", CouponType.FIXED, 57784L, 25055L, 167836L, LocalDate.parse("2026-11-01"));
        assertEquals(57784L, new PricingService().calculateDiscount(465385L, coupon, LocalDate.parse("2026-10-20")));
    }

    @Test
    void sample70() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.SUBURB, 10811, 1167246L));
    }

    @Test
    void sample71() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p239513", "sku407428", 476531L, 495, 37);
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.placeOrder("order897263", null, null, Region.PROVINCE, null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(37, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.pay(null));
        assertEquals(37, p.getStock());
    }

    @Test
    void sample72() {
        assertEquals(Region.SUBURB, Region.values()[1]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown927428"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message764278").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message764278").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message764278").getClass().getSimpleName());
    }

    @Test
    void sample73() {
        var p = new Product("p364796", "sku278371", 891441L, 3023, 47);
        assertEquals("p364796", p.getId());
        assertEquals("sku278371", p.getName());
        assertEquals(891441L, p.getPrice());
        assertEquals(3023, p.getWeightGrams());
        p.reduceStock(2);
        assertEquals(45, p.getStock());
        p.restoreStock(24);
        assertEquals(69, p.getStock());
        p.restoreStock(6);
        assertEquals(75, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> p.restoreStock(0));
        assertEquals(75, p.getStock());
        p.reduceStock(27);
        assertEquals(48, p.getStock());
    }

    @Test
    void sample74() {
        var p = new Product("p5494", "sku78150", 255370L, 928, 58);
        var item = new CartItem(p, 36);
        assertEquals("p5494", item.getProduct().getId());
        assertEquals(36, item.getQuantity());
        assertEquals(9193320L, item.getLineTotal());
    }

    @Test
    void sample75() {
        var cart = new Cart();
        var a = new Product("a547726", "sku348057", 748643L, 807, 4);
        var b = new Product("b212047", "sku206606", 187016L, 948, 13);
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 39));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 9));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 47));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 2));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 33));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(a, 19));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(shop.InsufficientStockException.class, () -> cart.addItem(b, 22));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample76() {
        var coupon = new Coupon("code71479", CouponType.PERCENT, 34L, 258512L, 185573L, LocalDate.parse("2026-09-20"));
        assertEquals("code71479", coupon.getCode());
        assertEquals(CouponType.PERCENT, coupon.getType());
        assertEquals(34L, coupon.getValue());
        assertEquals(258512L, coupon.getMinOrder());
        assertEquals(185573L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2026-09-20"), coupon.getExpiry());
    }

    @Test
    void sample77() {
        var coupon = new Coupon("code824683", CouponType.FIXED, 478515L, 305602L, 301L, LocalDate.parse("2024-08-12"));
        assertEquals(478515L, new PricingService().calculateDiscount(1144344L, coupon, LocalDate.parse("2024-06-28")));
    }

    @Test
    void sample78() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 19921, 274332L));
    }

    @Test
    void sample79() {
        var service = new OrderService(new PricingService(), new ShippingCalculator());
        var cart = new Cart();
        var p = new Product("p378230", "sku264230", 748770L, 1221, 62);
        cart.addItem(p, 7);
        var order = service.placeOrder("order863796", cart, null, Region.INNER_CITY, LocalDateTime.parse("2027-04-02T01:17"));
        assertEquals(55, p.getStock());
        assertEquals("order863796", order.getId());
        assertEquals(5241390L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(5241390L, order.getTotal());
        assertEquals(LocalDateTime.parse("2027-04-02T01:17"), order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p378230", order.getItems().get(0).getProduct().getId());
        assertEquals(7, order.getItems().get(0).getQuantity());
        assertEquals(5241390L, order.getItems().get(0).getLineTotal());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(55, p.getStock());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(55, p.getStock());
        assertThrows(java.lang.IllegalStateException.class, () -> service.cancelOrder(order, LocalDateTime.parse("2027-04-03T01:28")));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(55, p.getStock());
    }

    @Test
    void sample80() {
        assertEquals(Region.PROVINCE, Region.values()[2]);
        assertEquals(Region.PROVINCE, Region.valueOf("PROVINCE"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.FIXED, CouponType.valueOf("FIXED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.values()[2]);
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message848592").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message848592").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message848592").getClass().getSimpleName());
    }

    @Test
    void sample81() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p589989", " ", 75377L, 10716, 44));
    }

    @Test
    void sample82() {
        var p = new Product("p448823", "sku639793", 919168L, 1562, 6);
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(p, -5));
    }

    @Test
    void sample83() {
        var cart = new Cart();
        var a = new Product("a396109", "sku469183", 975290L, 101, 9);
        var b = new Product("b815113", "sku715612", 860108L, 684, 18);
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b815113"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b815113", 24));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.updateQuantity("b815113", 55));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample84() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Coupon(" ", null, 438869L, 86799L, 13294L, LocalDate.parse("2024-08-15")));
    }

    @Test
    void sample85() {
        assertEquals(0L, new PricingService().calculateDiscount(397432L, null, LocalDate.parse("2024-04-12")));
    }

    @Test
    void sample86() {
        assertEquals(0L, new ShippingCalculator().calculate(Region.INNER_CITY, 17355, 884889L));
    }

    @Test
    void sample87() {
        var service = new OrderService();
        var cart = new Cart();
        var p = new Product("p574054", "sku739869", 851642L, 409, 31);
        assertThrows(java.lang.IllegalStateException.class, () -> service.placeOrder("order137127", null, null, Region.SUBURB, LocalDateTime.parse("2026-03-19T13:17")));
        assertEquals(31, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-03-20T05:52")));
        assertEquals(31, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-03-19T15:30")));
        assertEquals(31, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-03-19T06:32")));
        assertEquals(31, p.getStock());
        assertThrows(java.lang.IllegalArgumentException.class, () -> service.cancelOrder(null, LocalDateTime.parse("2026-03-21T06:41")));
        assertEquals(31, p.getStock());
    }

    @Test
    void sample88() {
        assertEquals(Region.INNER_CITY, Region.values()[0]);
        assertEquals(Region.SUBURB, Region.valueOf("SUBURB"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertEquals(CouponType.PERCENT, CouponType.valueOf("PERCENT"));
        assertEquals(OrderStatus.PENDING, OrderStatus.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown69641"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message959255").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message959255").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message959255").getClass().getSimpleName());
    }

    @Test
    void sample89() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product(" ", "sku620349", 484415L, 4962, 24));
    }

    @Test
    void sample90() {
        var p = new Product("p289363", "sku327761", 558751L, 309, 44);
        var item = new CartItem(p, 11);
        assertEquals("p289363", item.getProduct().getId());
        assertEquals(11, item.getQuantity());
        assertEquals(6146261L, item.getLineTotal());
    }

    @Test
    void sample91() {
        var cart = new Cart();
        var a = new Product("a288030", "sku403804", 247713L, 502, 20);
        var b = new Product("b416088", "sku194646", 93367L, 675, 73);
        cart.addItem(b, 9);
        assertEquals(840303L, cart.getSubtotal());
        assertEquals(6075, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("b416088", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(840303L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 9);
        assertEquals(3069720L, cart.getSubtotal());
        assertEquals(10593, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("b416088", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(840303L, cart.getItems().get(0).getLineTotal());
        assertEquals("a288030", cart.getItems().get(1).getProduct().getId());
        assertEquals(9, cart.getItems().get(1).getQuantity());
        assertEquals(2229417L, cart.getItems().get(1).getLineTotal());
        cart.removeItem("b416088");
        assertEquals(2229417L, cart.getSubtotal());
        assertEquals(4518, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a288030", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(2229417L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b416088"));
        assertEquals(2229417L, cart.getSubtotal());
        assertEquals(4518, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a288030", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(2229417L, cart.getItems().get(0).getLineTotal());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("b416088"));
        assertEquals(2229417L, cart.getSubtotal());
        assertEquals(4518, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a288030", cart.getItems().get(0).getProduct().getId());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(2229417L, cart.getItems().get(0).getLineTotal());
        cart.addItem(a, 9);
        assertEquals(4458834L, cart.getSubtotal());
        assertEquals(9036, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals("a288030", cart.getItems().get(0).getProduct().getId());
        assertEquals(18, cart.getItems().get(0).getQuantity());
        assertEquals(4458834L, cart.getItems().get(0).getLineTotal());
        cart.addItem(b, 44);
        assertEquals(8566982L, cart.getSubtotal());
        assertEquals(38736, cart.getTotalWeightGrams());
        assertEquals(false, cart.isEmpty());
        assertEquals(2, cart.getItems().size());
        assertEquals("a288030", cart.getItems().get(0).getProduct().getId());
        assertEquals(18, cart.getItems().get(0).getQuantity());
        assertEquals(4458834L, cart.getItems().get(0).getLineTotal());
        assertEquals("b416088", cart.getItems().get(1).getProduct().getId());
        assertEquals(44, cart.getItems().get(1).getQuantity());
        assertEquals(4108148L, cart.getItems().get(1).getLineTotal());
    }

    @Test
    void sample92() {
        var coupon = new Coupon("code501607", CouponType.FIXED, 509329L, 435893L, 137842L, LocalDate.parse("2025-03-11"));
        assertEquals("code501607", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(509329L, coupon.getValue());
        assertEquals(435893L, coupon.getMinOrder());
        assertEquals(137842L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2025-03-11"), coupon.getExpiry());
    }

    @Test
    void sample93() {
        var coupon = new Coupon("code980413", CouponType.PERCENT, 29L, 122097L, 27150L, LocalDate.parse("2026-08-18"));
        assertEquals(27150L, new PricingService().calculateDiscount(764892L, coupon, LocalDate.parse("2026-06-26")));
    }

    @Test
    void sample94() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new ShippingCalculator().calculate(null, 12103, 1166127L));
    }

    @Test
    void sample95() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new OrderService(null, new ShippingCalculator()));
    }

    @Test
    void sample96() {
        assertEquals(Region.REMOTE, Region.values()[3]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> Region.valueOf("unknown993013"));
        assertEquals(CouponType.PERCENT, CouponType.values()[0]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> CouponType.valueOf("unknown825656"));
        assertEquals(OrderStatus.PAID, OrderStatus.values()[1]);
        assertThrows(java.lang.IllegalArgumentException.class, () -> OrderStatus.valueOf("unknown943620"));
        assertEquals("InsufficientStockException", new InsufficientStockException("message167149").getClass().getSimpleName());
        assertEquals("CouponExpiredException", new CouponExpiredException("message167149").getClass().getSimpleName());
        assertEquals("CouponNotApplicableException", new CouponNotApplicableException("message167149").getClass().getSimpleName());
    }

    @Test
    void sample97() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new Product("p958704", " ", 8980L, 9412, 73));
    }

    @Test
    void sample98() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> new CartItem(null, 45));
    }

    @Test
    void sample99() {
        var cart = new Cart();
        var a = new Product("a13022", "sku93218", 853271L, 1396, 30);
        var b = new Product("b995853", "sku128317", 263253L, 28, 60);
        assertThrows(java.lang.IllegalArgumentException.class, () -> cart.addItem(null, 42));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a13022"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
        assertThrows(java.util.NoSuchElementException.class, () -> cart.removeItem("a13022"));
        assertEquals(0L, cart.getSubtotal());
        assertEquals(0, cart.getTotalWeightGrams());
        assertEquals(true, cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void sample100() {
        var coupon = new Coupon("code148460", CouponType.FIXED, 405222L, 168079L, 167855L, LocalDate.parse("2024-12-24"));
        assertEquals("code148460", coupon.getCode());
        assertEquals(CouponType.FIXED, coupon.getType());
        assertEquals(405222L, coupon.getValue());
        assertEquals(168079L, coupon.getMinOrder());
        assertEquals(167855L, coupon.getMaxDiscount());
        assertEquals(LocalDate.parse("2024-12-24"), coupon.getExpiry());
    }
}
