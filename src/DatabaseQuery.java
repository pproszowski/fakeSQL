import com.powder.Exception.CurrentDatabaseNotSetException;
import com.powder.Exception.TableAlreadyExistsException;
import com.powder.Exception.TableNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseQuery extends Query{
    private JSONObject query;

    public DatabaseQuery(JSONObject _query){
        query = _query;
    }

    @Override
    public Response execute(Storage storage) {
        Response response = new Response();
        try {
            String type = query.getString("Operation");
            Database database = storage.getCurrentDatabase();
            switch (type.toLowerCase()) {
                case "addtable":
                    database.addTable(new Table(query.getJSONObject("Table")));
                    break;
                case "deletetable":
                    database.removeTable(query.getString("Name"));
                    break;
            }
        } catch (CurrentDatabaseNotSetException e) {
            e.printStackTrace();
        } catch (TableNotFoundException e) {
            e.printStackTrace();
        } catch (TableAlreadyExistsException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }
}
