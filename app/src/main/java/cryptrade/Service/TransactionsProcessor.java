package cryptrade.Service;

import cryptrade.Model.Cryptocurrency;
import cryptrade.Model.Transaction;

import cryptrade.Interfaces.Trader;

public class TransactionsProcessor {
    Trader user;
    Transaction transaction;

    public TransactionsProcessor(){
    }

    public TransactionsProcessor(Trader user, Transaction transaction){
        this.user = user;
        this.transaction = transaction;
    }

    public void buy(float amount){
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

    public void sell(float amount){
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

    public void performOperation(){
        String orderType = transaction.getOrderType();
        float amount = transaction.getAmount();

        if (orderType == Transaction.BUY_ORDER_TYPE) {
            buy(amount);
        } else if (orderType == Transaction.SELL_ORDER_TYPE) {
            sell(amount);
        }
        
        user.registerOperation(transaction);
    }
}
