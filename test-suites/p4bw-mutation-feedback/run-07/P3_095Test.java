package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_095Test {

    @Test
    void orderServiceShipNullOrderThrowsException() {
        OrderService service = new OrderService();
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
    }
}
