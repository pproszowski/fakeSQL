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
                    storage.setCurrentDatabase(query.getString("Name"));
                    break;
                case "removedatabase":
                    storage.deleteDatabase(query.getString("CurrentDatabaseName"));
                    break;
            }
        } catch (DatabaseAlreadyExistsException e) {
            e.printStackTrace();
        } catch (DatabaseNotFoundException e) {
            e.printStackTrace();
        }

        return response;
    }
}
