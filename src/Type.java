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
        switch (name.toLowerCase()){
            case "string":
                return "string";
            case "int":
            case "integer":
            case "float":
            case "double":
            case "short":
                return "number";
        }
        return Type.determineType(name);
    }

    public static String determineType(String value) {
        Pattern pattern = Pattern.compile("\\d+([.]\\d+)?");
        Matcher matcher = pattern.matcher(value);
        if(matcher.matches()) {
            return "number";
        }else{
            return "string";
        }
    }
}
