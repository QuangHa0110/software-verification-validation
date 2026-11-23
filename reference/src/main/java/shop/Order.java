package shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            throw new IllegalArgumentException("Order id must not be empty");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items must not be null or empty");
        }
        List<CartItem> copiedItems = new ArrayList<>(items.size());
        long computedSubtotal = 0L;
        for (CartItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException("Order item must not be null");
            }
            copiedItems.add(item);
            computedSubtotal += item.getLineTotal();
        }
        if (subtotal != computedSubtotal) {
            throw new IllegalArgumentException("Order subtotal does not match sum of item line totals");
        }
        if (discount < 0 || discount > subtotal) {
            throw new IllegalArgumentException("Order discount must be between 0 and subtotal");
        }
        if (shipping < 0 || total != subtotal - discount + shipping) {
            throw new IllegalArgumentException("Order shipping or total is invalid");
        }
        if (createdAt == null || region == null) {
            throw new IllegalArgumentException("Order createdAt and region must not be null");
        }
        this.id = id;
        this.items = List.copyOf(copiedItems);
        this.subtotal = subtotal;
        this.discount = discount;
        this.shipping = shipping;
        this.total = total;
        this.status = OrderStatus.PENDING;
        this.createdAt = createdAt;
        this.region = region;
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
            throw new IllegalArgumentException("Order status must not be null");
        }
        this.status = status;
    }
}
