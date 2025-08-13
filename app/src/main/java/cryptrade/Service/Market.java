package cryptrade.Service;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;
import cryptrade.Model.Transaction;
import cryptrade.Model.Cryptocurrency;
import cryptrade.Model.OrderType;
import cryptrade.Model.User;

import java.util.UUID;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Market {
    public static void market() {
        String apiUrl = "https://api.coinlore.net/api/tickers/";
        Cryptocurrency[] cryptocurrenciesArray;
        try {
            // Crear cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Crear solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            // Enviar solicitud y obtener respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Analizar la respuesta JSON
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            // Extraer la lista de criptomonedas
            cryptocurrenciesArray = gson.fromJson(
                    jsonResponse.getAsJsonArray("data"), Cryptocurrency[].class);
        } catch (Exception e) {
            System.err.println("Error al consumir la API: " + e.getMessage());
            return;
        }

        // Bags are used to store the users as we don't actually care
        // about the order of the users, we only need to store them,
        // and retrieve them from the uniqueSet when neeeded
        Bag<Trader> users = new HashBag<>();
        users.add(new User("User 1", 1_000_000_000));
        users.add(new User("User 2", 2_000_000_000));
        users.add(new User("User 3", 300_000_000));
        users.add(new User("User 4", 400_000_000));
        users.add(new User("User 5", 500_000_000));

        // A queue is used to store market orders as it makes more
        // sense to process them in order (first market order in should
        // be processed first - FIFO)
        Queue<Operation> marketOrders = new LinkedList<>();

        TransactionsProcessor transactionsProcessor = new TransactionsProcessor(users, marketOrders);

        Operation transaction;

        for (int i = 0; i < 5; i++) {
            System.out.println("Enqueuing Market Orders in the round " + (i + 1) + ":");
            for (Trader user : users) {
                System.out.println("User: " + user);
                transaction = getRandomTransaction(user, cryptocurrenciesArray);
                marketOrders.add(transaction);
            }

            System.out.println("\nProcessing Market Orders in the round " + (i + 1) + ":");
            transactionsProcessor.processTransactions();
        }
    }

    private static Transaction getRandomTransaction(Trader user, Cryptocurrency[] cryptocurrencies){
        Cryptocurrency coin = cryptocurrencies[new Random().nextInt(cryptocurrencies.length)];
        OrderType orderType = new Random().nextBoolean() ? OrderType.BUY : OrderType.SELL;
        float amount = new Random().nextFloat() * 100;
        
        Transaction transaction = new Transaction(
            UUID.randomUUID(),
            user.getId(),
            amount,
            orderType,
            coin
        );
        
        return transaction;
    }
}
