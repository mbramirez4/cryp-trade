package cryptrade.Model;

import java.util.random;

public class Transaction {
    private String orderType;
    private float amount;
    private float priceUsd;
    private Cryptocurrency coin;
    private float currentPriceUsd;
    

    public static final String BUY_ORDER_TYPE = "COMPRA";
    public static final String SELL_ORDER_TYPE = "VENTA";

    public Transaction(){};

    public Transaction(String orderType, float amount){

    }

    private float fluctuatePrice() {
        
    }
}


