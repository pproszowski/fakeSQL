import com.powder.Exception.BadQueryTypeException;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryFactory {
    private String type;
    private static QueryFactory ourInstance = new QueryFactory();
    public static QueryFactory getInstance() {
        return ourInstance;
    }
    private QueryFactory() {
    }

    public Query getQuery(JSONObject jsonQuery) throws JSONException, BadQueryTypeException {
        type = jsonQuery.getString( "Target");
        JSONObject query = jsonQuery.getJSONObject("Query");
        switch(type.toLowerCase()){
            case "storage":
                return new StorageQuery(query);
            case "database":
                return new DatabaseQuery(query);
            case "table":
                return new TableQuery(query);
            default:
                throw new BadQueryTypeException();
        }
    }
}
