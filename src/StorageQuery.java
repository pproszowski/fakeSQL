import com.powder.Exception.CurrentDatabaseNotSetException;
import com.powder.Exception.DatabaseAlreadyExistsException;
import com.powder.Exception.DatabaseNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
                    database.saveToFile();
                    storage.saveToFile();
                    response.setMessage("Database " + "\"" + query.getJSONObject("Database").getString("Name") + "\"" + " has been added to storage.");
                    break;
                case "setcurrentdatabase":
                    String name = query.getString("Name");
                    storage.setCurrentDatabase(name);
                    storage.saveToFile();
                    response.setMessage("Changed database context to '" + name + "'.");
                    break;
                case "dropdatabase":
                    storage.deleteDatabase(query.getString("Name"));
                    storage.saveToFile();
                    response.setMessage("Database " + "\"" + query.getString("Name") + "\"" + " has been dropped.");
                    break;
            }
            response.setValid(true);
        } catch (DatabaseAlreadyExistsException | DatabaseNotFoundException | IOException e) {
            response.setValid(false);
            response.setMessage(e.getMessage());
            return response;
        }

        return response;
    }
}
