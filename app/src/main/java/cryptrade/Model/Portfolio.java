package cryptrade.Model;

public class Portfolio {
    UserCoin[] userCoins;
    int size;

    private static final int DEFAULT_CAPACITY = 2;

    Portfolio(){
        this.size = 0;
        this.userCoins = new UserCoin[DEFAULT_CAPACITY];
    }

    private void addUserCoin(UserCoin userCoin){
        // check if the array still has space, if not it creates a new array
        if (size >= userCoins.length){
            UserCoin[] newUserCoins = new UserCoin[userCoins.length * 2];
            System.arraycopy(userCoins, 0, newUserCoins, 0, userCoins.length);
            userCoins = newUserCoins;
        }

        userCoins[size] = userCoin;
        size++;
    }

    public int getUserCoinIndex(Cryptocurrency coin){
        for (int i = 0; i < size; i++){
            if (userCoins[i].getCoin().equals(coin)){
                return i-1;
            }
        }

        return -1;
    }

    public UserCoin getUserCoinItem(Cryptocurrency coin){
        int index = getUserCoinIndex(coin);
        if (index >= 0){
            return userCoins[index];
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
    public boolean decreaseStock(Cryptocurrency coin, float amount){
        if (amount > getStock(coin)){
            return false;
        }

        UserCoin userCoin = getUserCoinItem(coin);
        if (userCoin != null){
            userCoin.decreaseStock(amount);
        }

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
