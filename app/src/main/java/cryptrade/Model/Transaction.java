package cryptrade.Model;

import java.util.UUID;
import java.util.Random;

import cryptrade.Interfaces.Operation;

public class Transaction implements Operation {
    private UUID id;
    private UUID userId;
    private float amount;
    private float currencyPriceUsd;
    private boolean approved;
    private OrderType orderType;
    private Cryptocurrency coin;

    public static final float USD_TO_COP = 4000;

    public Transaction(){};

    public Transaction(
        UUID id,
        UUID userId,
        float amount,
        OrderType orderType,
        Cryptocurrency coin
    ) throws IllegalArgumentException {
        if (orderType == OrderType.BUY) {
            this.orderType = OrderType.BUY;
        } else if (orderType == OrderType.SELL) {
            this.orderType = OrderType.SELL;
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

    public OrderType getOrderType(){
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

    public static Transaction createRandomOperation(UUID userId, float maxAmount, Cryptocurrency[] cryptocurrencies) throws IllegalArgumentException {
        if (cryptocurrencies.length == 0) {
            throw new IllegalArgumentException("Invalid cryptocurrencies array");
        }

        Random random = new Random();
        
        return new Transaction(
            UUID.randomUUID(),
            userId,
            random.nextFloat() * maxAmount,
            random.nextBoolean() ? OrderType.BUY : OrderType.SELL,
            cryptocurrencies[random.nextInt(cryptocurrencies.length)]
        );        
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", orderType='" + orderType + '\'' +
                ", coin=" + coin +
                ", approved=" + approved +
                ", currencyPriceUsd=" + currencyPriceUsd +
                '}';
    }
}