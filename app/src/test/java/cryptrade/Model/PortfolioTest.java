package cryptrade.Model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {
    
    private Portfolio portfolio;
    private Cryptocurrency bitcoin;
    private Cryptocurrency ethereum;
    private Cryptocurrency litecoin;
    
    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        bitcoin = new Cryptocurrency("bitcoin", "BTC", "Bitcoin", "45000.50");
        ethereum = new Cryptocurrency("ethereum", "ETH", "Ethereum", "3000.25");
        litecoin = new Cryptocurrency("litecoin", "LTC", "Litecoin", "150.75");
    }
    
    @Test
    void testConstructor() {
        assertNotNull(portfolio);
        assertEquals(0, portfolio.getStock(bitcoin));
        assertEquals(0, portfolio.getStock(ethereum));
    }
    
    @Test
    void testGetStockWithNonExistentCoin() {
        assertEquals(0.0f, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testIncreaseStockNewCoin() {
        float amount = 2.5f;
        portfolio.increaseStock(bitcoin, amount);
        
        assertEquals(amount, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testIncreaseStockExistingCoin() {
        float initialAmount = 1.0f;
        float additionalAmount = 2.5f;
        float expectedTotal = initialAmount + additionalAmount;
        
        portfolio.increaseStock(bitcoin, initialAmount);
        portfolio.increaseStock(bitcoin, additionalAmount);
        
        assertEquals(expectedTotal, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testIncreaseStockWithZero() {
        portfolio.increaseStock(bitcoin, 0.0f);
        assertEquals(0.0f, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testIncreaseStockWithNegative() {
        boolean result = portfolio.increaseStock(bitcoin, -1.0f);
        assertEquals(0.0f, portfolio.getStock(bitcoin));
        assertFalse(result);
    }
    
    @Test
    void testIncreaseStockMultipleCoins() {
        portfolio.increaseStock(bitcoin, 2.0f);
        portfolio.increaseStock(ethereum, 5.0f);
        portfolio.increaseStock(litecoin, 10.0f);
        
        assertEquals(2.0f, portfolio.getStock(bitcoin));
        assertEquals(5.0f, portfolio.getStock(ethereum));
        assertEquals(10.0f, portfolio.getStock(litecoin));
    }
    
    @Test
    void testDecreaseStockSuccess() {
        portfolio.increaseStock(bitcoin, 5.0f);
        boolean result = portfolio.decreaseStock(bitcoin, 2.0f);
        
        assertTrue(result);
        assertEquals(3.0f, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testDecreaseStockExactAmount() {
        portfolio.increaseStock(bitcoin, 3.0f);
        boolean result = portfolio.decreaseStock(bitcoin, 3.0f);
        
        assertTrue(result);
        assertEquals(0.0f, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testDecreaseStockInsufficientStock() {
        portfolio.increaseStock(bitcoin, 2.0f);
        boolean result = portfolio.decreaseStock(bitcoin, 5.0f);
        
        assertFalse(result);
        assertEquals(2.0f, portfolio.getStock(bitcoin)); // Stock unchanged
    }
    
    @Test
    void testDecreaseStockNonExistentCoin() {
        boolean result = portfolio.decreaseStock(bitcoin, 1.0f);
        
        assertFalse(result);
    }
    
    @Test
    void testDecreaseStockWithZero() {
        portfolio.increaseStock(bitcoin, 3.0f);
        boolean result = portfolio.decreaseStock(bitcoin, 0.0f);
        
        assertTrue(result);
        assertEquals(3.0f, portfolio.getStock(bitcoin)); // Stock unchanged
    }
    
    @Test
    void testDecreaseStockWithNegative() {
        portfolio.increaseStock(bitcoin, 3.0f);
        boolean result = portfolio.decreaseStock(bitcoin, -1.0f);
        
        assertFalse(result);
        assertEquals(3.0f, portfolio.getStock(bitcoin)); // Stock increased
    }
    
    @Test
    void testGetUserCoinItemExisting() {
        portfolio.increaseStock(bitcoin, 2.0f);
        UserCoin userCoin = portfolio.getUserCoinItem(bitcoin);
        
        assertNotNull(userCoin);
        assertEquals(bitcoin, userCoin.getCoin());
        assertEquals(2.0f, userCoin.getStock());
    }
    
    @Test
    void testGetUserCoinItemNonExistent() {
        UserCoin userCoin = portfolio.getUserCoinItem(bitcoin);
        assertNull(userCoin);
    }
    
    @Test
    void testGetUserCoinItemAfterDecrease() {
        portfolio.increaseStock(bitcoin, 5.0f);
        portfolio.decreaseStock(bitcoin, 3.0f);
        
        UserCoin userCoin = portfolio.getUserCoinItem(bitcoin);
        assertNotNull(userCoin);
        assertEquals(2.0f, userCoin.getStock());
    }
    
    @Test
    void testStockPrecision() {
        portfolio.increaseStock(bitcoin, 0.001f);
        assertEquals(0.001f, portfolio.getStock(bitcoin));
        
        portfolio.increaseStock(bitcoin, 0.999f);
        assertEquals(1.0f, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testLargeStockOperations() {
        portfolio.increaseStock(bitcoin, 1000000.0f);
        assertEquals(1000000.0f, portfolio.getStock(bitcoin));
        
        boolean result = portfolio.decreaseStock(bitcoin, 500000.0f);
        assertTrue(result);
        assertEquals(500000.0f, portfolio.getStock(bitcoin));
    }
    
    @Test
    void testToString() {
        String result = portfolio.toString();
        assertTrue(result.contains("Portfolio{"));
        assertTrue(result.contains("userCoins="));
    }
    
    @Test
    void testToStringWithCoins() {
        portfolio.increaseStock(bitcoin, 2.0f);
        portfolio.increaseStock(ethereum, 5.0f);
        
        String result = portfolio.toString();
        assertTrue(result.contains("Portfolio{"));
        assertTrue(result.contains("userCoins="));
    }
    
    @Test
    void testMultipleOperationsOnSameCoin() {
        portfolio.increaseStock(bitcoin, 1.0f);
        portfolio.increaseStock(bitcoin, 2.0f);
        portfolio.increaseStock(bitcoin, 3.0f);
        portfolio.decreaseStock(bitcoin, 1.5f);
        
        assertEquals(4.5f, portfolio.getStock(bitcoin));
    }
} 