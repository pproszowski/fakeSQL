import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableQuery extends Query{
    private String tableName;
    private String type;
    private List<String> whichColumns;
    private Record record;
    private Condition condition;
    private Map<String, Tuple> toUpdate;

    public TableQuery(String _tableName, String _type){
        tableName = _tableName;
        type = _type;
    }

    public TableQuery(String _tableName, String _type, List<String> _whichColumns){
        this(_tableName, _type);
        whichColumns = new ArrayList<>(_whichColumns);
    }

    public TableQuery(String _tableName, String _type, Record _record){
        this(_tableName, _type);
        record = new Record(_record);
    }

    public TableQuery(String _tableName, String _type, Condition _condition){
        this(_tableName, _type);
        condition = _condition;
    }

    public TableQuery(String _tableName, String _type, Condition _condition, Map<String,Tuple> _toUpdate){
        this(_tableName, _type, _condition);
        toUpdate = new HashMap<>(_toUpdate);
    }

    @Override
    public Response execute(Storage storage) {
        Response response = new Response();
        try {
            Database database = storage.getCurrentDatabase();
            Table table = database.getTable(tableName);
            switch(type.toLowerCase()){
                case "select":
                    try {
                        table.select(whichColumns);
                    } catch (DuplicateColumnsException e) {
                        e.printStackTrace();
                    }
                    break;
                case "insert":
                    try {
                        table.insert(record);
                    } catch (DifferentTypesException | ColumnNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case "delete":
                    table.delete(condition);
                    break;
                case "update":
                    table.update(condition, toUpdate);
                    break;
            }
        } catch (CurrentDatabaseNotSetException | TableNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }
}
