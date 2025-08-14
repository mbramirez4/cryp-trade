package cryptrade.Model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CryptocurrencyTest {
    
    private Cryptocurrency bitcoin;
    private Cryptocurrency ethereum;
    
    @BeforeEach
    void setUp() {
        bitcoin = new Cryptocurrency("bitcoin", "BTC", "Bitcoin", "45000.50");
        ethereum = new Cryptocurrency("ethereum", "ETH", "Ethereum", "3000.25");
    }
    
    @Test
    void testDefaultConstructor() {
        Cryptocurrency coin = new Cryptocurrency();
        assertNotNull(coin);
        assertNull(coin.getId());
        assertNull(coin.getSymbol());
        assertNull(coin.getName());
        assertThrows(NullPointerException.class, () -> coin.getPriceUsd());
    }
    
    @Test
    void testParameterizedConstructor() {
        Cryptocurrency coin = new Cryptocurrency("test", "TEST", "TestCoin", "100.00");
        assertEquals("test", coin.getId());
        assertEquals("TEST", coin.getSymbol());
        assertEquals("TestCoin", coin.getName());
        assertEquals(100.00f, coin.getPriceUsd());
    }
    
    @Test
    void testGetId() {
        assertEquals("bitcoin", bitcoin.getId());
        assertEquals("ethereum", ethereum.getId());
    }
    
    @Test
    void testGetSymbol() {
        assertEquals("BTC", bitcoin.getSymbol());
        assertEquals("ETH", ethereum.getSymbol());
    }
    
    @Test
    void testGetName() {
        assertEquals("Bitcoin", bitcoin.getName());
        assertEquals("Ethereum", ethereum.getName());
    }
    
    @Test
    void testGetPriceUsd() {
        assertEquals(45000.50f, bitcoin.getPriceUsd());
        assertEquals(3000.25f, ethereum.getPriceUsd());
    }
    
    @Test
    void testGetPriceUsdWithDecimalString() {
        Cryptocurrency coin = new Cryptocurrency("test", "TEST", "Test", "123.456");
        assertEquals(123.456f, coin.getPriceUsd());
    }
    
    @Test
    void testGetPriceUsdWithIntegerString() {
        Cryptocurrency coin = new Cryptocurrency("test", "TEST", "Test", "100");
        assertEquals(100.0f, coin.getPriceUsd());
    }
    
    @Test
    void testCopy() {
        Cryptocurrency copy = bitcoin.copy();
        
        // Test that it's a different object
        assertNotSame(bitcoin, copy);
        
        // Test that all values are copied correctly
        assertEquals(bitcoin.getId(), copy.getId());
        assertEquals(bitcoin.getSymbol(), copy.getSymbol());
        assertEquals(bitcoin.getName(), copy.getName());
        assertEquals(bitcoin.getPriceUsd(), copy.getPriceUsd());
    }
    
    @Test
    void testToString() {
        String expected = "Cryptocurrency{id='bitcoin', symbol='BTC', name='Bitcoin', price_usd='45000.50'}";
        assertEquals(expected, bitcoin.toString());
    }
    
    @Test
    void testEqualsWithSameObject() {
        assertTrue(bitcoin.equals(bitcoin));
    }
    
    @Test
    void testEqualsWithNull() {
        assertFalse(bitcoin.equals(null));
    }
    
    @Test
    void testEqualsWithDifferentType() {
        String notACoin = "not a coin";
        assertFalse(bitcoin.equals(notACoin));
    }
    
    @Test
    void testEqualsWithEqualObjects() {
        Cryptocurrency sameBitcoin = new Cryptocurrency("bitcoin", "BTC", "Bitcoin", "45000.50");
        assertTrue(bitcoin.equals(sameBitcoin));
        assertTrue(sameBitcoin.equals(bitcoin));
    }
    
    @Test
    void testEqualsWithDifferentObjects() {
        assertFalse(bitcoin.equals(ethereum));
        assertFalse(ethereum.equals(bitcoin));
    }
    
    @Test
    void testEqualsWithDifferentId() {
        Cryptocurrency differentId = new Cryptocurrency("different", "BTC", "Bitcoin", "45000.50");
        assertFalse(bitcoin.equals(differentId));
    }
    
    @Test
    void testEqualsWithDifferentSymbol() {
        Cryptocurrency differentSymbol = new Cryptocurrency("bitcoin", "DIFF", "Bitcoin", "45000.50");
        assertFalse(bitcoin.equals(differentSymbol));
    }
    
    @Test
    void testEqualsWithDifferentName() {
        Cryptocurrency differentName = new Cryptocurrency("bitcoin", "BTC", "Different", "45000.50");
        assertFalse(bitcoin.equals(differentName));
    }
    
    @Test
    void testEqualsWithDifferentPrice() {
        Cryptocurrency differentPrice = new Cryptocurrency("bitcoin", "BTC", "Bitcoin", "50000.00");
        assertFalse(bitcoin.equals(differentPrice));
    }
} 