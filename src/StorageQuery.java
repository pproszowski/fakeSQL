import com.powder.Exception.DatabaseAlreadyExistsException;
import com.powder.Exception.DatabaseNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

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
                case "dropdatabase":
                    //TODO: it's not implemented int Parser yet, you know what to do!
                    storage.deleteDatabase(query.getString("Name"));
                    response.setMessage("Database " + "\"" + query.getString("Name") + "\"" + " has been dropped");
                    break;
            }
            response.setValid(true);
        } catch (DatabaseAlreadyExistsException | DatabaseNotFoundException e) {
            response.setValid(false);
            response.setMessage(e.getMessage());
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return response;
    }
}
