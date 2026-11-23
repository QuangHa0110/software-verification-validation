package shop;

import java.time.LocalDateTime;

public class OrderService {
    public OrderService() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public OrderService(PricingService pricingService, ShippingCalculator shippingCalculator) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public Order placeOrder(String orderId, Cart cart, Coupon coupon, Region region, LocalDateTime now) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public void pay(Order order) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public void ship(Order order) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public long cancelOrder(Order order, LocalDateTime now) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }
}
