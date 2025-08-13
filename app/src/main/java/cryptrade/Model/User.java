package cryptrade.Model;

import java.util.UUID;

import cryptrade.Interfaces.Trader;
import cryptrade.Interfaces.Operation;

import java.util.Stack;

public class User implements Trader{
    private UUID id;
    private String name;
    private float balanceCop;
    
    private Portfolio portfolio;

    // A stack is used to store the transactions as it makes more sense
    // for the user to see the most recent transactions first (LIFO)
    private Stack<Operation> transactionHistory;
    
    public User(String name, float balanceCop){
        this(UUID.randomUUID(), name, balanceCop);
    }

    public User(UUID id, String name, float balanceCop){
        this.id = id;
        this.name = name;
        this.balanceCop = balanceCop;
        this.portfolio = new Portfolio();
        this.transactionHistory = new Stack<>();
    }

    public void registerOperation(Operation transaction){
        transactionHistory.push(transaction);
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

    public Stack<Operation> getTransactionHistory(){
        return transactionHistory;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balanceCop=" + String.format("%15.2f", balanceCop) +
                ", portfolio=" + portfolio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o instanceof User){
            User user = (User) o;
            return id.equals(user.id);
        }

        return false;
    }
}