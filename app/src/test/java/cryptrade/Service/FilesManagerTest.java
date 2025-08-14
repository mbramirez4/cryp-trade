package cryptrade.Service;

import cryptrade.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilesManagerTest {

    @Mock
    private HttpClient mockHttpClient;
    
    @Mock
    private HttpResponse<String> mockHttpResponse;
    
    private User testUser;
    private String testFileName;
    private Path testFilePath;

    @BeforeEach
    void setUp() {
        testUser = new User("TestUser", 1000.0f);
        testFileName = "test_output";
        testFilePath = Path.of(testFileName + ".json");
        
        // Clean up any existing test files
        try {
            Files.deleteIfExists(testFilePath);
        } catch (IOException e) {
            // Ignore if file doesn't exist
        }
    }

    @Test
    void testSaveToJsonWithValidObject() throws IOException {
        // Execute
        FilesManager.saveToJson(testFileName, testUser);
        
        // Verify file was created
        assertTrue(Files.exists(testFilePath));
        
        // Verify file content
        String fileContent = Files.readString(testFilePath);
        assertNotNull(fileContent);
        assertTrue(fileContent.contains("TestUser"));
        assertTrue(fileContent.contains(testUser.getId().toString()));
        
        // Clean up
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void testSaveToJsonWithFileNameWithoutExtension() throws IOException {
        String fileNameWithoutExt = "test_output_no_ext";
        Path expectedPath = Path.of(fileNameWithoutExt + ".json");
        
        // Clean up any existing test files
        try {
            Files.deleteIfExists(expectedPath);
        } catch (IOException e) {
            // Ignore if file doesn't exist
        }
        
        // Execute
        FilesManager.saveToJson(fileNameWithoutExt, testUser);
        
        // Verify file was created with .json extension
        assertTrue(Files.exists(expectedPath));
        
        // Clean up
        Files.deleteIfExists(expectedPath);
    }

    @Test
    void testSaveToJsonWithFileNameWithExtension() throws IOException {
        String fileNameWithExt = "test_output.json";
        Path expectedPath = Path.of(fileNameWithExt);
        
        // Clean up any existing test files
        try {
            Files.deleteIfExists(expectedPath);
        } catch (IOException e) {
            // Ignore if file doesn't exist
        }
        
        // Execute
        FilesManager.saveToJson(fileNameWithExt, testUser);
        
        // Verify file was created without adding another .json extension
        assertTrue(Files.exists(expectedPath));
        
        // Clean up
        Files.deleteIfExists(expectedPath);
    }

    @Test
    void testSaveToJsonWithComplexObject() throws IOException {
        // Create a more complex object to test
        testUser.getPortfolio().increaseStock(createMockCryptocurrency("BTC", 50000.0f), 1.5f);
        
        // Execute
        FilesManager.saveToJson(testFileName, testUser);
        
        // Verify file was created and contains portfolio data
        assertTrue(Files.exists(testFilePath));
        String fileContent = Files.readString(testFilePath);
        assertTrue(fileContent.contains("portfolio"));
        
        // Clean up
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void testGetDataFromApiWithValidResponse() throws Exception {
        // Mock HTTP response
        String mockJsonResponse = """
            {
                "data": [
                    {"id": "1", "name": "Bitcoin", "price_usd": "50000.0"},
                    {"id": "2", "name": "Ethereum", "price_usd": "3000.0"}
                ]
            }
            """;
        
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            
            when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);
            when(mockHttpResponse.body()).thenReturn(mockJsonResponse);
            
            // Execute
            Object[] result = FilesManager.getDataFromApi(
                "https://api.coinlore.net/api/tickers/",
                "data",
                Object[].class
            );
            
            // Verify
            assertNotNull(result);
            assertEquals(2, result.length);
        }
    }

    @Test
    void testGetDataFromApiWithEmptyData() throws Exception {
        // Mock HTTP response with empty data
        String mockJsonResponse = """
            {
                "data": []
            }
            """;
        
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            
            when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);
            when(mockHttpResponse.body()).thenReturn(mockJsonResponse);
            
            // Execute
            Object[] result = FilesManager.getDataFromApi(
                "https://api.coinlore.net/api/tickers/",
                "data",
                Object[].class
            );
            
            // Verify
            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    // Helper method to create mock cryptocurrency for testing
    private cryptrade.Model.Cryptocurrency createMockCryptocurrency(String name, float price) {
        try {
            // Using reflection to create Cryptocurrency instance since we don't have access to its constructor
            Class<?> cryptoClass = Class.forName("cryptrade.Model.Cryptocurrency");
            return (cryptrade.Model.Cryptocurrency) cryptoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Return null if we can't create the instance
            return null;
        }
    }
} 