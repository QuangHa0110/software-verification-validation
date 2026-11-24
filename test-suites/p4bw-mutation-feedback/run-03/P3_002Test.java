package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_002Test {

    @ParameterizedTest
    @CsvSource({
        "INNER_CITY, INNER_CITY",
        "SUBURB, SUBURB",
        "PROVINCE, PROVINCE",
        "REMOTE, REMOTE"
    })
    void regionEnumValueOfValid(String name, Region expected) {
        assertEquals(expected, Region.valueOf(name));
    }
}
