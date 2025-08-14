package cryptrade.Service;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;

import cryptrade.Model.User;
import cryptrade.Model.Transaction;
import cryptrade.Model.Cryptocurrency;
import cryptrade.Model.OrderType;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionsProcessorTest {
    private Cryptocurrency testCryptocurrency;
    private TransactionsProcessor transactionsProcessor;
    private Bag<Trader> users;
    private Queue<Operation> marketOrders;
    private User testUser;

    @BeforeEach
    void setUp() {
        users = new HashBag<>();
        marketOrders = new LinkedList<>();
        testCryptocurrency = new Cryptocurrency("1", "TEST", "TestCryptocurrency", "1.0");
        testUser = new User("TestUser", 1_000_000.0f);
        transactionsProcessor = new TransactionsProcessor(users, marketOrders);
    }

    @Test
    void testDefaultConstructor() {
        TransactionsProcessor defaultProcessor = new TransactionsProcessor();
        assertNotNull(defaultProcessor);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(transactionsProcessor);
        assertEquals(users, getFieldValue(transactionsProcessor, "users"));
        assertEquals(marketOrders, getFieldValue(transactionsProcessor, "marketOrders"));
    }

    @Test
    void testBuyWithSufficientBalance() {
        float amount = (testUser.getBalanceCop() / Transaction.USD_TO_COP) / testCryptocurrency.getPriceUsd() * 0.5f;
        
        Operation testOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            amount,
            OrderType.BUY,
            testCryptocurrency
        );
        
        float initialBalance = testUser.getBalanceCop();
        float totalPriceCop = testOperation.getTotalPriceCop();
        
        transactionsProcessor.buy(testUser, testOperation, amount);
        
        assertTrue(testOperation.isApproved());
        assertEquals(initialBalance - totalPriceCop, testUser.getBalanceCop(), 0.01f);
        
        float actualStock = testUser.getPortfolio().getStock(testCryptocurrency);
        assertEquals(amount, actualStock, 0.01f);
    }

    @Test
    void testBuyWithInsufficientBalance() {
        float amount = (testUser.getBalanceCop() / Transaction.USD_TO_COP) / testCryptocurrency.getPriceUsd() * 2.0f;
        
        Operation testOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            amount,
            OrderType.BUY,
            testCryptocurrency
        );
        
        float initialBalance = testUser.getBalanceCop();
        
        transactionsProcessor.buy(testUser, testOperation, amount);
        
        assertFalse(testOperation.isApproved());
        assertEquals(initialBalance, testUser.getBalanceCop(), 0.01f);
        assertEquals(0.0f, testUser.getPortfolio().getStock(testCryptocurrency), 0.01f);
    }

    @Test
    void testSellWithSufficientStock() {
        float amount = 10.0f;
        
        Operation testOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            amount,
            OrderType.SELL,
            testCryptocurrency
        );
        
        // Add stock to portfolio first
        testUser.getPortfolio().increaseStock(testCryptocurrency, amount);
        float initialBalance = testUser.getBalanceCop();
        float totalPriceCop = testOperation.getTotalPriceCop();
        
        transactionsProcessor.sell(testUser, testOperation, amount);
        
        assertTrue(testOperation.isApproved());
        assertEquals(initialBalance + totalPriceCop, testUser.getBalanceCop(), 0.01f);
        assertEquals(0.0f, testUser.getPortfolio().getStock(testCryptocurrency), 0.01f);
    }

    @Test
    void testSellWithInsufficientStock() {
        float amount = 10.0f;
        float availableStock = 5.0f;
        
        Operation testOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            amount,
            OrderType.SELL,
            testCryptocurrency
        );
        
        testUser.getPortfolio().increaseStock(testCryptocurrency, availableStock);
        float initialBalance = testUser.getBalanceCop();
        
        transactionsProcessor.sell(testUser, testOperation, amount);
        
        assertFalse(testOperation.isApproved());
        assertEquals(initialBalance, testUser.getBalanceCop(), 0.01f);
        assertEquals(availableStock, testUser.getPortfolio().getStock(testCryptocurrency), 0.01f);
    }

    @Test
    void testProcessTransactionsWithValidTransaction() throws Exception {
        users.add(testUser);
        
        Operation buyOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            10.0f,
            OrderType.BUY,
            testCryptocurrency
        );

        Operation sellOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            5.0f,
            OrderType.BUY,
            testCryptocurrency
        );
        
        marketOrders.add(buyOperation);
        marketOrders.add(sellOperation);
        
        transactionsProcessor.processTransactions();
        
        // Verify
        assertTrue(marketOrders.isEmpty());
        assertTrue(buyOperation.isApproved());
        assertTrue(sellOperation.isApproved());

        // Check LIFO order of transactions
        Stack<Operation> transactionsHistory = testUser.getTransactionHistory();
        assertEquals(2, transactionsHistory.size());
        assertEquals(sellOperation, transactionsHistory.pop());
        assertEquals(buyOperation, transactionsHistory.pop());
    }

    @Test
    void testProcessTransactionsWithEmptyQueue() {
        transactionsProcessor.processTransactions();
        
        assertTrue(marketOrders.isEmpty());
    }

    @Test
    void testProcessTransactionsWithUserNotFound() {
        // Setup
        Operation testOperation = new Transaction(
            UUID.randomUUID(),
            UUID.randomUUID(), // Random UUID that doesn't exist in users
            10.0f,
            OrderType.BUY,
            testCryptocurrency
        );
        
        marketOrders.add(testOperation);
        
        // Execute - should handle exception gracefully and continue
        assertDoesNotThrow(() -> transactionsProcessor.processTransactions());
        
        // Verify
        assertTrue(marketOrders.isEmpty());
    }

    @Test
    void testProcessTransactionsWithMultipleTransactions() throws Exception {
        users.add(testUser);
        
        Operation operation1 = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            5.0f,
            OrderType.BUY,
            testCryptocurrency
        );
        
        Operation operation2 = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            3.0f,
            OrderType.SELL,
            testCryptocurrency
        );
        
        testUser.getPortfolio().increaseStock(testCryptocurrency, 5.0f);
        
        marketOrders.add(operation1);
        marketOrders.add(operation2);
        
        transactionsProcessor.processTransactions();
        
        assertTrue(marketOrders.isEmpty());
        assertTrue(operation1.isApproved());
        assertTrue(operation2.isApproved());
        assertEquals(7.0f, testUser.getPortfolio().getStock(testCryptocurrency), 0.01f);
    }

    @Test
    void testBuyOperationUpdatesPortfolioCorrectly() {
        float amount = 15.0f;
        
        Operation testOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            amount,
            OrderType.BUY,
            testCryptocurrency
        );
        
        float initialStock = testUser.getPortfolio().getStock(testCryptocurrency);
        assertEquals(0.0f, initialStock, 0.01f);
        
        transactionsProcessor.buy(testUser, testOperation, amount);
        
        assertEquals(amount, testUser.getPortfolio().getStock(testCryptocurrency), 0.01f);
    }

    @Test
    void testSellOperationUpdatesPortfolioCorrectly() {
        float amount = 8.0f;
        
        Operation testOperation = new Transaction(
            UUID.randomUUID(),
            testUser.getId(),
            amount,
            OrderType.SELL,
            testCryptocurrency
        );
        
        testUser.getPortfolio().increaseStock(testCryptocurrency, 20.0f);
        float initialStock = testUser.getPortfolio().getStock(testCryptocurrency);
        
        transactionsProcessor.sell(testUser, testOperation, amount);
        
        assertEquals(initialStock - amount, testUser.getPortfolio().getStock(testCryptocurrency), 0.01f);
    }

    @Test
    void testPortfolioOperations() {
        float amount = 5.0f;
        
        boolean increaseResult = testUser.getPortfolio().increaseStock(testCryptocurrency, amount);
        assertTrue(increaseResult);
        
        float stock = testUser.getPortfolio().getStock(testCryptocurrency);
        assertEquals(amount, stock, 0.01f);
        
        assertNotNull(testUser.getPortfolio().getUserCoinItem(testCryptocurrency));
    }

    // Helper method to access private fields for testing
    private Object getFieldValue(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field value", e);
        }
    }
} 