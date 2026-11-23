package shop;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class Cart {
    public static final int MAX_QTY_PER_ITEM = 50;

    private final LinkedHashMap<String, CartItem> items = new LinkedHashMap<>();

    public Cart() {
    }

    public void addItem(Product p, int qty) {
        if (p == null || qty <= 0) {
            throw new IllegalArgumentException("Product must not be null and qty must be greater than 0");
        }
        CartItem existing = items.get(p.getId());
        long currentQty = existing == null ? 0L : existing.getQuantity();
        long newQty = currentQty + (long) qty;
        if (newQty > MAX_QTY_PER_ITEM) {
            throw new IllegalArgumentException("Quantity per item cannot exceed " + MAX_QTY_PER_ITEM);
        }
        if (newQty > p.getStock()) {
            throw new InsufficientStockException("Insufficient stock for product " + p.getId());
        }
        items.put(p.getId(), new CartItem(p, (int) newQty));
    }

    public void updateQuantity(String productId, int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        if (productId == null || productId.trim().isEmpty() || !items.containsKey(productId)) {
            throw new NoSuchElementException("Product not found in cart");
        }
        if (qty == 0) {
            items.remove(productId);
            return;
        }
        if (qty > MAX_QTY_PER_ITEM) {
            throw new IllegalArgumentException("Quantity per item cannot exceed " + MAX_QTY_PER_ITEM);
        }
        CartItem existing = items.get(productId);
        if (qty > existing.getProduct().getStock()) {
            throw new InsufficientStockException("Insufficient stock for product " + productId);
        }
        items.put(productId, new CartItem(existing.getProduct(), qty));
    }

    public void removeItem(String productId) {
        if (productId == null || productId.trim().isEmpty() || !items.containsKey(productId)) {
            throw new NoSuchElementException("Product not found in cart");
        }
        items.remove(productId);
    }

    public long getSubtotal() {
        long subtotal = 0L;
        for (CartItem item : items.values()) {
            subtotal += item.getLineTotal();
        }
        return subtotal;
    }

    public int getTotalWeightGrams() {
        int totalWeight = 0;
        for (CartItem item : items.values()) {
            totalWeight += item.getProduct().getWeightGrams() * item.getQuantity();
        }
        return totalWeight;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items.values());
    }
}
