package cryptrade.Model;

import java.util.UUID;

public class Transaction {
    private UUID id;
    private String orderType;
    private float amount;
    private Cryptocurrency coin;
    private float priceUsd;
    private User user;
    private boolean approved;

    public static final String BUY_ORDER_TYPE = "COMPRA";
    public static final String SELL_ORDER_TYPE = "VENTA";

    public static final float USD_TO_COP = 4000;

    public Transaction(){};

    public Transaction(String orderType, float amount, User user) throws IllegalArgumentException {
        if (orderType == BUY_ORDER_TYPE) {
            this.orderType = BUY_ORDER_TYPE;
        } else if (orderType == SELL_ORDER_TYPE) {
            this.orderType = SELL_ORDER_TYPE;
        } else {
            throw new IllegalArgumentException("Invalid orderType: " + orderType);
        }
        
        this.amount = amount;
        this.user = user;
        setPriceUsd(coin.getPriceUsd());
    }

    private void setPriceUsd(float price) {
        double range = (Math.floor(Math.random() * 10) - 5) / 100.0;
        priceUsd = ((float) range) * price;
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

    public float getPriceUsd() {
        return priceUsd;
    }

    public boolean isApproved() {
        return approved;
    }

    public float getTotalPriceCop(){
        return priceUsd * amount * USD_TO_COP;
    }

    public void buy(float amount){
        float transactionPrice = getTotalPriceCop();
        if(user.getBalanceCop() < transactionPrice){
            approved = false;
            return;
        }

        approved = true;
        user.withdrawal(transactionPrice);
        // modify the user's wallet
    }

    public void sell(){
        float transactionPrice = getTotalPriceCop();
        // check in user's wallet if he has the coin and amount
        user.deposit(amount);
    }
}