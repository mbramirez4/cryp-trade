package cryptrade.Model;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

public class Portfolio {
    // this should have been a bag of UserCoins but we didn't thought
    // about that in the beginning. A bag works fine here as we don't
    // need to know the order of the coins, only to know if we have
    // a certain cryptocurrency in the portfolio.
    Bag<UserCoin> userCoins;

    Portfolio(){
        this.userCoins = new HashBag<>();
    }

    private void addUserCoin(UserCoin userCoin){
        userCoins.add(userCoin);
    }

    public UserCoin getUserCoinItem(Cryptocurrency coin){
        for (UserCoin userCoin : userCoins){
            if (userCoin.getCoin().equals(coin)){
                return userCoin;
            }
        }

        return null;
    }

    // The coin object is a reference to the cryptocurrency in the
    // available cryptocurrencies, so we need to copy new objects in
    // the portfolio to avoid changing the objects in the available
    // cryptocurrencies ArrayList.
    public void increaseStock(Cryptocurrency coin, float amount){
        UserCoin userCoin = getUserCoinItem(coin);
        if (userCoin == null){
            UserCoin newUserCoin = new UserCoin(coin.copy());

            userCoin = newUserCoin;
            addUserCoin(userCoin);
        }

        userCoin.increaseStock(amount);
    }

    // returns false if the amount to decrease is greater than the stock
    // or if the coin is not in the portfolio
    public boolean decreaseStock(Cryptocurrency coin, float amount){
        UserCoin userCoin = getUserCoinItem(coin);
        if (userCoin == null){
            return false;
        }
        
        if (amount > getStock(coin)) {
            return false;
        }
        
        userCoin.decreaseStock(amount);
        return true;
    }

    public float getStock(Cryptocurrency coin){
        UserCoin userCoin = getUserCoinItem(coin);
        if (userCoin != null){
            return userCoin.getStock();
        }

        return 0;
    }
}
