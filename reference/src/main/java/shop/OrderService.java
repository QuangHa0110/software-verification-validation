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
            throw new IllegalArgumentException("PricingService and ShippingCalculator must not be null");
        }
        this.pricingService = pricingService;
        this.shippingCalculator = shippingCalculator;
    }

    public Order placeOrder(String orderId, Cart cart, Coupon coupon, Region region, LocalDateTime now) {
        if (orderId == null || orderId.trim().isEmpty() || region == null || now == null) {
            throw new IllegalArgumentException("orderId, region, and now must be valid");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Cart must not be null or empty");
        }
        List<CartItem> items = cart.getItems();
        for (CartItem item : items) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                throw new InsufficientStockException("Insufficient stock for product " + item.getProduct().getId());
            }
        }
        long subtotal = cart.getSubtotal();
        long discount = pricingService.calculateDiscount(subtotal, coupon, now.toLocalDate());
        long goods = subtotal - discount;
        long shipping = shippingCalculator.calculate(region, cart.getTotalWeightGrams(), goods);
        long total = goods + shipping;
        Order order = new Order(orderId, items, subtotal, discount, shipping, total, now, region);
        for (CartItem item : items) {
            item.getProduct().reduceStock(item.getQuantity());
        }
        return order;
    }

    public void pay(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be paid");
        }
        order.setStatus(OrderStatus.PAID);
    }

    public void ship(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only PAID orders can be shipped");
        }
        order.setStatus(OrderStatus.SHIPPED);
    }

    public long cancelOrder(Order order, LocalDateTime now) {
        if (order == null || now == null) {
            throw new IllegalArgumentException("Order and now must not be null");
        }
        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == OrderStatus.SHIPPED || currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel an order in status " + currentStatus);
        }
        if (now.isBefore(order.getCreatedAt())) {
            throw new IllegalArgumentException("Cancel time cannot be before order creation time");
        }
        long refund;
        if (currentStatus == OrderStatus.PENDING) {
            refund = 0L;
        } else {
            if (!now.isAfter(order.getCreatedAt().plusHours(24))) {
                refund = order.getTotal();
            } else {
                refund = (order.getSubtotal() - order.getDiscount()) * 80L / 100L;
            }
        }
        for (CartItem item : order.getItems()) {
            item.getProduct().restoreStock(item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return refund;
    }
}
