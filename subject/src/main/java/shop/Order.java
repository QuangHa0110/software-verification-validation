package shop;

import java.time.LocalDateTime;
import java.util.List;

public final class Order {
    Order(String id, List<CartItem> items, long subtotal, long discount, long shipping, long total, LocalDateTime createdAt, Region region) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public String getId() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public List<CartItem> getItems() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public long getSubtotal() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public long getDiscount() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public long getShipping() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public long getTotal() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public OrderStatus getStatus() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public LocalDateTime getCreatedAt() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    public Region getRegion() {
        throw new UnsupportedOperationException("chưa cài đặt");
    }

    void setStatus(OrderStatus status) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }
}
