package cryptrade.Model;

import java.util.UUID;

import cryptrade.Interfaces.Trader;

public class User{
    private UUID id;
    private String name;
    private Portfolio portfolio; 
    private float balanceCop;

    public User(String name, float balanceCop){
        this(UUID.randomUUID(), name, balanceCop);
    }

    public User(UUID id, String name, float balanceCop){
        this.id = id;
        this.name = name;
        this.balanceCop = balanceCop;
        this.portfolio = new Portfolio();
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