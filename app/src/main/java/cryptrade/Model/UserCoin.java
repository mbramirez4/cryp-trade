package cryptrade.Model;

public class UserCoin extends Cryptocurrency {
    private float stock;

    public UserCoin(){}

    public UserCoin(Cryptocurrency coin){
        this(coin, 0);
    }

    public UserCoin(Cryptocurrency coin, float stock){
        super(coin.getId(), coin.getSymbol(), coin.getName(), coin.price_usd);
        
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
}
