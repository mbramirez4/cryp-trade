package cryptrade.Model;

public class Cryptocurrency {
    private String id;
    private String symbol;
    private String name;
    protected String price_usd;

    public Cryptocurrency(){}

    public Cryptocurrency(String id, String symbol, String name, String price_usd){
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.price_usd = price_usd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPriceUsd() {
        return Float.valueOf(price_usd);
    }

    @Override
    public String toString() {
        return "Cryptocurrency{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", price_usd='" + price_usd + '\'' +
                '}';
    }

    public Cryptocurrency copy(){
        Cryptocurrency coin = new Cryptocurrency(this.id, this.symbol, this.name, this.price_usd);
        return coin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o instanceof Cryptocurrency) {
            Cryptocurrency coin = (Cryptocurrency) o;
            return (
                this.id.equals(coin.id)
                && this.symbol.equals(coin.symbol)
                && this.name.equals(coin.name)
                && this.price_usd.equals(coin.price_usd)
            );
        }

        return false;
    }

    // public static void main(String[] args) {
    //     Cryptocurrency coin = Cryptocurrency(
    //         "", "", "", "11.27"
    //     )
    //     System.out.println(coin.getPriceUsd())
    // }
    
}
