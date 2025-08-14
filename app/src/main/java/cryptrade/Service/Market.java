package cryptrade.Service;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;
import cryptrade.Model.Transaction;
import cryptrade.Model.Cryptocurrency;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

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
        try {
            cryptocurrencies = FilesManager.getDataFromApi(
                "https://api.coinlore.net/api/tickers/",
                "data",
                Cryptocurrency[].class
            );
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void registerUser(Trader user) {
        if (users.contains(user)) {
            logger.warn("User already registered: " + user);
            return;
        }

        users.add(user);
        logger.info("User registered: " + user);
    }

    public void emulateMarket() throws Exception {
        emulateMarket(10);
    }

    public void emulateMarket(int rounds) throws Exception {
        if (cryptocurrencies.length == 0 || users.size() == 0) {
            throw new Exception("No cryptocurrencies or users registered in the market");
        }

        Operation transaction;
        boolean participatesInRound;
        Random random = new Random();

        for (int i = 0; i < rounds; i++) {
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

    public void createReport(String fileName) {
        FilesManager.saveToJson(fileName, users);
    }
}
