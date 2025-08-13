package cryptrade.Service;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cryptrade.Model.Transaction;
import cryptrade.Model.User;

public class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonUser = new JsonObject();

        jsonUser.addProperty("name", user.getName());
        jsonUser.addProperty("id", user.getId().toString());
        jsonUser.addProperty("balance_usd", user.getBalanceCop() / Transaction.USD_TO_COP);
        jsonUser.add("portfolio", context.serialize(user.getPortfolio()));
        jsonUser.add("transactions_history", context.serialize(user.getTransactionHistory()));
        
        return jsonUser;
    }
}
