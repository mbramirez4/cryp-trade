package cryptrade.Model;

import java.util.UUID;
import java.util.Random;

public class Transaction {
    private UUID id;
    private String orderType;
    private float amount;
    private Cryptocurrency coin;
    private UUID userId;
    
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
        UUID userId
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
        this.userId = userId;
        setCurrencyPriceUsd(coin.getPriceUsd());
    }

    private void setCurrencyPriceUsd(float price) {
        float delta = new Random().nextBoolean() ? 0.05f : -0.05f;
        currencyPriceUsd = price * (1 + delta);
    }

    public UUID getUserId() {
        return userId;
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

    public void setApproved(){
        approved = true;
    }

    public void setRejected(){
        approved = false;
    }

    public float getTotalPriceCop(){
        return currencyPriceUsd * amount * USD_TO_COP;
    }

    public Cryptocurrency getTradingCryptocurrency() {
        return coin;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", orderType='" + orderType + '\'' +
                ", amount=" + amount +
                ", coin=" + coin +
                ", userId=" + userId +
                ", approved=" + approved +
                ", currencyPriceUsd=" + currencyPriceUsd +
                '}';
    }
}