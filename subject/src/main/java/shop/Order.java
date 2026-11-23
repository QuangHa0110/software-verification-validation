package shop;

import java.time.LocalDateTime;
import java.util.List;

public final class Order {
    private final String id;
    private final List<CartItem> items;
    private final long subtotal;
    private final long discount;
    private final long shipping;
    private final long total;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final Region region;

    Order(String id, List<CartItem> items, long subtotal, long discount, long shipping, long total, LocalDateTime createdAt, Region region) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing order ID");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain items");
        }
        long lineSum = 0;
        for (CartItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException("Missing order item");
            }
            lineSum += item.getLineTotal();
        }
        if (subtotal != lineSum || discount < 0 || discount > subtotal) {
            throw new IllegalArgumentException("Invalid subtotal or discount");
        }
        if (shipping < 0 || total != subtotal - discount + shipping) {
            throw new IllegalArgumentException("Invalid shipping fee or total");
        }
        if (createdAt == null || region == null) {
            throw new IllegalArgumentException("Missing order date or region");
        }
        this.id = id;
        this.items = List.copyOf(items);
        this.subtotal = subtotal;
        this.discount = discount;
        this.shipping = shipping;
        this.total = total;
        this.createdAt = createdAt;
        this.region = region;
        this.status = OrderStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public long getDiscount() {
        return discount;
    }

    public long getShipping() {
        return shipping;
    }

    public long getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Region getRegion() {
        return region;
    }

    void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Missing order status");
        }
        this.status = status;
    }
}
