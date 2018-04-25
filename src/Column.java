import org.json.JSONException;
import org.json.JSONObject;

public class Column {
    private String name;
    private Type type;
    private int width;

    public Column(String _name, Type _type){
        name = _name;
        type = _type;
        width = name.length();
    }

    public Column(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("Name"), new Type(jsonObject.getJSONObject("Type")));
    }

    public Column(String _name , String _typeName) {
        this(_name, new Type(_typeName, 255));
    }

    public int getLimit(){
        return type.getLimit();
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public void expandWidth(int _width){
        width = _width;
    }
}
