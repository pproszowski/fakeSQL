import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableQuery extends Query{
    private JSONObject query;

    public TableQuery(JSONObject _query){
        query = _query;
    }

    @Override
    public Response execute(Storage storage) {
        Response response = new Response();
        try {
            String type = query.getString("Operation");
            Database database = storage.getCurrentDatabase();
            Table table = database.getTable(query.getString("TableName"));
            switch(type.toLowerCase()){
                case "select":
                        JSONArray columns = query.getJSONArray("ColumnNames");
                        List<String> columnNames = new ArrayList<>();
                        for(int i = 0; i < columns.length(); i++){
                            columnNames.add(columns.getString(i));
                        }
                        table.select(columnNames);
                    break;
                case "insert":
                        table.insert(new Record(query.getJSONObject("Record")));
                    break;
                case "delete":
                    table.delete(new Condition(query.getJSONObject("Condition")));
                    break;
                case "update":
                    Map<String, Tuple> newValues = new Gson().fromJson(
                            query.getJSONObject("NewValues").toString(), new TypeToken<HashMap<String, Tuple>>(){}.getType()
                    );
                    table.update(new Condition(query.getJSONObject("Condition")), newValues);
                    break;
            }
        } catch (CurrentDatabaseNotSetException | TableNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (DuplicateColumnsException e) {
            e.printStackTrace();
        } catch (ColumnNotFoundException e) {
            e.printStackTrace();
        } catch (DifferentTypesException e) {
            e.printStackTrace();
        }
        return response;
    }
}
