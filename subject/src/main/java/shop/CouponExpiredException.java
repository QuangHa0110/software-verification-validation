package shop;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String message) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }
}
