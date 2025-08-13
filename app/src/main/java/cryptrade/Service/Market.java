package cryptrade.Service;

import cryptrade.Model.Cryptocurrency;
import cryptrade.Model.Transaction;
import cryptrade.Model.User;

import java.util.UUID;

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
    public static User getUserFromId(UUID id, User[] usersArray){
        for (User user : usersArray) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        User[] usersArray = new User[5];
        usersArray[0] = new User("User 1", 1_000_000_000);
        usersArray[1] = new User("User 2", 2_000_000_000);
        usersArray[2] = new User("User 3", 300_000_000);
        usersArray[3] = new User("User 4", 400_000_000);
        usersArray[4] = new User("User 5", 500_000_000);

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

        Transaction transaction;
        User transactionUser;
        TransactionsProcessor transactionsProcessor;
        Queue<Transaction> marketOrders = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            System.out.println("Enqueuing Market Orders in the round " + (i + 1) + ":");
            for (User user : usersArray) {
                System.out.println("User: " + user.getName());
                transaction = getRandomTransaction(user, cryptocurrenciesArray);
                marketOrders.add(transaction);
            }

            System.out.println("\nProcessing Market Orders in the round " + (i + 1) + ":");
            while (!marketOrders.isEmpty()) {
                transaction = marketOrders.poll();
                System.out.println("Transaction: " + transaction);

                transactionUser = getUserFromId(transaction.getUserId(), usersArray);
                if (transactionUser == null) {
                    System.out.println("User not found for transaction: " + transaction);
                    continue;
                }

                transactionsProcessor = new TransactionsProcessor(transactionUser, transaction);
                transactionsProcessor.performOperation();
                
                System.out.println("Transaction approved: " + transaction.isApproved());
            }

            System.out.println();
        }
    }

    public static Transaction getRandomTransaction(User user, Cryptocurrency[] cryptocurrencies){
        Cryptocurrency coin = cryptocurrencies[new Random().nextInt(cryptocurrencies.length)];
        String orderType = new Random().nextBoolean() ? Transaction.BUY_ORDER_TYPE : Transaction.SELL_ORDER_TYPE;
        float amount = new Random().nextFloat() * 100;
        
        Transaction transaction = new Transaction(
            UUID.randomUUID(),
            orderType,
            amount,
            coin,
            user.getId()
        );
        
        return transaction;
    }
}
