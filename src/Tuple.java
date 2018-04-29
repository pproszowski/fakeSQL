public class Tuple<T> {
    private T value;
    private String typeName;

    public Tuple(T _value) {
        value = _value;
        typeName = Type.determineType(String.valueOf(value));
    }

    public String getTypeName() {
        return typeName;
    }

    public T getValue(){
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Tuple) {
            if (value instanceof String) {
                String one = (String) this.value;
                String two = ((Tuple) obj).value.toString();
                return one.equalsIgnoreCase(two);
            }
        }
        if(this.value.equals(((Tuple) obj).getValue())){
            return this.typeName.equalsIgnoreCase(((Tuple) obj).getTypeName());
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode() * typeName.hashCode();
    }

    @Override
    public String toString() {
        return "(TUPLE): {" + value.toString() + " : " + typeName + "}";
    }
}
