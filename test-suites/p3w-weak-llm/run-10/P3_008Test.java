package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @ParameterizedTest
    @CsvSource({
        "PENDING, PENDING",
        "PAID, PAID",
        "SHIPPED, SHIPPED",
        "CANCELLED, CANCELLED"
    })
    void testOrderStatusValueOfValid(String name, OrderStatus expected) {
        assertEquals(expected, OrderStatus.valueOf(name));
    }
}
