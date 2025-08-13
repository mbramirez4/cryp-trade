package cryptrade.Model;

public class UserCoin {
    private Cryptocurrency coin;
    private float stock;

    public UserCoin(){}

    public UserCoin(Cryptocurrency coin){
        this(coin, 0);
    }

    public UserCoin(Cryptocurrency coin, float stock){
        this.coin = coin;
        this.stock = stock;
    }
    
    public float getStock() {
        return stock;
    }

    public void increaseStock(float amount) {
        this.stock += amount;
    }

    public void decreaseStock(float amount) {
        this.stock -= amount;
    }

    public Cryptocurrency getCoin() {
        return coin;
    }

    @Override
    public String toString() {
        return "UserCoin{" +
                "coinSymbol=" + coin.getSymbol() +
                ", coinName=" + coin.getName() +
                ", stock=" + stock +
                '}';
    }
}
