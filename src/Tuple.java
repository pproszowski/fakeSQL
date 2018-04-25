public class Tuple<T> {
    private T value;
    private String type;

    public Tuple(T _value){
        value = _value;
        determineType();
    }

    private void determineType() {
        String className = value.getClass().getName();
        switch(className.toLowerCase()){
            case "string":
                type = "string";
                break;
            case "integer":
            case "double":
            case "float":
                type = "number";
                break;
        }
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
