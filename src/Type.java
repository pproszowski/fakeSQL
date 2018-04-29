import com.powder.Exception.InvalidTypeException;
import com.powder.Exception.UnknownTypeException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Type {
    private String name;
    private int limit;


    public Type(String _name, int _limit){
        name = determineType(_name);
        limit = _limit;
    }

    public Type(JSONObject type) throws JSONException {
        name = determineType(type.getString("Name"));
        limit = type.getInt("Limit");
    }

    public int getLimit() {
        return limit;
    }

    public String getName() {
        return Type.determineType(name.toLowerCase());
    }

    public static String determineType(String value) {
        switch (value.toLowerCase()){
            case "string":
            case "varchar":
                return "string";
            case "number":
            case "int":
            case "integer":
            case "float":
            case "double":
                return "number";
                default:
                    Pattern pattern = Pattern.compile("\\d+([.]\\d+)?");
                    Matcher matcher = pattern.matcher(value);
                    if(matcher.matches()) {
                        return "number";
                    }else{
                        return "string";
                    }
        }
    }
    @Override
    public String toString() {
        return "(TYPE) : {" + name +  " : " + limit + "}";
    }
}
