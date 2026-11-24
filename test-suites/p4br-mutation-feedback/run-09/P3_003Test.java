package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " \t\n"})
    void productRejectsBlankIdOrNameIndependently(String blank) {
        assertThrows(IllegalArgumentException.class, () -> new Product(blank, "Name", 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("P", blank, 1, 1, 0));
    }
}
