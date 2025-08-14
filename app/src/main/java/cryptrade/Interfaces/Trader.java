package cryptrade.Interfaces;

import java.util.UUID;

import cryptrade.Model.Portfolio;

public interface Trader {
    void deposit(float moneyAmount);
    void withdrawal(float moneyAmount);
    void registerOperation(Operation operation);
    float getBalanceCop();
    UUID getId();
    Portfolio getPortfolio();
}
