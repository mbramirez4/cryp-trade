package cryptrade.Model;

import java.util.UUID;

public class Transaction {
    private UUID id;
    private String orderType;
    private float amount;
    private Cryptocurrency coin;
    private User user;
    
    private boolean approved;
    private float currencyPriceUsd;

    public static final String BUY_ORDER_TYPE = "COMPRA";
    public static final String SELL_ORDER_TYPE = "VENTA";

    public static final float USD_TO_COP = 4000;

    public Transaction(){};

    public Transaction(
        UUID id,
        String orderType,
        float amount,
        Cryptocurrency coin,
        User user
    ) throws IllegalArgumentException {
        if (orderType == BUY_ORDER_TYPE) {
            this.orderType = BUY_ORDER_TYPE;
        } else if (orderType == SELL_ORDER_TYPE) {
            this.orderType = SELL_ORDER_TYPE;
        } else {
            throw new IllegalArgumentException("Invalid orderType: " + orderType);
        }
        
        this.id = id;
        this.amount = amount;
        this.coin = coin;
        this.user = user;
        setCurrencyPriceUsd(coin.getPriceUsd());
    }

    private void setCurrencyPriceUsd(float price) {
        double range = (Math.floor(Math.random() * 10) - 5) / 100.0;
        currencyPriceUsd = ((float) range) * price;
    }

    public float getAmount(){
        return amount;
    }

    public UUID getId(){
        return id;
    }

    public String getOrderType(){
        return orderType;
    }

    public float getCurrencyPriceUsd() {
        return currencyPriceUsd;
    }

    public boolean isApproved() {
        return approved;
    }

    public float getTotalPriceCop(){
        return currencyPriceUsd * amount * USD_TO_COP;
    }

    public void buy(float amount){
        float transactionPrice = getTotalPriceCop();
        if(user.getBalanceCop() < transactionPrice){
            approved = false;
            return;
        }

        user.withdrawal(transactionPrice);
        user.getPortfolio().increaseStock(coin, amount);
        approved = true;
    }

    public void sell(){
        if (user.getPortfolio().getStock(coin) < amount){
            approved = false;
            return;
        }
        
        float transactionPrice = getTotalPriceCop();
        user.getPortfolio().decreaseStock(coin, amount);
        user.deposit(transactionPrice);
        approved = true;
    }
}