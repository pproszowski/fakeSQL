import org.json.JSONException;
import org.json.JSONObject;

public class Type {
    private String name;
    private int limit;

    public Type(String _name, int _limit){
        name = _name;
        limit = _limit;
    }

    public Type(JSONObject type) throws JSONException {
        name = type.getString("Name");
        limit = type.getInt("Limit");
    }

    public int getLimit() {
        return limit;
    }

    public String getName() {
        return name;
    }
}
