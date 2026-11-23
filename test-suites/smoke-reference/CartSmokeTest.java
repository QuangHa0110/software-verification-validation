package shop;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CartSmokeTest {

    @Test
    void emptyCartAndIndependentInstances() {
        Cart cart1 = new Cart();
        assertTrue(cart1.isEmpty());
        assertEquals(0, cart1.getItems().size());
        assertEquals(0L, cart1.getSubtotal());
        assertEquals(0, cart1.getTotalWeightGrams());

        Product a = new Product("A", "A", 12000, 200, 10);
        Product b = new Product("B", "B", 7000, 150, 10);
        cart1.addItem(a, 2);
        cart1.addItem(b, 3);

        Cart cart2 = new Cart();
        assertEquals(2, cart1.getItems().size());
        assertEquals(0, cart2.getItems().size());
        assertFalse(cart1.isEmpty());
        assertTrue(cart2.isEmpty());
    }

    @Test
    void addItemExamplesAndCheckOrder() {
        Cart cart = new Cart();
        Product aOld = new Product("A", "A old", 9, 10, 1);
        Product b = new Product("B", "B", 5, 10, 5);
        cart.addItem(aOld, 1);
        cart.addItem(b, 1);

        Product aNew = new Product("A", "A new", 12, 20, 50);
        cart.addItem(aNew, 49);

        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(aNew, items.get(0).getProduct());
        assertEquals(50, items.get(0).getQuantity());
        assertEquals(600L, items.get(0).getLineTotal());
        assertSame(b, items.get(1).getProduct());

        // Example 2: 49 + 2 = 51 > 50 throws IAE before InsufficientStockException
        Cart cart2 = new Cart();
        Product a49 = new Product("A", "A49", 10, 10, 50);
        cart2.addItem(a49, 49);
        Product aLowStock = new Product("A", "A low", 10, 10, 3);
        assertThrows(IllegalArgumentException.class, () -> cart2.addItem(aLowStock, 2));
        assertEquals(1, cart2.getItems().size());
        assertSame(a49, cart2.getItems().get(0).getProduct());
        assertEquals(49, cart2.getItems().get(0).getQuantity());

        // Example 3: empty cart, stock 2, qty 3 -> InsufficientStockException
        Cart cart3 = new Cart();
        Product aStock2 = new Product("A", "A", 10, 10, 2);
        assertThrows(InsufficientStockException.class, () -> cart3.addItem(aStock2, 3));
        assertTrue(cart3.isEmpty());

        assertThrows(IllegalArgumentException.class, () -> cart3.addItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> cart3.addItem(aStock2, 0));
        assertThrows(IllegalArgumentException.class, () -> cart3.addItem(aStock2, -1));
    }

    @Test
    void updateQuantityExamplesAndCheckOrder() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 100, 10, 50);
        cart.addItem(a, 2);
        cart.updateQuantity("A", 50);
        assertEquals(50, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("A", 0);
        assertTrue(cart.isEmpty());

        // Empty cart: blank id with -1 -> IAE; blank id with 51 -> NoSuchElementException
        Cart empty = new Cart();
        assertThrows(IllegalArgumentException.class, () -> empty.updateQuantity("  ", -1));
        assertThrows(NoSuchElementException.class, () -> empty.updateQuantity("  ", 51));
        assertTrue(empty.isEmpty());

        // Existing item: stock 3, update 4 -> InsufficientStockException
        Cart cart3 = new Cart();
        Product a3 = new Product("A", "A", 100, 10, 3);
        cart3.addItem(a3, 2);
        assertThrows(InsufficientStockException.class, () -> cart3.updateQuantity("A", 4));
        assertEquals(2, cart3.getItems().get(0).getQuantity());

        // Existing item: qty 51 with low stock -> IAE before InsufficientStockException
        assertThrows(IllegalArgumentException.class, () -> cart3.updateQuantity("A", 51));
        assertEquals(2, cart3.getItems().get(0).getQuantity());

        // Existing item: stock dropped below current qty, updating to same qty still fails
        a3.reduceStock(2);
        assertEquals(1, a3.getStock());
        assertThrows(InsufficientStockException.class, () -> cart3.updateQuantity("A", 2));
        assertEquals(2, cart3.getItems().get(0).getQuantity());
    }

    @Test
    void removeItemAndSnapshotAndReadOperations() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 12000, 200, 10);
        Product b = new Product("B", "B", 7000, 150, 10);
        cart.addItem(a, 2);
        cart.addItem(b, 3);

        assertEquals(45000L, cart.getSubtotal());
        assertEquals(850, cart.getTotalWeightGrams());
        assertFalse(cart.isEmpty());

        List<CartItem> snapshot = cart.getItems();
        assertEquals(2, snapshot.size());
        assertSame(a, snapshot.get(0).getProduct());
        assertEquals(2, snapshot.get(0).getQuantity());
        assertSame(b, snapshot.get(1).getProduct());
        assertEquals(3, snapshot.get(1).getQuantity());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));

        cart.removeItem("A");
        assertEquals(1, cart.getItems().size());
        assertSame(b, cart.getItems().get(0).getProduct());
        assertEquals(2, snapshot.size());

        // Re-adding A places it at the end
        cart.addItem(a, 1);
        assertEquals("B", cart.getItems().get(0).getProduct().getId());
        assertEquals("A", cart.getItems().get(1).getProduct().getId());

        assertThrows(NoSuchElementException.class, () -> cart.removeItem("MISSING"));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
        assertThrows(NoSuchElementException.class, () -> cart.removeItem("   "));

        // Long overflow-int example from cart.md
        Cart bigCart = new Cart();
        Product big = new Product("BIG", "Big", 1_000_000_000L, 10, 5);
        bigCart.addItem(big, 3);
        assertEquals(3_000_000_000L, bigCart.getSubtotal());
    }
}
