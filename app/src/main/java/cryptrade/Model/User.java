package cryptrade.Model;

import java.nio.ByteBuffer;

import cryptrade.Interfaces.Trader;

public class User implements Trader {

    private String id;
    private String name;
    private float balanceCop;

    public User(String id, String name, float balanceCop){

        this.id = id;
        this.name = name;
        this.balanceCop = balanceCop;}

    public float getBalanceCop(){
        return balanceCop;
    }

    public void deposit(float moneyAmount){
        balanceCop += moneyAmount;
    }

    public void withdrawal(float moneyAmount){
        balanceCop -= moneyAmount;
    }

    public float Transaction(String orderType, float amount, Cryptocurrency coin){

        if (orderType == "BUY_ORDER_TYPE"){ Buy(amount), Sell(amount)}
    }
}