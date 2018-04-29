import org.json.JSONException;
import org.json.JSONObject;

public class Condition {
    private String columnName;
    private Tuple tuple;
    private String connector;

    public Condition(JSONObject condition) throws JSONException {
        columnName = condition.getString("ColumnName");
        tuple = new Tuple(condition.getString("Tuple"));
        connector = condition.getString("Connector");
    }

    public Condition(String _columnName, Tuple _tuple, String _connector) {
        columnName = _columnName;
        tuple = _tuple;
        connector = _connector;
    }

    public boolean isEmpty(){
        return true;
    }

    public String getColumnName(){
        return columnName;
    }

    public Tuple getTuple(){
        return tuple;
    }

    public String getConnector(){
        return connector;
    }

}
