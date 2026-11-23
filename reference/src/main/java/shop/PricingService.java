package shop;

import java.time.LocalDate;

public class PricingService {
    public PricingService() {
    }

    public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
        if (subtotal < 0 || today == null) {
            throw new IllegalArgumentException("Subtotal must be non-negative and today must not be null");
        }
        if (coupon == null) {
            return 0L;
        }
        if (today.isAfter(coupon.getExpiry())) {
            throw new CouponExpiredException("Coupon has expired: " + coupon.getCode());
        }
        if (subtotal < coupon.getMinOrder()) {
            throw new CouponNotApplicableException("Subtotal does not meet minimum order for coupon: " + coupon.getCode());
        }
        long discount;
        if (coupon.getType() == CouponType.PERCENT) {
            discount = subtotal * coupon.getValue() / 100L;
            if (coupon.getMaxDiscount() > 0 && discount > coupon.getMaxDiscount()) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            discount = Math.min(coupon.getValue(), subtotal);
        }
        return discount;
    }
}
