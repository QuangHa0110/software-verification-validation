package shop;

import java.time.LocalDateTime;
import java.util.List;

public class OrderService {
    private final PricingService pricingService;
    private final ShippingCalculator shippingCalculator;

    public OrderService() {
        this(new PricingService(), new ShippingCalculator());
    }

    public OrderService(PricingService pricingService, ShippingCalculator shippingCalculator) {
        if (pricingService == null || shippingCalculator == null) {
            throw new IllegalArgumentException("Both pricing and shipping services are required");
        }
        this.pricingService = pricingService;
        this.shippingCalculator = shippingCalculator;
    }

    public Order placeOrder(String orderId, Cart cart, Coupon coupon, Region region, LocalDateTime now) {
        if (orderId == null || orderId.trim().isEmpty() || region == null || now == null) {
            throw new IllegalArgumentException("Missing order ID, region or time");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Order requires a nonempty cart");
        }
        List<CartItem> items = cart.getItems();
        for (CartItem item : items) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                throw new InsufficientStockException(item.getProduct().getId());
            }
        }
        long subtotal = cart.getSubtotal();
        long discount = pricingService.calculateDiscount(subtotal, coupon, now.toLocalDate());
        long goods = subtotal - discount;
        long shipping = shippingCalculator.calculate(region, cart.getTotalWeightGrams(), goods);
        Order order = new Order(orderId, items, subtotal, discount, shipping, goods + shipping, now, region);

        for (CartItem item : items) {
            item.getProduct().reduceStock(item.getQuantity());
        }
        return order;
    }

    public void pay(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Missing order");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order must be pending before payment");
        }
        order.setStatus(OrderStatus.PAID);
    }

    public void ship(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Missing order");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Order must be paid before shipping");
        }
        order.setStatus(OrderStatus.SHIPPED);
    }

    public long cancelOrder(Order order, LocalDateTime now) {
        if (order == null || now == null) {
            throw new IllegalArgumentException("Missing order or cancellation time");
        }
        OrderStatus status = order.getStatus();
        if (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a " + status + " order");
        }
        if (now.isBefore(order.getCreatedAt())) {
            throw new IllegalArgumentException("Cancellation precedes order");
        }
        long refund = 0;
        if (status == OrderStatus.PAID) {
            if (now.isAfter(order.getCreatedAt().plusHours(24))) {
                refund = (order.getSubtotal() - order.getDiscount()) * 80 / 100;
            } else {
                refund = order.getTotal();
            }
        }
        for (CartItem item : order.getItems()) {
            item.getProduct().restoreStock(item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return refund;
    }
}
