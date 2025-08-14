package cryptrade.Model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class UserCoinTest {
    
    private Cryptocurrency bitcoin;
    private UserCoin userCoin;
    private UserCoin userCoinWithStock;
    
    @BeforeEach
    void setUp() {
        bitcoin = new Cryptocurrency("bitcoin", "BTC", "Bitcoin", "45000.50");
        userCoin = new UserCoin(bitcoin);
        userCoinWithStock = new UserCoin(bitcoin, 2.5f);
    }
    
    @Test
    void testDefaultConstructor() {
        UserCoin coin = new UserCoin();
        assertNotNull(coin);
        assertNull(coin.getCoin());
        assertEquals(0.0f, coin.getStock());
    }
    
    @Test
    void testConstructorWithCoin() {
        assertNotNull(userCoin);
        assertEquals(bitcoin, userCoin.getCoin());
        assertEquals(0.0f, userCoin.getStock());
    }
    
    @Test
    void testConstructorWithCoinAndStock() {
        assertNotNull(userCoinWithStock);
        assertEquals(bitcoin, userCoinWithStock.getCoin());
        assertEquals(2.5f, userCoinWithStock.getStock());
    }
    
    @Test
    void testGetStock() {
        assertEquals(0.0f, userCoin.getStock());
        assertEquals(2.5f, userCoinWithStock.getStock());
    }
    
    @Test
    void testIncreaseStock() {
        float originalStock = userCoin.getStock();
        float increaseAmount = 1.5f;
        float expectedStock = originalStock + increaseAmount;
        
        userCoin.increaseStock(increaseAmount);
        assertEquals(expectedStock, userCoin.getStock());
    }
    
    @Test
    void testIncreaseStockWithZero() {
        float originalStock = userCoin.getStock();
        userCoin.increaseStock(0.0f);
        assertEquals(originalStock, userCoin.getStock());
    }
    
    @Test
    void testIncreaseStockWithNegative() {
        float originalStock = userCoin.getStock();
        userCoin.increaseStock(-1.0f);
        assertEquals(originalStock - 1.0f, userCoin.getStock());
    }
    
    @Test
    void testIncreaseStockMultipleTimes() {
        userCoin.increaseStock(1.0f);
        userCoin.increaseStock(2.0f);
        userCoin.increaseStock(0.5f);
        
        assertEquals(3.5f, userCoin.getStock());
    }
    
    @Test
    void testDecreaseStock() {
        userCoin.increaseStock(5.0f); // Set initial stock
        float originalStock = userCoin.getStock();
        float decreaseAmount = 2.0f;
        float expectedStock = originalStock - decreaseAmount;
        
        userCoin.decreaseStock(decreaseAmount);
        assertEquals(expectedStock, userCoin.getStock());
    }
    
    @Test
    void testDecreaseStockWithZero() {
        userCoin.increaseStock(3.0f); // Set initial stock
        float originalStock = userCoin.getStock();
        userCoin.decreaseStock(0.0f);
        assertEquals(originalStock, userCoin.getStock());
    }
    
    @Test
    void testDecreaseStockWithNegative() {
        userCoin.increaseStock(3.0f); // Set initial stock
        float originalStock = userCoin.getStock();
        userCoin.decreaseStock(-1.0f);
        assertEquals(originalStock + 1.0f, userCoin.getStock());
    }
    
    @Test
    void testDecreaseStockBelowZero() {
        userCoin.increaseStock(2.0f); // Set initial stock
        userCoin.decreaseStock(3.0f);
        assertEquals(-1.0f, userCoin.getStock());
    }
    
    @Test
    void testGetCoin() {
        assertEquals(bitcoin, userCoin.getCoin());
        assertEquals(bitcoin, userCoinWithStock.getCoin());
    }
    
    @Test
    void testStockPrecision() {
        userCoin.increaseStock(0.001f);
        assertEquals(0.001f, userCoin.getStock());
        
        userCoin.increaseStock(0.999f);
        assertEquals(1.0f, userCoin.getStock());
    }
    
    @Test
    void testToString() {
        String result = userCoin.toString();
        assertTrue(result.contains("UserCoin{"));
        assertTrue(result.contains("coinSymbol=" + bitcoin.getSymbol()));
        assertTrue(result.contains("coinName=" + bitcoin.getName()));
        assertTrue(result.contains("stock=" + userCoin.getStock()));
        
        String resultWithStock = userCoinWithStock.toString();
        assertTrue(resultWithStock.contains("stock=" + userCoinWithStock.getStock()));
    }
    
    @Test
    void testStockOperationsWithLargeNumbers() {
        userCoin.increaseStock(1000000.0f);
        assertEquals(1000000.0f, userCoin.getStock());
        
        userCoin.decreaseStock(500000.0f);
        assertEquals(500000.0f, userCoin.getStock());
    }
    
    @Test
    void testStockOperationsWithSmallDecimals() {
        userCoin.increaseStock(0.000001f);
        assertEquals(0.000001f, userCoin.getStock());
        
        userCoin.increaseStock(0.000002f);
        assertEquals(0.000003f, userCoin.getStock());
    }
} 