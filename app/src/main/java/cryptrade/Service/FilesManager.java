package cryptrade.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import cryptrade.Model.User;

public class FilesManager {
    private static final Logger logger = LogManager.getLogger(FilesManager.class.getName());

    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(User.class, new UserSerializer())
        .create();

    private static String processFileName(String fileName) {
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }

        return fileName;
    }
    public static void saveToJson(String fileName, Object objectToSave) {
        Path filePath = Paths.get(processFileName(fileName));

        String jsonContent = gson.toJson(objectToSave);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T[] getDataFromApi(String apiUrl, String memberName, Class<T[]> typeofDst) throws Exception {
        logger.info("Fetching data started");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .header("Accept", "application/json")
                .build();
        logger.info("Http request created");

        T[] data = null;

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Http response received");

            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            data = gson.fromJson(
                jsonResponse.getAsJsonArray(memberName), typeofDst
            );
            logger.info("Data successfully parsed into the array");
        } catch (Exception e) {
            throw new Exception("Error fetching data: " + e.getMessage());
        }

        logger.info("Data fetched successfully");

        return data;
    }
}
