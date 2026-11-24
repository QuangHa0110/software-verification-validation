package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void pricingServiceDefaultConstructor() {
        PricingService pricingService = new PricingService();
        assertNotNull(pricingService);
    }
}
