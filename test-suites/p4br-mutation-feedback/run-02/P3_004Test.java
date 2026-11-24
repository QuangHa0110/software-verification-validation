package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void productRequiresBothIdAndNameToContainText(String invalid) {
        assertThrows(IllegalArgumentException.class, () -> new Product(invalid, "Name", 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Product("ID", invalid, 1, 1, 0));
    }
}
