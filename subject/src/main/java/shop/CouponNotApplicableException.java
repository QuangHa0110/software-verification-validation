package shop;

public class CouponNotApplicableException extends RuntimeException {
    public CouponNotApplicableException(String message) {
        throw new UnsupportedOperationException("chưa cài đặt");
    }
}
