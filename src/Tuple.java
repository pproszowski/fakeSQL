import com.powder.Exception.UnknownTypeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tuple<T> {
    private T value;
    private String type;

    public Tuple(T _value) {
        value = _value;
        type = Type.determineType(String.valueOf(value));
    }

    public String getType() {
        return type;
    }

    public T getValue(){
        return value;
    }


    @Override
    public String toString() {
        return value.toString();
    }
}
