package shop;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Cart {
    public static final int MAX_QTY_PER_ITEM = 50;
    private final Map<String, CartItem> items;

    public Cart() {
        items = new LinkedHashMap<>();
    }

    public void addItem(Product p, int qty) {
        if (p == null || qty <= 0) {
            throw new IllegalArgumentException("Expected a product and a positive quantity");
        }
        CartItem previous = items.get(p.getId());
        long newQty = (long) qty + (previous == null ? 0 : previous.getQuantity());
        putItem(p, newQty);
    }

    public void updateQuantity(String productId, int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity must be nonnegative");
        }
        CartItem item = items.get(productId);
        if (item == null) {
            throw new NoSuchElementException(productId);
        }
        if (qty == 0) {
            items.remove(productId);
        } else {
            putItem(item.getProduct(), qty);
        }
    }

    private void putItem(Product product, long quantity) {
        if (quantity > MAX_QTY_PER_ITEM) {
            throw new IllegalArgumentException("Quantity exceeds cart limit");
        }
        if (quantity > product.getStock()) {
            throw new InsufficientStockException(product.getId());
        }
        items.put(product.getId(), new CartItem(product, (int) quantity));
    }

    public void removeItem(String productId) {
        if (items.remove(productId) == null) {
            throw new NoSuchElementException(productId);
        }
    }

    public long getSubtotal() {
        long subtotal = 0;
        for (CartItem item : items.values()) {
            subtotal += item.getLineTotal();
        }
        return subtotal;
    }

    public int getTotalWeightGrams() {
        int weight = 0;
        for (CartItem item : items.values()) {
            weight += item.getProduct().getWeightGrams() * item.getQuantity();
        }
        return weight;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items.values());
    }
}
