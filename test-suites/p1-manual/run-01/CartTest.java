package shop;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static shop.DomainTest.expectError;

class CartTest {
    private final Product a = new Product("A", "A", 12_000, 200, 50);
    private final Product b = new Product("B", "B", 7_000, 150, 3);
    private final Cart cart = new Cart();

    static void assertLine(CartItem line, Product product, int quantity) {
        // Dòng giữ đúng tham chiếu sản phẩm và lượng tại thời điểm chụp.
        assertAll(() -> assertSame(product, line.getProduct()), () -> assertEquals(quantity, line.getQuantity()));
    }

    static void assertPair(List<CartItem> items, Product first, int firstQty, Product second, int secondQty) {
        // Hai dòng theo đúng thứ tự thêm; không được gộp nhầm, mất hoặc sinh thêm dòng.
        assertEquals(2, items.size());
        assertLine(items.get(0), first, firstQty);
        assertLine(items.get(1), second, secondQty);
    }

    private void fill(int quantityA) {
        cart.addItem(a, quantityA);
        cart.addItem(b, 3);
    }

    private void checkPair(int quantityA, int stockA) {
        // Các dòng gốc phải còn theo thứ tự A rồi B.
        assertPair(cart.getItems(), a, quantityA, b, 3);
        // Thao tác giỏ không trừ tồn của A hay B.
        assertAll(() -> assertEquals(stockA, a.getStock()), () -> assertEquals(3, b.getStock()));
    }

    private void checkEmpty(Cart empty) {
        // Không còn dòng nào nên tổng tiền và tổng khối lượng đều là 0.
        assertAll(() -> assertTrue(empty.isEmpty()), () -> assertEquals(0, empty.getItems().size()),
                () -> assertEquals(0L, empty.getSubtotal()), () -> assertEquals(0, empty.getTotalWeightGrams()));
    }

    @Test
    void totalsFollowAddedAndRemovedLines() {
        checkEmpty(cart);
        cart.addItem(a, 2);
        // 2 × 12000 = 24000 VND, 2 × 200 = 400 g.
        assertAll(() -> assertFalse(cart.isEmpty()), () -> assertEquals(24_000L, cart.getSubtotal()),
                () -> assertEquals(400, cart.getTotalWeightGrams()));
        cart.addItem(b, 3);
        // 24000 + 3 × 7000 = 45000, 400 + 3 × 150 = 850.
        assertAll(() -> assertEquals(45_000L, cart.getSubtotal()), () -> assertEquals(850, cart.getTotalWeightGrams()));
        checkPair(2, 50);
        checkEmpty(new Cart());
        cart.removeItem("A");
        cart.removeItem("B");
        checkEmpty(cart);
    }

    @Test
    void subtotalExceedsIntegerRange() {
        cart.addItem(new Product("L", "L", 1_000_000_000L, 1, 3), 3);
        // 3 × 1000000000 = 3000000000 VND.
        assertEquals(3_000_000_000L, cart.getSubtotal());
    }

    @Test
    void addRequiresProduct() {
        fill(2);
        // Lượng 1 nhưng thiếu sản phẩm.
        expectError(IllegalArgumentException.class, () -> cart.addItem(null, 1));
        checkPair(2, 50);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void addRequiresPositiveQuantity(int quantity) {
        fill(2);
        // Lượng thêm phải dương ngay cả khi A đã có trong giỏ.
        expectError(IllegalArgumentException.class, () -> cart.addItem(a, quantity));
        checkPair(2, 50);
    }

    @Test
    void addSameObjectAccumulatesInPlace() {
        fill(2);
        cart.addItem(a, 1);
        // 2 + 1 = 3 ở vị trí đầu, B vẫn đứng sau.
        checkPair(3, 50);
    }

    @Test
    void addUsesNewProductForStockPriceAndWeight() {
        Product old = new Product("A", "old", 9, 5, 1);
        Product fresh = new Product(new String("A"), "new", 12, 7, 50);
        cart.addItem(old, 1);
        cart.addItem(b, 3);
        cart.addItem(fresh, 49);
        // 1 + 49 = 50, bằng tồn mới và giới hạn giỏ, giữ vị trí A.
        assertPair(cart.getItems(), fresh, 50, b, 3);
        // 12 × 50 + 7000 × 3 = 21600; 7 × 50 + 150 × 3 = 800.
        assertAll(() -> assertEquals(21_600L, cart.getSubtotal()), () -> assertEquals(800, cart.getTotalWeightGrams()),
                () -> assertEquals(1, old.getStock()), () -> assertEquals(50, fresh.getStock()),
                () -> assertEquals(3, b.getStock()));
    }

    @Test
    void addLimitPrecedesNewProductStock() {
        fill(49);
        Product replacement = new Product("A", "new", 5, 2, 3);
        // 49 + 2 = 51 > 50 và 51 > 3. Giới hạn 50 được xét trước tồn.
        expectError(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
        checkPair(49, 50);
        // Không giữ chỗ ở đối tượng mới sau lỗi.
        assertEquals(3, replacement.getStock());
    }

    @Test
    void addInsufficientReplacementStockKeepsOldProduct() {
        fill(2);
        Product replacement = new Product("A", "new", 5, 2, 2);
        // 2 + 1 = 3, vừa vượt tồn mới 2, dù tồn cũ 50 đủ.
        expectError(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
        checkPair(2, 50);
        // Tồn mới vẫn là 2 sau khi bị từ chối.
        assertEquals(2, replacement.getStock());
    }

    @Test
    void addNewIdShortByOneLeavesEmptyCart() {
        // B tồn 3, yêu cầu 4 tức thiếu 1.
        expectError(InsufficientStockException.class, () -> cart.addItem(b, 4));
        checkEmpty(cart);
        // Không có lần trừ, tồn B vẫn 3.
        assertEquals(3, b.getStock());
    }

    @ParameterizedTest
    @CsvSource(value = {"missing,-1", "'',-1", "NULL,-1", "missing,0", "'',0",
                        "missing,51", "'',51", "NULL,51", "'  ',0"}, nullValues = "NULL")
    void updateChecksSignThenIdBeforeZeroOrLimit(String id, int quantity) {
        fill(2);
        Class<? extends RuntimeException> expected = quantity < 0
                ? IllegalArgumentException.class : NoSuchElementException.class;
        // -1 sai ở bước 1. Với 0/51 thiếu ID ở bước 2, chưa xét xóa hay giới hạn 50.
        expectError(expected, () -> cart.updateQuantity(id, quantity));
        checkPair(2, 50);
    }

    @Test
    void updateLimitPrecedesStock() {
        fill(2);
        // B tồn 3; 51 > 50 và 51 > 3, lỗi giới hạn được ưu tiên.
        expectError(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
        checkPair(2, 50);
    }

    @Test
    void updateShortByOne() {
        fill(2);
        // Yêu cầu 4 thay vì 3, vượt tồn B đúng 1.
        expectError(InsufficientStockException.class, () -> cart.updateQuantity("B", 4));
        checkPair(2, 50);
    }

    @Test
    void updateRechecksUnchangedQuantityAgainstCurrentStock() {
        fill(2);
        a.reduceStock(49);
        // A còn 50 - 49 = 1. Giữ lượng cũ 2 cũng thiếu 1.
        expectError(InsufficientStockException.class, () -> cart.updateQuantity("A", 2));
        checkPair(2, 1);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 50})
    void updateReplacesInsteadOfAdding(int quantity) {
        fill(2);
        cart.updateQuantity("A", quantity);
        // Lượng 1 hoặc 50 thay hẳn lượng 2, cả hai trong [1,50] và không vượt tồn 50.
        checkPair(quantity, 50);
    }

    @Test
    void updateZeroRemovesLastLineEvenAfterStockChange() {
        cart.addItem(b, 3);
        b.reduceStock(3);
        cart.updateQuantity("B", 0);
        // Từ một dòng xuống không dòng, tồn 3 - 3 vẫn 0.
        checkEmpty(cart);
        assertEquals(0, b.getStock());
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL", "''", "'  '", "unknown", "a", "' A '"}, nullValues = "NULL")
    void removeRequiresExactId(String id) {
        fill(2);
        // Không ID nào trong bảng bằng chính xác A hoặc B.
        expectError(NoSuchElementException.class, () -> cart.removeItem(id));
        checkPair(2, 50);
    }

    @Test
    void removalAndReinsertionPreserveRelativeOrder() {
        fill(2);
        Product c = new Product("C", "C", 2, 2, 1);
        cart.addItem(c, 1);
        cart.removeItem("B");
        // Xóa giữa [A,B,C] còn [A,C].
        assertPair(cart.getItems(), a, 2, c, 1);
        cart.addItem(b, 1);
        List<CartItem> result = cart.getItems();
        // Thêm lại B vào cuối, được [A,C,B].
        assertEquals(3, result.size());
        assertLine(result.get(0), a, 2);
        assertLine(result.get(1), c, 1);
        assertLine(result.get(2), b, 1);
    }

    @Test
    void itemsAreAnImmutableSnapshot() {
        fill(2);
        List<CartItem> before = cart.getItems();
        // Danh sách không rỗng, xóa chỉ số hợp lệ vẫn bị cấm.
        expectError(UnsupportedOperationException.class, () -> before.remove(0));
        checkPair(2, 50);
        var iterator = before.iterator();
        iterator.next();
        // Iterator cũng không thể xóa A.
        expectError(UnsupportedOperationException.class, iterator::remove);
        checkPair(2, 50);
        cart.updateQuantity("A", 1);
        cart.removeItem("B");
        // Bản chụp trước hai lần sửa vẫn là [A × 2, B × 3].
        assertPair(before, a, 2, b, 3);
        // Giỏ hiện tại chỉ còn A × 1.
        assertEquals(1, cart.getItems().size());
        assertLine(cart.getItems().get(0), a, 1);
    }
}
