package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "\t\n"})
    void productInvalidName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", name, 1000L, 10, 5));
    }
}
