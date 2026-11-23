package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    void productConstructorInvalidIdThrowsException(String invalidId) {
        assertThrows(IllegalArgumentException.class, () -> new Product(invalidId, "Book", 100000L, 250, 10));
    }
}
