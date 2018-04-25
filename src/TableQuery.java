import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.powder.Exception.*;
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
            Table table = database.getTable(query.getString("Name"));
            switch(type.toLowerCase()){
                case "select":
                        JSONArray columns = query.getJSONArray("ColumnNames");
                        List<String> columnNames = new ArrayList<>();
                        for(int i = 0; i < columns.length(); i++){
                            columnNames.add(columns.getString(i));
                        }
                        table.select(columnNames);
                        response.setMessage(table.show());
                    break;
                case "insert":
                        table.insert(new Record(query.getJSONObject("Record")));
                    break;
                case "delete":
                    table.delete(new Condition(query.getJSONObject("Condition")));
                    break;
                case "update":
                    Map<String, Tuple> changes = new Gson().fromJson(
                            query.getJSONObject("Changes").toString(), new TypeToken<HashMap<String, Tuple>>(){}.getType()
                    );
                    table.update(new Condition(query.getJSONObject("Condition")), changes);
                    break;
            }
        } catch (CurrentDatabaseNotSetException | TableNotFoundException | JSONException | ColumnNotFoundException | DuplicateColumnsException | DifferentTypesException e) {
            response.setValid(true);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
