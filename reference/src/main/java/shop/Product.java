package shop;

public final class Product {
    private final String id;
    private final String name;
    private final long price;
    private final int weightGrams;
    private int stock;

    public Product(String id, String name, long price, int weightGrams, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product id must not be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name must not be empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        if (weightGrams <= 0) {
            throw new IllegalArgumentException("Product weightGrams must be greater than 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock must be non-negative");
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
            throw new IllegalArgumentException("Quantity to reduce must be greater than 0");
        }
        if (q > stock) {
            throw new InsufficientStockException("Insufficient stock for product " + id);
        }
        stock -= q;
    }

    public void restoreStock(int q) {
        if (q <= 0) {
            throw new IllegalArgumentException("Quantity to restore must be greater than 0");
        }
        stock += q;
    }
}
