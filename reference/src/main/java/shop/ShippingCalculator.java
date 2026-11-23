package shop;

public class ShippingCalculator {
    private static final int MAX_WEIGHT_GRAMS = 20000;
    private static final long FREE_SHIPPING_THRESHOLD = 500000L;
    private static final int BASE_WEIGHT_GRAMS = 1000;
    private static final int SURCHARGE_STEP_GRAMS = 500;
    private static final long SURCHARGE_PER_STEP = 2000L;

    public ShippingCalculator() {
    }

    public long calculate(Region region, int weightGrams, long orderValue) {
        if (region == null || weightGrams <= 0 || orderValue < 0) {
            throw new IllegalArgumentException("Invalid shipping parameters");
        }
        if (weightGrams > MAX_WEIGHT_GRAMS) {
            throw new IllegalArgumentException("Weight exceeds maximum allowed limit");
        }
        if (orderValue >= FREE_SHIPPING_THRESHOLD
                && (region == Region.INNER_CITY || region == Region.SUBURB)) {
            return 0L;
        }
        long baseFee;
        switch (region) {
            case INNER_CITY:
                baseFee = 15000L;
                break;
            case SUBURB:
                baseFee = 25000L;
                break;
            case PROVINCE:
                baseFee = 35000L;
                break;
            case REMOTE:
                baseFee = 55000L;
                break;
            default:
                throw new IllegalArgumentException("Unknown region: " + region);
        }
        long surcharge = 0L;
        int excessWeight = weightGrams - BASE_WEIGHT_GRAMS;
        while (excessWeight > 0) {
            surcharge += SURCHARGE_PER_STEP;
            excessWeight -= SURCHARGE_STEP_GRAMS;
        }
        return baseFee + surcharge;
    }
}
