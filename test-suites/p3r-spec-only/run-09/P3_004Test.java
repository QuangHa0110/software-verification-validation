package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @ParameterizedTest
    @CsvSource({"0,1,0", "-1,1,0", "1,0,0", "1,-1,0", "1,1,-1"})
    void productRejectsInvalidPriceWeightOrStock(long price, int weight, int stock) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P", "Name", price, weight, stock));
    }
}
