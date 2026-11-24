package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @ParameterizedTest
    @ValueSource(strings = {"inner_city", "UNKNOWN", " INNER_CITY ", ""})
    void regionEnumValueOfInvalid(String name) {
        assertThrows(IllegalArgumentException.class, () -> Region.valueOf(name));
    }
}
