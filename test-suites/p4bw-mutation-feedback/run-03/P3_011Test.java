package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @ParameterizedTest
    @ValueSource(strings = {"pending", "Paid", "UNKNOWN", ""})
    void orderStatusEnumValueOfInvalid(String name) {
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf(name));
    }
}
