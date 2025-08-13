package cryptrade.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;
import cryptrade.Model.OrderType;
import cryptrade.Model.Cryptocurrency;

public class TransactionsProcessor {
    private Bag<Trader> users;
    private Queue<Operation> marketOrders;

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

        if (orderType == OrderType.BUY) {
            buy(user, transaction, amount);
        } else if (orderType == OrderType.BUY) {
            sell(user, transaction, amount);
        }
        
        if (!transaction.isApproved()){
            throw new Exception("Transaction not approved");
        }

        user.registerOperation(transaction);
    }

    private void processTransaction(){
        Operation transaction = marketOrders.poll();
        if (transaction == null){
            // add log here no transactions to process
            return;
        }
        
        Trader user = getUserFromId(transaction.getUserId());
        if (user == null){
            // add log here no user
            return;
        }
        
        try {
            performOperation(user, transaction);
        } catch (Exception e) {
            // add log here exception
            return;
        }
        // add log here single transaction processed
    }

    public void processTransactions(){
        while (!marketOrders.isEmpty()){
            processTransaction();
        }
        // add log here all transactions processed
    }
}
