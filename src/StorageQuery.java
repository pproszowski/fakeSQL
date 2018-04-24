import com.powder.Exception.DatabaseAlreadyExistsException;
import com.powder.Exception.DatabaseNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

public class StorageQuery extends Query {
    private JSONObject query;

    protected StorageQuery(JSONObject _query){
        query = _query;
    }

    @Override
    public Response execute(Storage storage) throws JSONException {
        Response response = new Response();
        String type = query.getString("Operation");
        try {
            switch (type.toLowerCase()) {
                case "adddatabase":
                    Database database = new Database(query.getJSONObject("Database"));
                    storage.addDatabase(database);
                    break;
                case "setcurrentdatabase":
                    String name = query.getString("Name");
                    storage.setCurrentDatabase(name);
                    response.setMessage("Changed database context to '" + name + "'");
                    break;
                case "removedatabase":
                    storage.deleteDatabase(query.getString("CurrentDatabaseName"));
                    break;
            }
        } catch (DatabaseAlreadyExistsException | DatabaseNotFoundException e) {
            response.setValid(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }
}
