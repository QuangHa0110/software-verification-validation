package shop;

public final class CartItem {
    private final Product product;
    private final int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Expected a product and a positive quantity");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getLineTotal() {
        return product.getPrice() * quantity;
    }
}
