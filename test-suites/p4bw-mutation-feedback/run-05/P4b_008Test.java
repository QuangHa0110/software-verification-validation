package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_008Test {

    @Test
    void testKillProductConstructorStockZeroBoundary() {
        assertDoesNotThrow(() ->
            new Product("P1", "Product 1", 1000, 100, 0)
        );
    }
}
