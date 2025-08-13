package cryptrade.Model;

import java.util.UUID;

import cryptrade.Interfaces.Trader;

public class User{
    private UUID id;
    private String name;
    private float balanceCop;
    
    private Portfolio portfolio; 
    private Transaction[] transactionHistory;
    private int transactionHistorySize;

    public User(String name, float balanceCop){
        this(UUID.randomUUID(), name, balanceCop);
    }

    private static final int DEFAULT_CAPACITY = 2;

    public User(UUID id, String name, float balanceCop){
        this.id = id;
        this.name = name;
        this.balanceCop = balanceCop;
        this.portfolio = new Portfolio();
        this.transactionHistory = new Transaction[DEFAULT_CAPACITY];
        this.transactionHistorySize = 0;
    }

    public void addTransaction(Transaction transaction){
        if (transactionHistorySize >= transactionHistory.length){
            Transaction[] newTransactions = new Transaction[transactionHistory.length * 2];
            System.arraycopy(transactionHistory, 0, newTransactions, 0, transactionHistory.length);
            
            transactionHistory = newTransactions;
        }

        transactionHistory[transactionHistorySize] = transaction;
        transactionHistorySize++;
    }

    public UUID getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public float getBalanceCop(){
        return balanceCop;
    }

    public void deposit(float moneyAmount){
        balanceCop += moneyAmount;
    }

    public void withdrawal(float moneyAmount){
        balanceCop -= moneyAmount;
    }

    public Portfolio getPortfolio(){
        return portfolio;
    }
}