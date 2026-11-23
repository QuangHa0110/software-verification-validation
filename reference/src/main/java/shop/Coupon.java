package shop;

import java.time.LocalDate;

public final class Coupon {
    private final String code;
    private final CouponType type;
    private final long value;
    private final long minOrder;
    private final long maxDiscount;
    private final LocalDate expiry;

    public Coupon(String code, CouponType type, long value, long minOrder, long maxDiscount, LocalDate expiry) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code must not be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Coupon type must not be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Coupon value must be greater than 0");
        }
        if (type == CouponType.PERCENT && value > 100) {
            throw new IllegalArgumentException("Percent coupon value must not exceed 100");
        }
        if (minOrder < 0) {
            throw new IllegalArgumentException("Coupon minOrder must be non-negative");
        }
        if (maxDiscount < 0) {
            throw new IllegalArgumentException("Coupon maxDiscount must be non-negative");
        }
        if (expiry == null) {
            throw new IllegalArgumentException("Coupon expiry must not be null");
        }
        this.code = code;
        this.type = type;
        this.value = value;
        this.minOrder = minOrder;
        this.maxDiscount = maxDiscount;
        this.expiry = expiry;
    }

    public String getCode() {
        return code;
    }

    public CouponType getType() {
        return type;
    }

    public long getValue() {
        return value;
    }

    public long getMinOrder() {
        return minOrder;
    }

    public long getMaxDiscount() {
        return maxDiscount;
    }

    public LocalDate getExpiry() {
        return expiry;
    }
}
