package cryptrade.Interfaces;

import cryptrade.Model.Portfolio;

public interface Trader {
    void deposit(float moneyAmount);
    void withdrawal(float moneyAmount);
    void registerOperation(Operation operation) throws IllegalArgumentException;
    float getBalanceCop();
    Portfolio getPortfolio();
}
