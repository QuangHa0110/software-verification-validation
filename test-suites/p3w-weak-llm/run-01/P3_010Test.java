package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @Test
    void productConstructorNullNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", null, 1000L, 100, 5));
    }
}
