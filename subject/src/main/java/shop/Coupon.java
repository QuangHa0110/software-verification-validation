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
            throw new IllegalArgumentException("Missing coupon code");
        }
        if (type == null || value <= 0 || (type == CouponType.PERCENT && value > 100)) {
            throw new IllegalArgumentException("Invalid coupon type or value");
        }
        if (minOrder < 0 || maxDiscount < 0 || expiry == null) {
            throw new IllegalArgumentException("Invalid coupon terms");
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
