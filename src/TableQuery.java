import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.powder.Exception.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    public Response execute(Storage storage) throws JSONException{
        Response response = new Response();
        try {
            String type = query.getString("Operation");
            Database database = storage.getCurrentDatabase();
            Table table = database.getTable(query.getString("Name"));
            int howMany;
            switch(type.toLowerCase()){
                case "select":
                        JSONArray columns = query.getJSONArray("ColumnNames");
                        List<String> columnNames = new ArrayList<>();
                        for(int i = 0; i < columns.length(); i++){
                            columnNames.add(columns.getString(i));
                        }
                        response.setMessage(table.select(columnNames).show());
                    break;
                case "insert":
                   table.insert(new Record(query.getJSONObject("Record")));
                    break;
                case "delete":
                    List<Condition> conditions = new ArrayList<>();
                    JSONArray jsonArray = query.getJSONArray("Conditions");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String columnName = jsonObject.getString("ColumnName");
                        Tuple tuple = new Tuple(jsonObject.get("Tuple"));
                        String connector = jsonObject.getString("Connector");
                        Condition condition = new Condition(columnName, tuple, connector);
                        conditions.add(condition);
                    }

                    howMany = table.delete(conditions);
                    response.setMessage("(" + howMany + ") rows has been removed");
                    break;
                case "update":
                    Map<String, Tuple> changes = new Gson().fromJson(
                            query.getJSONObject("Changes").toString(), new TypeToken<HashMap<String, Tuple>>(){}.getType()
                    );
                    howMany = table.update(new Condition(query.getJSONObject("Condition")), changes);
                    response.setMessage("(" + howMany + ") records has been affected");
                    break;
            }
            response.setValid(true);
        } catch (CurrentDatabaseNotSetException | DuplicateColumnsException | ColumnNotFoundException | DifferentTypesException | TableNotFoundException e) {
            response.setValid(false);
            response.setMessage(e.getMessage());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
