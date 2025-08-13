package cryptrade.Service;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;
import cryptrade.Model.Transaction;
import cryptrade.Model.Cryptocurrency;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Market {
    // Bags are used to store the users as we don't actually care
    // about the order of the users, we only need to store them,
    // and retrieve them from the uniqueSet when neeeded
    private Bag<Trader> users;

    // A queue is used to store market orders as it makes more
    // sense to process them in order (first market order in should
    // be processed first - FIFO)
    private Queue<Operation> marketOrders;
    private Cryptocurrency[] cryptocurrencies;
    private TransactionsProcessor transactionsProcessor;

    private static final Logger logger = LogManager.getLogger(Market.class.getName());

    public Market() throws Exception {
        this(new HashBag<>(), new LinkedList<>());
    }

    public Market(Bag<Trader> users, Queue<Operation> marketOrders) throws Exception {
        try {
            setCryptocurrencies();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        this.users = users;
        this.marketOrders = marketOrders;
        this.transactionsProcessor = new TransactionsProcessor(users, marketOrders);
    }

    private void setCryptocurrencies() throws Exception {
        String apiUrl = "https://api.coinlore.net/api/tickers/";
        
        logger.info("Fetching cryptocurrencies started");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .header("Accept", "application/json")
                .build();
        logger.info("Http request created");

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Http response received");

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            cryptocurrencies = gson.fromJson(
                jsonResponse.getAsJsonArray("data"), Cryptocurrency[].class
            );
            logger.info("Data successfully parsed into the cryptocurrencies array");
        } catch (Exception e) {
            throw new Exception("Error fetching cryptocurrencies: " + e.getMessage());
        }
        logger.info("Cryptocurrencies fetched successfully");
    }

    public void registerUser(Trader user) {
        if (users.contains(user)) {
            logger.warn("User already registered: " + user);
            return;
        }

        users.add(user);
        logger.info("User registered: " + user);
    }

    public void emulateMarket() {
        Operation transaction;
        boolean participatesInRound;
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            logger.info("Round " + (i + 1) + " started :");
            for (Trader user : users) {
                participatesInRound = random.nextBoolean();
                if (!participatesInRound) {
                    logger.info("User " + user + " did not participate in round " + (i + 1));
                    continue;
                }

                transaction = Transaction.createRandomOperation(user.getId(), 100, cryptocurrencies);
                marketOrders.add(transaction);
                logger.info("Market order added: " + transaction);
            }

            transactionsProcessor.processTransactions();
        }
    }
}
