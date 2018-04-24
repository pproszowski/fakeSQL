import org.json.JSONException;
import org.json.JSONObject;

public class Column {
    private String name;
    private String type;

    public Column(String _name, String _type){
        name = _name;
        type = _type;
    }

    public Column(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("Name"), jsonObject.getString("Type"));
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
