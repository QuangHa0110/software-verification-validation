package shop;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartSmokeTest {
    private final Cart cart = new Cart();
    private final Product pen = new Product("P", "Pen", 12_000, 200, 5);
    private final Product book = new Product("B", "Book", 7_000, 150, 3);

    @Test
    void snapshotsRetainOrderAndQuantitiesAcrossCartEdits() {
        assertTrue(cart.isEmpty());
        cart.addItem(pen, 2);
        cart.addItem(book, 3);
        assertEquals(45_000, cart.getSubtotal());
        assertEquals(850, cart.getTotalWeightGrams());
        List<CartItem> snapshot = cart.getItems();

        Product replacement = new Product("P", "Pen refill", 2_000, 10, 5);
        cart.addItem(replacement, 1);
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("P", 1);
        assertEquals(23_000, cart.getSubtotal());
        cart.removeItem("P");
        cart.updateQuantity("B", 0);
        assertTrue(cart.isEmpty());
        assertSame(pen, snapshot.get(0).getProduct());
        assertEquals(2, snapshot.get(0).getQuantity());
        assertSame(book, snapshot.get(1).getProduct());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
    }

    @Test
    void failedAddsKeepTheOriginalProductAndQuantity() {
        cart.addItem(pen, 2);
        Product replacement = new Product("P", "Refill", 1, 1, 0);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 49));
        assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        assertSame(pen, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void failedUpdatesAndRemovalsKeepTheCartIntact() {
        cart.addItem(pen, 2);
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("missing", -1));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("missing", 51));
        assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("P", 6));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(5, pen.getStock());
    }
}
