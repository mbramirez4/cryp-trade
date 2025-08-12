package cryptrade.Interfaces;

import cryptrade.Model.Cryptocurrency;
import cryptrade.Model.Transaction;

public interface Trader {
    Transaction operate(String orderType, float amount, Cryptocurrency coin);
}
