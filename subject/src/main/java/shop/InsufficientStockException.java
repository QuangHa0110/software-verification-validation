package shop;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }
}
