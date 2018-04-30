import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Condition {
    private String columnName;
    private Tuple tuple;
    private String connector;
    private String symbol;

    public Condition(JSONObject condition) throws JSONException {
        columnName = condition.getString("ColumnName");
        tuple = new Tuple(condition.getString("Tuple"));
        connector = condition.getString("Connector");
        symbol = condition.getString("Symbol");
    }

    public Condition(String _columnName, Tuple _tuple, String _connector, String _symbol) {
        columnName = _columnName;
        tuple = _tuple;
        connector = _connector;
        symbol = _symbol;
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

    public static List<Condition> extractConditionsFromJSONArray(JSONArray jsonConditions) throws JSONException {
        List<Condition> _conditions = new ArrayList<>();
        for(int i = 0; i < jsonConditions.length(); i++){
            JSONObject jsonCondition = jsonConditions.getJSONObject(i);
            String columnName = jsonCondition.getString("ColumnName");
            Tuple tuple = new Tuple(jsonCondition.get("Tuple"));
            String connector = jsonCondition.getString("Connector");
            String symbol = jsonCondition.getString("Symbol");
            Condition condition = new Condition(columnName, tuple, connector, symbol);
            _conditions.add(condition);
        }
        return _conditions;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean getResult(Tuple _tuple){
        if(!tuple.getTypeName().equalsIgnoreCase(_tuple.getTypeName())){
            return false;
        }
        switch (symbol){
            case "=":
                return tuple.equals(_tuple);
            case ">":
                return (int) tuple.getValue() > (int) tuple.getValue();
            case "<":
                return (int) tuple.getValue() < (int) tuple.getValue();
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "(CONDITION):{" + "ColumnName:\"" + columnName + "\"," + "Tuple:\"" + tuple.toString() + "\",Connector:\"" +
                connector + "\",Symbol:\"" + symbol + "\"}";
    }
}
