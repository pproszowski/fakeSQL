import org.json.JSONException;
import org.json.JSONObject;

public class Column {
    private String name;
    private Type type;
    private int width;


    public Column(String _name, Type _type){
        name = _name;
        type = new Type(_type.getName(), _type.getLimit());
        width = name.length();
    }

    public Column(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("Name"), new Type(jsonObject.getJSONObject("Type")));
        if(jsonObject.has("Width")){
            width = jsonObject.getInt("Width");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Column){
            return name.equalsIgnoreCase(((Column) obj).name) && type.equals(((Column) obj).type) && width == ((Column) obj).width;
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode() + width;
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

    @Override
    public String toString() {
        return "(COLUMN): {" + name + " : " + type.toString() + " : " + width + "}";
    }
}
