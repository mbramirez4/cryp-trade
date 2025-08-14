package cryptrade.Model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.Stack;
import cryptrade.Interfaces.Operation;
import cryptrade.Interfaces.Trader;

class UserTest {
    
    private User user;
    private User userWithId;
    private UUID testId;
    private String testName;
    private float testBalance;
    
    @BeforeEach
    void setUp() {
        testName = "John Doe";
        testBalance = 1000.0f;
        testId = UUID.randomUUID();
        user = new User(testName, testBalance);
        userWithId = new User(testId, testName, testBalance);
    }
    
    @Test
    void testConstructorWithNameAndBalance() {
        assertNotNull(user);
        assertEquals(testName, user.getName());
        assertEquals(testBalance, user.getBalanceCop());
        assertNotNull(user.getId());
        assertNotNull(user.getPortfolio());
        assertNotNull(user.getTransactionHistory());
    }
    
    @Test
    void testConstructorWithIdNameAndBalance() {
        assertNotNull(userWithId);
        assertEquals(testId, userWithId.getId());
        assertEquals(testName, userWithId.getName());
        assertEquals(testBalance, userWithId.getBalanceCop());
        assertNotNull(userWithId.getPortfolio());
        assertNotNull(userWithId.getTransactionHistory());
    }
    
    @Test
    void testGetId() {
        assertNotNull(user.getId());
        assertTrue(user.getId() instanceof UUID);
    }
    
    @Test
    void testGetName() {
        assertEquals(testName, user.getName());
    }
    
    @Test
    void testGetBalanceCop() {
        assertEquals(testBalance, user.getBalanceCop());
    }
    
    @Test
    void testDeposit() {
        float depositAmount = 500.0f;
        float expectedBalance = testBalance + depositAmount;
        
        user.deposit(depositAmount);
        assertEquals(expectedBalance, user.getBalanceCop());
    }
    
    @Test
    void testDepositWithZero() {
        float originalBalance = user.getBalanceCop();
        user.deposit(0.0f);
        assertEquals(originalBalance, user.getBalanceCop());
    }
    
    @Test
    void testDepositWithNegative() {
        float originalBalance = user.getBalanceCop();
        user.deposit(-100.0f);
        assertEquals(originalBalance - 100.0f, user.getBalanceCop());
    }
    
    @Test
    void testWithdrawal() {
        float withdrawalAmount = 300.0f;
        float expectedBalance = testBalance - withdrawalAmount;
        
        user.withdrawal(withdrawalAmount);
        assertEquals(expectedBalance, user.getBalanceCop());
    }
    
    @Test
    void testWithdrawalWithZero() {
        float originalBalance = user.getBalanceCop();
        user.withdrawal(0.0f);
        assertEquals(originalBalance, user.getBalanceCop());
    }
    
    @Test
    void testWithdrawalWithNegative() {
        float originalBalance = user.getBalanceCop();
        user.withdrawal(-200.0f);
        assertEquals(originalBalance + 200.0f, user.getBalanceCop());
    }
    
    @Test
    void testGetPortfolio() {
        assertNotNull(user.getPortfolio());
        assertTrue(user.getPortfolio() instanceof Portfolio);
    }
    
    @Test
    void testGetTransactionHistory() {
        assertNotNull(user.getTransactionHistory());
        assertTrue(user.getTransactionHistory() instanceof Stack);
        assertTrue(user.getTransactionHistory().isEmpty());
    }
    
    @Test
    void testRegisterOperation() {
        // Create a mock operation
        Operation mockOperation = new Operation() {
            @Override
            public void setApproved() {}
            
            @Override
            public void setRejected() {}
            
            @Override
            public boolean isApproved() { return false; }
            
            @Override
            public float getCurrencyPriceUsd() { return 0.0f; }
            
            @Override
            public float getTotalPriceCop() { return 0.0f; }
            
            @Override
            public float getAmount() { return 0.0f; }
            
            @Override
            public UUID getUserId() { return UUID.randomUUID(); }
            
            @Override
            public OrderType getOrderType() { return OrderType.BUY; }
            
            @Override
            public Cryptocurrency getTradingCryptocurrency() { return null; }
        };
        
        user.registerOperation(mockOperation);
        
        Stack<Operation> history = user.getTransactionHistory();
        assertFalse(history.isEmpty());
        assertEquals(1, history.size());
        assertSame(mockOperation, history.peek());
    }
    
    @Test
    void testRegisterMultipleOperations() {
        Operation mockOperation1 = new Operation() {
            @Override
            public void setApproved() {}
            
            @Override
            public void setRejected() {}
            
            @Override
            public boolean isApproved() { return false; }
            
            @Override
            public float getCurrencyPriceUsd() { return 0.0f; }
            
            @Override
            public float getTotalPriceCop() { return 0.0f; }
            
            @Override
            public float getAmount() { return 0.0f; }
            
            @Override
            public UUID getUserId() { return UUID.randomUUID(); }
            
            @Override
            public OrderType getOrderType() { return OrderType.BUY; }
            
            @Override
            public Cryptocurrency getTradingCryptocurrency() { return null; }
        };
        
        Operation mockOperation2 = new Operation() {
            @Override
            public void setApproved() {}
            
            @Override
            public void setRejected() {}
            
            @Override
            public boolean isApproved() { return false; }
            
            @Override
            public float getCurrencyPriceUsd() { return 0.0f; }
            
            @Override
            public float getTotalPriceCop() { return 0.0f; }
            
            @Override
            public float getAmount() { return 0.0f; }
            
            @Override
            public UUID getUserId() { return UUID.randomUUID(); }
            
            @Override
            public OrderType getOrderType() { return OrderType.SELL; }
            
            @Override
            public Cryptocurrency getTradingCryptocurrency() { return null; }
        };
        
        user.registerOperation(mockOperation1);
        user.registerOperation(mockOperation2);
        
        Stack<Operation> history = user.getTransactionHistory();
        assertEquals(2, history.size());
        assertSame(mockOperation2, history.peek()); // LIFO order
    }
    
    @Test
    void testToString() {
        String result = user.toString();
        assertTrue(result.contains("User{"));
        assertTrue(result.contains("name='" + testName + "'"));
        assertTrue(result.contains("balanceCop="));
        assertTrue(result.contains("portfolio="));
    }
    
    @Test
    void testEqualsWithSameObject() {
        assertTrue(user.equals(user));
    }
    
    @Test
    void testEqualsWithNull() {
        assertFalse(user.equals(null));
    }
    
    @Test
    void testEqualsWithDifferentType() {
        String notAUser = "not a user";
        assertFalse(user.equals(notAUser));
    }
    
    @Test
    void testEqualsWithEqualUsers() {
        User sameUser = new User(user.getId(), testName, testBalance);
        assertTrue(user.equals(sameUser));
        assertTrue(sameUser.equals(user));
    }
    
    @Test
    void testEqualsWithDifferentUsers() {
        assertFalse(user.equals(userWithId));
        assertFalse(userWithId.equals(user));
    }
    
    @Test
    void testEqualsWithDifferentId() {
        User differentUser = new User(UUID.randomUUID(), testName, testBalance);
        assertFalse(user.equals(differentUser));
    }
    
    @Test
    void testImplementsTraderInterface() {
        assertTrue(user instanceof Trader);
    }
    
    @Test
    void testBalancePrecision() {
        user.deposit(0.01f);
        assertEquals(1000.01f, user.getBalanceCop());
        
        user.withdrawal(0.01f);
        assertEquals(1000.0f, user.getBalanceCop());
    }
} 