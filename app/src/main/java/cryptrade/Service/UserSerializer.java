package cryptrade.Service;

import java.lang.reflect.Type;
import java.util.Stack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cryptrade.Interfaces.Operation;
import cryptrade.Model.Transaction;
import cryptrade.Model.User;

public class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonUser = new JsonObject();

        jsonUser.addProperty("name", user.getName());
        jsonUser.addProperty("id", user.getId().toString());
        jsonUser.addProperty("balance_usd", user.getBalanceCop() / Transaction.USD_TO_COP);

        if (context == null) {
            return jsonUser;
        }
        
        jsonUser.add("portfolio", context.serialize(user.getPortfolio()));

        Stack<Operation> transactionHistory = user.getTransactionHistory();
        Operation[] operations = new Operation[transactionHistory.size()];
        int i = 0;
        while (!transactionHistory.isEmpty()) {
            operations[i] = transactionHistory.pop();
            i++;
        }

        jsonUser.add("transaction_history", context.serialize(operations));
        
        return jsonUser;
    }
}
