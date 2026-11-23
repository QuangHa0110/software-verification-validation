package shop;

public class ShippingCalculator {
    public ShippingCalculator() {
    }

    public long calculate(Region region, int weightGrams, long orderValue) {
        if (region == null || weightGrams <= 0 || orderValue < 0) {
            throw new IllegalArgumentException("Invalid shipping request");
        }
        if (weightGrams > 20_000) {
            throw new IllegalArgumentException("Shipping weight exceeds 20000 g");
        }
        if (orderValue >= 500_000 && (region == Region.INNER_CITY || region == Region.SUBURB)) {
            return 0;
        }
        long fee = switch (region) {
            case INNER_CITY -> 15_000;
            case SUBURB -> 25_000;
            case PROVINCE -> 35_000;
            case REMOTE -> 55_000;
        };
        for (int excess = weightGrams - 1_000; excess > 0; excess -= 500) {
            fee += 2_000;
        }
        return fee;
    }
}
