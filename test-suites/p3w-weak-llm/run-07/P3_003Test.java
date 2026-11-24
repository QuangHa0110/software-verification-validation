package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @Test
    void productConstructorNullIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Book", 100000L, 250, 10));
    }
}
