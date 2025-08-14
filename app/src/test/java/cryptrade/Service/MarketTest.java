package cryptrade.Service;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;
import cryptrade.Model.User;
import cryptrade.Model.Cryptocurrency;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketTest {

    @Mock
    private Trader mockTrader;
    
    @Mock
    private Cryptocurrency mockCryptocurrency;
    
    private Market market;
    private Bag<Trader> users;
    private Queue<Operation> marketOrders;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        users = new HashBag<>();
        marketOrders = new LinkedList<>();
        testUser = new User("TestUser", 1000.0f);
        
        // Mock FilesManager.getDataFromApi to return test data
        try (MockedStatic<FilesManager> mockedFilesManager = mockStatic(FilesManager.class)) {
            Cryptocurrency[] mockCryptoArray = {mockCryptocurrency};
            mockedFilesManager.when(() -> FilesManager.getDataFromApi(
                anyString(), 
                anyString(), 
                eq(Cryptocurrency[].class)
            )).thenReturn(mockCryptoArray);
            
            market = new Market(users, marketOrders);
        }
    }

    @Test
    void testDefaultConstructor() throws Exception {
        try (MockedStatic<FilesManager> mockedFilesManager = mockStatic(FilesManager.class)) {
            Cryptocurrency[] mockCryptoArray = {mockCryptocurrency};
            mockedFilesManager.when(() -> FilesManager.getDataFromApi(
                anyString(), 
                anyString(), 
                eq(Cryptocurrency[].class)
            )).thenReturn(mockCryptoArray);
            
            Market defaultMarket = new Market();
            assertNotNull(defaultMarket);
        }
    }

    @Test
    void testParameterizedConstructor() throws Exception {
        assertNotNull(market);
        assertEquals(users, getFieldValue(market, "users"));
        assertEquals(marketOrders, getFieldValue(market, "marketOrders"));
        assertNotNull(getFieldValue(market, "transactionsProcessor"));
    }

    @Test
    void testConstructorWithException() {
        try (MockedStatic<FilesManager> mockedFilesManager = mockStatic(FilesManager.class)) {
            mockedFilesManager.when(() -> FilesManager.getDataFromApi(
                anyString(), 
                anyString(), 
                eq(Cryptocurrency[].class)
            )).thenThrow(new RuntimeException("API Error"));
            
            assertThrows(Exception.class, () -> new Market());
        }
    }

    @Test
    void testRegisterUser() {
        assertFalse(users.contains(testUser));
        
        market.registerUser(testUser);
        
        assertTrue(users.contains(testUser));
        assertEquals(1, users.getCount(testUser));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        users.add(testUser);
        
        market.registerUser(testUser);
        
        assertEquals(1, users.getCount(testUser));
    }

    @Test
    void testEmulateMarketWithValidData() throws Exception {
        users.add(testUser);
        
        market = new Market(users, marketOrders);
        
        market.emulateMarket(20);
        
        assertTrue(marketOrders.isEmpty());
        assertTrue(testUser.getTransactionHistory().size() > 0);
    }

    @Test
    void testEmulateMarketWithNoCryptocurrencies() throws Exception {
        // Create market with empty crypto array
        try (MockedStatic<FilesManager> mockedFilesManager = mockStatic(FilesManager.class)) {
            mockedFilesManager.when(() -> FilesManager.getDataFromApi(
                anyString(), 
                anyString(), 
                eq(Cryptocurrency[].class)
            )).thenReturn(new Cryptocurrency[0]);
            
            Market emptyMarket = new Market(users, marketOrders);
            
            assertThrows(Exception.class, () -> emptyMarket.emulateMarket());
        }
    }

    @Test
    void testEmulateMarketWithNoUsers() {
        assertThrows(Exception.class, () -> market.emulateMarket());
    }

    @Test
    void testCreateReport() {
        String fileName = "test_report";
        
        // Mock FilesManager.saveToJson
        try (MockedStatic<FilesManager> mockedFilesManager = mockStatic(FilesManager.class)) {
            market.createReport(fileName);
            
            mockedFilesManager.verify(() -> FilesManager.saveToJson(fileName, users));
        }
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