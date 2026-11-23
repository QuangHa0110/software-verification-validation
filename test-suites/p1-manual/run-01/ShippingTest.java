package shop;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static shop.DomainTest.expectError;

class ShippingTest {
    private final ShippingCalculator shipping = new ShippingCalculator();

    @ParameterizedTest
    @CsvSource(value = {"NULL,1000,500000", "INNER_CITY,-1,500000", "INNER_CITY,0,500000",
                        "INNER_CITY,1000,-1", "INNER_CITY,20001,500000", "INNER_CITY,20001,499999"},
               nullValues = "NULL")
    void invalidInputBeforeFreeShipping(Region region, int grams, long goods) {
        // Thiếu vùng, gam ngoài [1,20000] hoặc tiền âm đều bị từ chối trước xét miễn phí.
        expectError(IllegalArgumentException.class, () -> shipping.calculate(region, grams, goods));
    }

    @ParameterizedTest
    @CsvSource({"INNER_CITY,499999,15000", "SUBURB,499999,25000",
                "PROVINCE,499999,35000",   "REMOTE,499999,55000",
                "INNER_CITY,500000,0",     "SUBURB,500000,0",
                "PROVINCE,500000,35000",   "REMOTE,500000,55000"})
    void freeShippingDecisionTable(Region region, long goods, long expected) {
        // 1000 g thêm 0. Dưới ngưỡng giữ phí vùng; đúng 500000 chỉ hai vùng đầu được miễn.
        assertEquals(expected, shipping.calculate(region, 1000, goods));
    }

    @ParameterizedTest
    @CsvSource({"INNER_CITY,0", "SUBURB,0", "PROVINCE,111000", "REMOTE,131000"})
    void heavyShipmentAboveThreshold(Region region, long expected) {
        // 500001 > ngưỡng; hai vùng đầu miễn cả 38 × 2000. Hai vùng còn lại: 35000/55000 + 76000.
        assertEquals(expected, shipping.calculate(region, 20_000, 500_001));
    }

    @ParameterizedTest
    @CsvSource({"1,15000", "1000,15000", "1001,17000", "1500,17000", "1501,19000", "20000,91000"})
    void startedWeightBlocks(int grams, long expected) {
        // Phí = 15000 + 2000 × (0/0/1/1/2/38 khối), tương ứng từng khối lượng.
        assertEquals(expected, shipping.calculate(Region.INNER_CITY, grams, 499_999));
    }
}
