package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void testOrderStatusValueOfInvalidThrowsException() {
        assertThrows(NullPointerException.class, () -> OrderStatus.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("pending"));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("OTHER"));
    }
}
