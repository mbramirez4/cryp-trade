package cryptrade.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;
import cryptrade.Model.OrderType;
import cryptrade.Model.Cryptocurrency;

public class TransactionsProcessor {
    private Bag<Trader> users;
    private Queue<Operation> marketOrders;

    private static final Logger logger = LogManager.getLogger(TransactionsProcessor.class.getName());

    public TransactionsProcessor(){
        this(new HashBag<>(), new LinkedList<>());
    }

    public TransactionsProcessor(Bag<Trader> users, Queue<Operation> marketOrders){
        this.users = users;
        this.marketOrders = marketOrders;
    }

    private Trader getUserFromId(UUID userId){
        for (Trader userInBag : users.uniqueSet()){
            if (userInBag.getId().equals(userId)){
                return userInBag;
            }
        }

        return null;
    }

    public void buy(Trader user, Operation transaction, float amount){
        float transactionPrice = transaction.getTotalPriceCop();
        if(user.getBalanceCop() < transactionPrice){
            transaction.setRejected();
            return;
        }

        user.withdrawal(transactionPrice);
        
        Cryptocurrency coin = transaction.getTradingCryptocurrency();
        user.getPortfolio().increaseStock(coin, amount);
        
        transaction.setApproved();
    }

    public void sell(Trader user, Operation transaction, float amount){
        Cryptocurrency coin = transaction.getTradingCryptocurrency();
        if (user.getPortfolio().getStock(coin) < amount){
            transaction.setRejected();
            return;
        }
        
        float transactionPrice = transaction.getTotalPriceCop();
        user.getPortfolio().decreaseStock(coin, amount);
        user.deposit(transactionPrice);
        
        transaction.setApproved();
    }

    private void performOperation(Trader user, Operation transaction) throws Exception{
        OrderType orderType = transaction.getOrderType();
        float amount = transaction.getAmount();

        logger.info("Started performing transaction: " + transaction + "\nCurrent user state: " + user);
        if (orderType == OrderType.BUY) {
            buy(user, transaction, amount);
        } else if (orderType == OrderType.BUY) {
            sell(user, transaction, amount);
        }
        
        if (!transaction.isApproved()){
            throw new Exception("Transaction not approved");
        }
        
        logger.info("Finished performing transaction: " + transaction + "\nCurrent user state: " + user);
        
        user.registerOperation(transaction);
    }

    private void processTransaction() throws Exception{
        Operation transaction = marketOrders.poll();
        if (transaction == null){
            logger.warn("No market orders to process");
            return;
        }
        
        Trader user = getUserFromId(transaction.getUserId());
        if (user == null){
            throw new Exception("User not found for transaction: " + transaction);
        }
        
        try {
            performOperation(user, transaction);
        } catch (Exception e) {
            throw new Exception("Error processing transaction: " + transaction, e);
        }

        logger.info("Transaction processed successfully: " + transaction);
    }

    public void processTransactions(){
        logger.info("Processing market orders");
        while (!marketOrders.isEmpty()){
            try {
                processTransaction();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                continue;
            }
        }

        logger.info("All market orders processed");
    }
}
