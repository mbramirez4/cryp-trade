package cryptrade.Service;

import cryptrade.Model.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSerializerTest {

    @Mock
    private JsonSerializationContext mockContext;
    
    @Mock
    private JsonElement mockPortfolioElement;
    
    @Mock
    private JsonElement mockTransactionsElement;
    
    private UserSerializer userSerializer;
    private User testUser;
    private UUID testUserId;
    private Type mockType;

    @BeforeEach
    void setUp() {
        userSerializer = new UserSerializer();
        testUserId = UUID.randomUUID();
        testUser = new User(testUserId, "TestUser", 1000.0f);
        mockType = mock(Type.class);
    }

    @Test
    void testSerializeWithValidUser() {
        // Setup mocks
        when(mockContext.serialize(testUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(testUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(testUser, mockType, mockContext);
        
        // Verify
        assertNotNull(result);
        assertTrue(result.isJsonObject());
        
        JsonObject jsonUser = result.getAsJsonObject();
        
        // Verify all required properties are present
        assertTrue(jsonUser.has("name"));
        assertTrue(jsonUser.has("id"));
        assertTrue(jsonUser.has("balance_usd"));
        assertTrue(jsonUser.has("portfolio"));
        assertTrue(jsonUser.has("transactions_history"));
        
        // Verify property values
        assertEquals("TestUser", jsonUser.get("name").getAsString());
        assertEquals(testUserId.toString(), jsonUser.get("id").getAsString());
        assertEquals(0.25f, jsonUser.get("balance_usd").getAsFloat(), 0.01f); // 1000 COP / 4000 = 0.25 USD
        assertEquals(mockPortfolioElement, jsonUser.get("portfolio"));
        assertEquals(mockTransactionsElement, jsonUser.get("transactions_history"));
    }

    @Test
    void testSerializeWithZeroBalance() {
        // Setup user with zero balance
        User zeroBalanceUser = new User("ZeroBalanceUser", 0.0f);
        
        when(mockContext.serialize(zeroBalanceUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(zeroBalanceUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(zeroBalanceUser, mockType, mockContext);
        
        // Verify
        JsonObject jsonUser = result.getAsJsonObject();
        assertEquals(0.0f, jsonUser.get("balance_usd").getAsFloat(), 0.01f);
    }

    @Test
    void testSerializeWithLargeBalance() {
        // Setup user with large balance
        User largeBalanceUser = new User("LargeBalanceUser", 1000000.0f); // 1M COP
        
        when(mockContext.serialize(largeBalanceUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(largeBalanceUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(largeBalanceUser, mockType, mockContext);
        
        // Verify
        JsonObject jsonUser = result.getAsJsonObject();
        assertEquals(250.0f, jsonUser.get("balance_usd").getAsFloat(), 0.01f); // 1M COP / 4000 = 250 USD
    }

    @Test
    void testSerializeWithDecimalBalance() {
        // Setup user with decimal balance
        User decimalBalanceUser = new User("DecimalBalanceUser", 1234.56f);
        
        when(mockContext.serialize(decimalBalanceUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(decimalBalanceUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(decimalBalanceUser, mockType, mockContext);
        
        // Verify
        JsonObject jsonUser = result.getAsJsonObject();
        assertEquals(0.30864f, jsonUser.get("balance_usd").getAsFloat(), 0.00001f); // 1234.56 COP / 4000 = 0.30864 USD
    }

    @Test
    void testSerializeWithNegativeBalance() {
        // Setup user with negative balance (if such a case is possible)
        User negativeBalanceUser = new User("NegativeBalanceUser", -500.0f);
        
        when(mockContext.serialize(negativeBalanceUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(negativeBalanceUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(negativeBalanceUser, mockType, mockContext);
        
        // Verify
        JsonObject jsonUser = result.getAsJsonObject();
        assertEquals(-0.125f, jsonUser.get("balance_usd").getAsFloat(), 0.01f); // -500 COP / 4000 = -0.125 USD
    }

    @Test
    void testSerializeWithSpecialCharactersInName() {
        // Setup user with special characters in name
        User specialNameUser = new User("Test-User_123", 1000.0f);
        
        when(mockContext.serialize(specialNameUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(specialNameUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(specialNameUser, mockType, mockContext);
        
        // Verify
        JsonObject jsonUser = result.getAsJsonObject();
        assertEquals("Test-User_123", jsonUser.get("name").getAsString());
    }

    @Test
    void testSerializeWithEmptyName() {
        // Setup user with empty name
        User emptyNameUser = new User("", 1000.0f);
        
        when(mockContext.serialize(emptyNameUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(emptyNameUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(emptyNameUser, mockType, mockContext);
        
        // Verify
        JsonObject jsonUser = result.getAsJsonObject();
        assertEquals("", jsonUser.get("name").getAsString());
    }

    @Test
    void testSerializeWithNullContext() {
        // This test verifies that the serializer handles null context gracefully
        // In a real scenario, this shouldn't happen, but it's good to test edge cases
        
        // Execute - should not throw exception
        assertDoesNotThrow(() -> {
            JsonElement result = userSerializer.serialize(testUser, mockType, null);
            // Result might be null or throw exception, but the method should complete
        });
    }

    @Test
    void testSerializeMaintainsJsonStructure() {
        // Setup
        when(mockContext.serialize(testUser.getPortfolio())).thenReturn(mockPortfolioElement);
        when(mockContext.serialize(testUser.getTransactionHistory())).thenReturn(mockTransactionsElement);
        
        // Execute
        JsonElement result = userSerializer.serialize(testUser, mockType, mockContext);
        
        // Verify JSON structure
        JsonObject jsonUser = result.getAsJsonObject();
        
        // Check that all properties are primitive types (except portfolio and transactions_history)
        assertTrue(jsonUser.get("name").isJsonPrimitive());
        assertTrue(jsonUser.get("id").isJsonPrimitive());
        assertTrue(jsonUser.get("balance_usd").isJsonPrimitive());
        assertFalse(jsonUser.get("portfolio").isJsonPrimitive());
        assertFalse(jsonUser.get("transactions_history").isJsonPrimitive());
        
        // Verify data types
        assertTrue(jsonUser.get("name").getAsJsonPrimitive().isString());
        assertTrue(jsonUser.get("id").getAsJsonPrimitive().isString());
        assertTrue(jsonUser.get("balance_usd").getAsJsonPrimitive().isNumber());
    }

    @Test
    void testSerializeWithDifferentUserIds() {
        // Test with different UUIDs to ensure proper serialization
        UUID[] testIds = {
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff")
        };
        
        for (UUID testId : testIds) {
            User userWithId = new User(testId, "UserWithId", 1000.0f);
            
            when(mockContext.serialize(userWithId.getPortfolio())).thenReturn(mockPortfolioElement);
            when(mockContext.serialize(userWithId.getTransactionHistory())).thenReturn(mockTransactionsElement);
            
            // Execute
            JsonElement result = userSerializer.serialize(userWithId, mockType, mockContext);
            
            // Verify
            JsonObject jsonUser = result.getAsJsonObject();
            assertEquals(testId.toString(), jsonUser.get("id").getAsString());
        }
    }
} 