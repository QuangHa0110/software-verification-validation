package shop;

import java.time.LocalDate;

public class PricingService {
    public PricingService() {
    }

    public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
        if (subtotal < 0 || today == null) {
            throw new IllegalArgumentException("Invalid subtotal or date");
        }
        if (coupon == null) {
            return 0;
        }
        if (today.isAfter(coupon.getExpiry())) {
            throw new CouponExpiredException(coupon.getCode());
        }
        if (subtotal < coupon.getMinOrder()) {
            throw new CouponNotApplicableException(coupon.getCode());
        }
        if (coupon.getType() == CouponType.PERCENT) {
            long discount = subtotal * coupon.getValue() / 100;
            if (coupon.getMaxDiscount() > 0 && discount > coupon.getMaxDiscount()) {
                return coupon.getMaxDiscount();
            }
            return discount;
        }
        return Math.min(coupon.getValue(), subtotal);
    }
}
