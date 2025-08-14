package cryptrade.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTypeTest {
    
    @Test
    void testEnumValues() {
        OrderType[] values = OrderType.values();
        assertEquals(2, values.length);
        
        assertTrue(contains(values, OrderType.BUY));
        assertTrue(contains(values, OrderType.SELL));
    }
    
    @Test
    void testValueOf() {
        assertEquals(OrderType.BUY, OrderType.valueOf("BUY"));
        assertEquals(OrderType.SELL, OrderType.valueOf("SELL"));
    }
    
    @Test
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> {
            OrderType.valueOf("INVALID");
        });
    }
    
    @Test
    void testEnumComparison() {
        assertNotEquals(OrderType.BUY, OrderType.SELL);
        assertEquals(OrderType.BUY, OrderType.BUY);
        assertEquals(OrderType.SELL, OrderType.SELL);
    }
    
    @Test
    void testEnumOrdinal() {
        assertEquals(0, OrderType.BUY.ordinal());
        assertEquals(1, OrderType.SELL.ordinal());
    }
    
    @Test
    void testEnumToString() {
        assertEquals("BUY", OrderType.BUY.toString());
        assertEquals("SELL", OrderType.SELL.toString());
    }
    
    @Test
    void testEnumName() {
        assertEquals("BUY", OrderType.BUY.name());
        assertEquals("SELL", OrderType.SELL.name());
    }
    
    @Test
    void testEnumEquality() {
        OrderType buy1 = OrderType.BUY;
        OrderType buy2 = OrderType.BUY;
        OrderType sell = OrderType.SELL;
        
        assertSame(buy1, buy2);
        assertNotSame(buy1, sell);
        assertEquals(buy1, buy2);
        assertNotEquals(buy1, sell);
    }
    
    @Test
    void testEnumHashCode() {
        assertEquals(OrderType.BUY.hashCode(), OrderType.BUY.hashCode());
        assertNotEquals(OrderType.BUY.hashCode(), OrderType.SELL.hashCode());
    }
    
    @Test
    void testEnumSwitchStatement() {
        OrderType orderType = OrderType.BUY;
        String result = "";
        
        switch (orderType) {
            case BUY:
                result = "buy";
                break;
            case SELL:
                result = "sell";
                break;
        }
        
        assertEquals("buy", result);
    }
    
    // Helper method to check if an array contains a specific value
    private boolean contains(OrderType[] array, OrderType value) {
        for (OrderType item : array) {
            if (item == value) {
                return true;
            }
        }
        return false;
    }
} 