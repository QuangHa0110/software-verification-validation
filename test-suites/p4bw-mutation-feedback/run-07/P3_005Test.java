package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void productConstructorNullNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Product("P01", null, 100000L, 250, 10));
    }
}
