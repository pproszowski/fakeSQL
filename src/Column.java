public class Column {
    private String name;
    private String type;

    public Column(String _name, String _type){
        name = _name;
        type = _type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
