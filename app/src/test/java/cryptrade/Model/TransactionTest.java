package cryptrade.Model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class TransactionTest {
    
    private UUID userId;
    private UUID transactionId;
    private Cryptocurrency bitcoin;
    private Cryptocurrency ethereum;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        bitcoin = new Cryptocurrency("bitcoin", "BTC", "Bitcoin", "45000.50");
        ethereum = new Cryptocurrency("ethereum", "ETH", "Ethereum", "3000.25");
    }
    
    @Test
    void testDefaultConstructor() {
        Transaction transaction = new Transaction();
        assertNotNull(transaction);
    }
    
    @Test
    void testParameterizedConstructorWithBuyOrder() {
        float amount = 2.5f;
        Transaction transaction = new Transaction(transactionId, userId, amount, OrderType.BUY, bitcoin);
        
        assertEquals(transactionId, transaction.getId());
        assertEquals(userId, transaction.getUserId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(OrderType.BUY, transaction.getOrderType());
        assertEquals(bitcoin, transaction.getTradingCryptocurrency());
        assertFalse(transaction.isApproved());
    }
    
    @Test
    void testParameterizedConstructorWithSellOrder() {
        float amount = 1.0f;
        Transaction transaction = new Transaction(transactionId, userId, amount, OrderType.SELL, ethereum);
        
        assertEquals(transactionId, transaction.getId());
        assertEquals(userId, transaction.getUserId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(OrderType.SELL, transaction.getOrderType());
        assertEquals(ethereum, transaction.getTradingCryptocurrency());
        assertFalse(transaction.isApproved());
    }
    
    @Test
    void testConstructorWithInvalidOrderType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(transactionId, userId, 1.0f, null, bitcoin);
        });
    }
    
    @Test
    void testGetUserId() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        assertEquals(userId, transaction.getUserId());
    }
    
    @Test
    void testGetAmount() {
        float amount = 3.75f;
        Transaction transaction = new Transaction(transactionId, userId, amount, OrderType.BUY, bitcoin);
        assertEquals(amount, transaction.getAmount());
    }
    
    @Test
    void testGetId() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        assertEquals(transactionId, transaction.getId());
    }
    
    @Test
    void testGetOrderType() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.SELL, bitcoin);
        assertEquals(OrderType.SELL, transaction.getOrderType());
    }
    
    @Test
    void testGetCurrencyPriceUsd() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        float price = transaction.getCurrencyPriceUsd();
        
        // Price should be +/-5% of the original price due to random delta
        float originalPrice = bitcoin.getPriceUsd();
        float minPrice = originalPrice * 0.95f;
        float maxPrice = originalPrice * 1.05f;
        
        assertTrue(price == minPrice || price == maxPrice);
    }
    
    @Test
    void testIsApprovedInitiallyFalse() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        assertFalse(transaction.isApproved());
    }
    
    @Test
    void testSetApproved() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        transaction.setApproved();
        assertTrue(transaction.isApproved());
    }
    
    @Test
    void testSetRejected() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        transaction.setRejected();
        assertFalse(transaction.isApproved());
    }
    
    @Test
    void testGetTotalPriceCop() {
        float amount = 2.0f;
        Transaction transaction = new Transaction(transactionId, userId, amount, OrderType.BUY, bitcoin);
        
        float totalPrice = transaction.getTotalPriceCop();
        float expectedPrice = transaction.getCurrencyPriceUsd() * amount * Transaction.USD_TO_COP;
        
        assertEquals(expectedPrice, totalPrice);
    }
    
    @Test
    void testGetTradingCryptocurrency() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        assertEquals(bitcoin, transaction.getTradingCryptocurrency());
    }
    
    @Test
    void testCreateRandomOperation() {
        Cryptocurrency[] cryptocurrencies = {bitcoin, ethereum};
        float maxAmount = 10.0f;
        
        Transaction randomTransaction = Transaction.createRandomOperation(userId, maxAmount, cryptocurrencies);
        
        assertNotNull(randomTransaction);
        assertEquals(userId, randomTransaction.getUserId());
        assertTrue(randomTransaction.getAmount() >= 0 && randomTransaction.getAmount() <= maxAmount);
        assertTrue(randomTransaction.getOrderType() == OrderType.BUY || randomTransaction.getOrderType() == OrderType.SELL);
        assertTrue(randomTransaction.getTradingCryptocurrency() == bitcoin || randomTransaction.getTradingCryptocurrency() == ethereum);
    }
    
    @Test
    void testCreateRandomOperationWithZeroMaxAmount() {
        Cryptocurrency[] cryptocurrencies = {bitcoin};
        Transaction randomTransaction = Transaction.createRandomOperation(userId, 0.0f, cryptocurrencies);
        
        assertNotNull(randomTransaction);
        assertEquals(0.0f, randomTransaction.getAmount());
    }
    
    @Test
    void testCreateRandomOperationWithEmptyArray() {
        Cryptocurrency[] cryptocurrencies = {};
        
        assertThrows(IllegalArgumentException.class, () -> {
            Transaction.createRandomOperation(userId, 10.0f, cryptocurrencies);
        });
    }
    
    @Test
    void testUSD_TO_COP_Constant() {
        assertEquals(4000.0f, Transaction.USD_TO_COP);
    }
    
    @Test
    void testToString() {
        Transaction transaction = new Transaction(transactionId, userId, 1.5f, OrderType.BUY, bitcoin);
        String result = transaction.toString();
        
        assertTrue(result.contains("Transaction{"));
        assertTrue(result.contains("id=" + transactionId));
        assertTrue(result.contains("userId=" + userId));
        assertTrue(result.contains("amount=1.5"));
        assertTrue(result.contains("orderType='BUY'"));
        assertTrue(result.contains("coin="));
        assertTrue(result.contains("approved=false"));
        assertTrue(result.contains("currencyPriceUsd="));
    }
    
    @Test
    void testPriceVariationRange() {
        // Test multiple transactions to ensure price variation is within expected range
        for (int i = 0; i < 100; i++) {
            Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
            float price = transaction.getCurrencyPriceUsd();
            float originalPrice = bitcoin.getPriceUsd();
            
            // Price should be +/-5% of original (0.95 to 1.05)
            assertTrue(price == originalPrice * 0.95f || price == originalPrice * 1.05f);
        }
    }
    
    @Test
    void testApprovalStateChanges() {
        Transaction transaction = new Transaction(transactionId, userId, 1.0f, OrderType.BUY, bitcoin);
        
        // Initially false
        assertFalse(transaction.isApproved());
        
        // Set to approved
        transaction.setApproved();
        assertTrue(transaction.isApproved());
        
        // Set to rejected
        transaction.setRejected();
        assertFalse(transaction.isApproved());
        
        // Set to approved again
        transaction.setApproved();
        assertTrue(transaction.isApproved());
    }
} 