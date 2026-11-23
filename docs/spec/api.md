# API đóng băng tại M1

Mỗi khối mô tả một kiểu cấp cao nhất trong package `shop`. Dấu `;` sau chữ ký thay cho thân hàm. Kiểu ngắn dùng `java.lang.*`, `java.time.LocalDate`, `java.time.LocalDateTime`, `java.util.List` hoặc kiểu cùng package. Thành viên thiếu từ khóa truy cập có phạm vi package-private. Constructor không có kiểu trả về. Các phương thức nghiệp vụ là phương thức thực thể.

```java
public enum Region {
    INNER_CITY, SUBURB, PROVINCE, REMOTE
}
```

```java
public enum CouponType {
    PERCENT, FIXED
}
```

```java
public enum OrderStatus {
    PENDING, PAID, SHIPPED, CANCELLED
}
```

```java
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message);
}
```

```java
public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String message);
}
```

```java
public class CouponNotApplicableException extends RuntimeException {
    public CouponNotApplicableException(String message);
}
```

```java
public final class Product {
    public Product(String id, String name, long price, int weightGrams, int stock);
    public String getId();
    public String getName();
    public long getPrice();
    public int getWeightGrams();
    public int getStock();
    public void reduceStock(int q);
    public void restoreStock(int q);
}
```

```java
public final class Coupon {
    public Coupon(String code, CouponType type, long value, long minOrder, long maxDiscount, LocalDate expiry);
    public String getCode();
    public CouponType getType();
    public long getValue();
    public long getMinOrder();
    public long getMaxDiscount();
    public LocalDate getExpiry();
}
```

```java
public final class CartItem {
    public CartItem(Product product, int quantity);
    public Product getProduct();
    public int getQuantity();
    public long getLineTotal();
}
```

```java
public class Cart {
    public static final int MAX_QTY_PER_ITEM = 50;
    public Cart();
    public void addItem(Product p, int qty);
    public void updateQuantity(String productId, int qty);
    public void removeItem(String productId);
    public long getSubtotal();
    public int getTotalWeightGrams();
    public boolean isEmpty();
    public List<CartItem> getItems();
}
```

```java
public class PricingService {
    public PricingService();
    public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today);
}
```

```java
public class ShippingCalculator {
    public ShippingCalculator();
    public long calculate(Region region, int weightGrams, long orderValue);
}
```

```java
public final class Order {
    Order(String id, List<CartItem> items, long subtotal, long discount, long shipping, long total, LocalDateTime createdAt, Region region);
    public String getId();
    public List<CartItem> getItems();
    public long getSubtotal();
    public long getDiscount();
    public long getShipping();
    public long getTotal();
    public OrderStatus getStatus();
    public LocalDateTime getCreatedAt();
    public Region getRegion();
    void setStatus(OrderStatus status);
}
```

```java
public class OrderService {
    public OrderService();
    public OrderService(PricingService pricingService, ShippingCalculator shippingCalculator);
    public Order placeOrder(String orderId, Cart cart, Coupon coupon, Region region, LocalDateTime now);
    public void pay(Order order);
    public void ship(Order order);
    public long cancelOrder(Order order, LocalDateTime now);
}
```

## Thành viên enum do Java 17 sinh

| Kiểu | Phương thức public static |
|---|---|
| `Region` | `Region[] values()`, `Region valueOf(String name)` |
| `CouponType` | `CouponType[] values()`, `CouponType valueOf(String name)` |
| `OrderStatus` | `OrderStatus[] values()`, `OrderStatus valueOf(String name)` |

Hằng enum là `public static final` với kiểu chính enum đó. Constructor enum là private ngầm định. Thành viên kế thừa từ thư viện Java tuân theo JDK 17. Hành vi cho từng kiểu dữ liệu nằm trong [domain.md](domain.md).
