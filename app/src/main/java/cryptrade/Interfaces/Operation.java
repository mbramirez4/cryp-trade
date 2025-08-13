package cryptrade.Interfaces;

import java.util.UUID;

import cryptrade.Model.Cryptocurrency;
import cryptrade.Model.OrderType;

public interface Operation {
    void setApproved();
    void setRejected();
    boolean isApproved();
    float getCurrencyPriceUsd();
    float getTotalPriceCop();
    float getAmount();
    UUID getUserId();
    OrderType getOrderType();
    Cryptocurrency getTradingCryptocurrency();
} 