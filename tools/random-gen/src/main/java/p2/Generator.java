package p2;

import shop.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** Executes each sampled scenario on reference while emitting its concrete JUnit replay. */
public final class Generator {
    private final Random random;
    private final StringBuilder source = new StringBuilder();

    private Generator(long seed) {
        random = new Random(seed);
    }

    private int number(int low, int high) {
        return low + random.nextInt(high - low + 1);
    }

    private boolean sometimes() {
        return random.nextInt(8) == 0;
    }

    private <T> T pick(T[] values) {
        return values[random.nextInt(values.length)];
    }

    private String label(String prefix, boolean allowInvalid) {
        if (allowInvalid && sometimes()) return random.nextBoolean() ? null : " ";
        return prefix + number(1, 999999);
    }

    private static String literal(Object value) {
        if (value == null) return "null";
        if (value instanceof String s) return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        if (value instanceof Long) return value + "L";
        if (value instanceof Enum<?> e) return e.getDeclaringClass().getSimpleName() + "." + e.name();
        if (value instanceof LocalDate || value instanceof LocalDateTime)
            return value.getClass().getSimpleName() + ".parse(\"" + value + "\")";
        if (value instanceof Integer || value instanceof Boolean) return value.toString();
        throw new IllegalArgumentException("Unsupported literal: " + value.getClass());
    }

    private static String args(Object... values) {
        return Arrays.stream(values).map(Generator::literal).collect(Collectors.joining(", "));
    }

    private void line(String statement) {
        source.append("        ").append(statement).append(";\n");
    }

    private void failure(String expression, RuntimeException error) {
        line("assertThrows(" + error.getClass().getCanonicalName() + ".class, () -> " + expression + ")");
    }

    private <T> T bind(String name, String expression, Supplier<T> oracle) {
        T value;
        try {
            value = oracle.get();
        } catch (RuntimeException error) {
            failure(expression, error);
            return null;
        }
        line("var " + name + " = " + expression);
        return value;
    }

    private void check(String expression, Supplier<?> oracle) {
        Object value;
        try {
            value = oracle.get();
        } catch (RuntimeException error) {
            failure(expression, error);
            return;
        }
        line("assertEquals(" + literal(value) + ", " + expression + ")");
    }

    private void action(String expression, Runnable oracle) {
        try {
            oracle.run();
        } catch (RuntimeException error) {
            failure(expression, error);
            return;
        }
        line(expression);
    }

    private Product product(String var, boolean allowInvalid) {
        String id = label(var, allowInvalid), name = label("sku", allowInvalid);
        long price = number(allowInvalid ? -50000 : 1, 1000000);
        int weight = allowInvalid ? number(-100, 25000) : number(1, 1600);
        int stock = number(allowInvalid ? -5 : 1, 85);
        return bind(var, "new Product(" + args(id, name, price, weight, stock) + ")",
                () -> new Product(id, name, price, weight, stock));
    }

    private void inventory() {
        Product p = product("p", true);
        if (p == null) return;
        check("p.getId()", p::getId);
        check("p.getName()", p::getName);
        check("p.getPrice()", p::getPrice);
        check("p.getWeightGrams()", p::getWeightGrams);
        int steps = number(2, 5);
        for (int i = 0; i < steps; i++) {
            int q = number(-5, 60);
            if (random.nextBoolean()) action("p.reduceStock(" + q + ")", () -> p.reduceStock(q));
            else action("p.restoreStock(" + q + ")", () -> p.restoreStock(q));
            check("p.getStock()", p::getStock);
        }
    }

    private void item() {
        Product p = sometimes() ? null : product("p", false);
        int q = number(-5, 60);
        CartItem item = bind("item", "new CartItem(" + (p == null ? "null" : "p") + ", " + q + ")",
                () -> new CartItem(p, q));
        if (item != null) readItem("item", item);
    }

    private void readItem(String var, CartItem item) {
        check(var + ".getProduct().getId()", () -> item.getProduct().getId());
        check(var + ".getQuantity()", item::getQuantity);
        check(var + ".getLineTotal()", item::getLineTotal);
    }

    private void items(String expression, List<CartItem> items) {
        check(expression + ".size()", items::size);
        for (int i = 0; i < items.size(); i++)
            readItem(expression + ".get(" + i + ")", items.get(i));
    }

    private void cart() {
        Cart cart = bind("cart", "new Cart()", Cart::new);
        Product a = product("a", false), b = product("b", false);
        int steps = number(3, 7);
        for (int i = 0; i < steps; i++) {
            Product p = random.nextBoolean() ? a : b;
            String var = p == a ? "a" : "b";
            int q = number(-5, 60);
            String id = sometimes() ? label("other", true) : p.getId();
            switch (random.nextInt(3)) {
                case 0 -> {
                    boolean missing = sometimes();
                    action("cart.addItem(" + (missing ? "null" : var) + ", " + q + ")",
                            () -> cart.addItem(missing ? null : p, q));
                }
                case 1 -> action("cart.updateQuantity(" + args(id, q) + ")", () -> cart.updateQuantity(id, q));
                default -> action("cart.removeItem(" + literal(id) + ")", () -> cart.removeItem(id));
            }
            check("cart.getSubtotal()", cart::getSubtotal);
            check("cart.getTotalWeightGrams()", cart::getTotalWeightGrams);
            check("cart.isEmpty()", cart::isEmpty);
            items("cart.getItems()", cart.getItems());
        }
    }

    private LocalDate date() {
        return LocalDate.of(2026, 1, 1).plusDays(number(-730, 730));
    }

    private Coupon coupon(boolean allowInvalid) {
        String code = label("code", allowInvalid);
        CouponType type = allowInvalid && sometimes() ? null : pick(CouponType.values());
        long value = type == CouponType.PERCENT ? number(allowInvalid ? -5 : 1, allowInvalid ? 130 : 100)
                : number(allowInvalid ? -10000 : 1, 600000);
        long min = number(allowInvalid ? -50000 : 0, 600000), cap = number(allowInvalid ? -25000 : 0, 300000);
        LocalDate expiry = allowInvalid && sometimes() ? null : date();
        return bind("coupon", "new Coupon(" + args(code, type, value, min, cap, expiry) + ")",
                () -> new Coupon(code, type, value, min, cap, expiry));
    }

    private void couponData() {
        Coupon c = coupon(true);
        if (c == null) return;
        check("coupon.getCode()", c::getCode);
        check("coupon.getType()", c::getType);
        check("coupon.getValue()", c::getValue);
        check("coupon.getMinOrder()", c::getMinOrder);
        check("coupon.getMaxDiscount()", c::getMaxDiscount);
        check("coupon.getExpiry()", c::getExpiry);
    }

    private void pricing() {
        Coupon c = sometimes() ? null : coupon(false);
        LocalDate today = sometimes() ? null : c == null ? date() : c.getExpiry().plusDays(number(-60, 60));
        long subtotal = number(-50000, 1250000);
        check("new PricingService().calculateDiscount(" + literal(subtotal) + ", "
                        + (c == null ? "null" : "coupon") + ", " + literal(today) + ")",
                () -> new PricingService().calculateDiscount(subtotal, c, today));
    }

    private void shipping() {
        Region region = sometimes() ? null : pick(Region.values());
        int weight = number(-100, 25000);
        long goods = number(-50000, 1250000);
        check("new ShippingCalculator().calculate(" + args(region, weight, goods) + ")",
                () -> new ShippingCalculator().calculate(region, weight, goods));
    }

    private void order() {
        boolean inject = random.nextBoolean();
        boolean noPricing = inject && sometimes(), noShipping = inject && sometimes();
        String constructor = inject ? "new OrderService(" + (noPricing ? "null" : "new PricingService()")
                + ", " + (noShipping ? "null" : "new ShippingCalculator()") + ")" : "new OrderService()";
        OrderService service = bind("service", constructor, () -> inject
                ? new OrderService(noPricing ? null : new PricingService(), noShipping ? null : new ShippingCalculator())
                : new OrderService());
        if (service == null) return;
        Cart cart = bind("cart", "new Cart()", Cart::new);
        Product p = product("p", false);
        boolean nullCart = sometimes();
        if (!nullCart) {
            int qty = number(1, 18);
            action("cart.addItem(p, " + qty + ")", () -> cart.addItem(p, qty));
        }
        Coupon c = random.nextBoolean() ? null : coupon(false);
        LocalDate day = c == null ? date() : c.getExpiry().plusDays(number(-60, 60));
        LocalDateTime now = day.atTime(number(0, 23), number(0, 59));
        LocalDateTime placeNow = sometimes() ? null : now;
        String id = label("order", true);
        Region region = sometimes() ? null : pick(Region.values());
        String place = "service.placeOrder(" + literal(id) + ", " + (nullCart ? "null" : "cart") + ", "
                + (c == null ? "null" : "coupon") + ", " + args(region, placeNow) + ")";
        Order o = bind("order", place, () -> service.placeOrder(id, nullCart ? null : cart, c, region, placeNow));
        check("p.getStock()", p::getStock);
        if (o != null) {
            check("order.getId()", o::getId);
            check("order.getSubtotal()", o::getSubtotal);
            check("order.getDiscount()", o::getDiscount);
            check("order.getShipping()", o::getShipping);
            check("order.getTotal()", o::getTotal);
            check("order.getCreatedAt()", o::getCreatedAt);
            check("order.getRegion()", o::getRegion);
            check("order.getStatus()", o::getStatus);
            items("order.getItems()", o.getItems());
        }
        String var = o == null ? "null" : "order";
        int steps = number(2, 5);
        for (int i = 0; i < steps; i++) {
            switch (random.nextInt(3)) {
                case 0 -> action("service.pay(" + var + ")", () -> service.pay(o));
                case 1 -> action("service.ship(" + var + ")", () -> service.ship(o));
                default -> {
                    LocalDateTime when = sometimes() ? null : now.plusMinutes(number(-1440, 5760));
                    check("service.cancelOrder(" + var + ", " + literal(when) + ")", () -> service.cancelOrder(o, when));
                }
            }
            if (o != null) check("order.getStatus()", o::getStatus);
            check("p.getStock()", p::getStock);
        }
    }

    private <E extends Enum<E>> void enumData(E[] values, Function<String, E> lookup) {
        E selected = pick(values);
        String type = selected.getDeclaringClass().getSimpleName();
        int i = number(0, values.length - 1);
        check(type + ".values()[" + i + "]", () -> values[i]);
        String name = random.nextBoolean() ? selected.name() : label("unknown", true);
        check(type + ".valueOf(" + literal(name) + ")", () -> lookup.apply(name));
    }

    private void domainTypes() {
        enumData(Region.values(), Region::valueOf);
        enumData(CouponType.values(), CouponType::valueOf);
        enumData(OrderStatus.values(), OrderStatus::valueOf);
        String msg = label("message", true);
        String arg = "(" + literal(msg) + ").getClass().getSimpleName()";
        check("new InsufficientStockException" + arg, () -> new InsufficientStockException(msg).getClass().getSimpleName());
        check("new CouponExpiredException" + arg, () -> new CouponExpiredException(msg).getClass().getSimpleName());
        check("new CouponNotApplicableException" + arg, () -> new CouponNotApplicableException(msg).getClass().getSimpleName());
    }

    private String generate(int count) {
        source.append("""
                package shop;

                import java.time.LocalDate;
                import java.time.LocalDateTime;
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.*;

                public class RandomShopTest {
                """);
        // Balance API families; all input values and state transitions remain sampled.
        List<Runnable> scenarios = List.of(this::inventory, this::item, this::cart, this::couponData,
                this::pricing, this::shipping, this::order, this::domainTypes);
        for (int i = 0; i < count; i++) {
            source.append("\n    @Test\n    void sample").append(i + 1).append("() {\n");
            scenarios.get(i % scenarios.size()).run();
            source.append("    }\n");
        }
        return source.append("}\n").toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: p2.Generator <seed> <numTests> <outputFile>");
        }
        long seed = Long.parseLong(args[0]);
        int count = Integer.parseInt(args[1]);
        if (count < 1) {
            throw new IllegalArgumentException("numTests must be positive: " + count);
        }
        Generator generator = new Generator(seed);
        Files.writeString(Path.of(args[2]), generator.generate(count), StandardCharsets.UTF_8);
    }
}
