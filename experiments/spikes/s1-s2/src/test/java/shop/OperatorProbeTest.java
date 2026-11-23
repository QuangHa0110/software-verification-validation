package shop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorProbeTest {
    @Test
    void recordsSumWhenBothConditionsHold() {
        OperatorProbe probe = new OperatorProbe();
        assertTrue(probe.evaluate(3, 2, true));
        assertEquals(5, probe.recorded());
    }

    @Test
    void rejectsEqualityAndDisabledInputWithoutChangingRecordedSum() {
        OperatorProbe probe = new OperatorProbe();
        probe.evaluate(4, 2, true);
        assertFalse(probe.evaluate(3, 3, true));
        assertFalse(probe.evaluate(3, 2, false));
        assertEquals(6, probe.recorded());
    }
}
