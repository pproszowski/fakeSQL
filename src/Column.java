import org.json.JSONException;
import org.json.JSONObject;

public class Column {
    private String name;
    private String type;
    private int width;

    public Column(String _name, String _type){
        name = _name;
        type = _type;
        width = name.length();
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

    public int getWidth() {
        return width;
    }
}
