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

    public Query getQueryJSON(JSONObject jsonQuery) throws JSONException {
        type = jsonQuery.getString( "Target");
        switch(type.toLowerCase()){
            case "storage":
                return new StorageQuery();
                break;
            case "database":
                return new DatabaseQuery();
                break;
            case "table":
                return new TableQuery();
                break;
        }
    }
}
