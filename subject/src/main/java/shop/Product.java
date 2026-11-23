package shop;

public final class Product {
    private final String id;
    private final String name;
    private final long price;
    private final int weightGrams;
    private int stock;

    public Product(String id, String name, long price, int weightGrams, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing product ID");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing product name");
        }
        if (price <= 0 || weightGrams <= 0 || stock < 0) {
            throw new IllegalArgumentException("Invalid price, weight or stock");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.weightGrams = weightGrams;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    public int getStock() {
        return stock;
    }

    public void reduceStock(int q) {
        if (q <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (q > stock) {
            throw new InsufficientStockException(id);
        }
        stock -= q;
    }

    public void restoreStock(int q) {
        if (q <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stock += q;
    }
}
