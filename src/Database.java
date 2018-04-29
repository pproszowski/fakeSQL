import com.powder.Exception.TableAlreadyExistsException;
import com.powder.Exception.TableNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Table> tables;
    private String name;

    public Database(String _name){
        name = _name;
        tables = new ArrayList<>();
    }

    public Database(JSONObject database) throws JSONException, IOException {
        name = database.getString("Name");
        tables = new ArrayList<>();
        JSONArray jsonTables = database.getJSONArray("Tables");
        List<String> tableNames = new ArrayList<>();
        for(int i = 0; i < jsonTables.length(); i++){
            tableNames.add(jsonTables.getString(i));
        }
        for(String tableName : tableNames){
            ResourceManager resourceManager = new ResourceManager("res/Storages/Databases/Tables/", tableName);
            JSONObject jsonTable = new JSONObject(resourceManager.readFromResource());
            Table table = new Table(jsonTable);
            tables.add(table);
        }
    }

    public void addTable(Table _table) throws TableAlreadyExistsException, JSONException, IOException {
        for(Table table : tables){
            if(table.getName().equals(_table.getName())){
                throw new TableAlreadyExistsException(table.getName() ,this.getName());
            }
        }
        tables.add(_table);
        saveToFile();
    }

    public void removeTable(String tableName) throws TableNotFoundException, JSONException, IOException {
        Table tableToDelete = null;
        for(Table table : tables){
            if(table.getName().equals(tableName)){
                tableToDelete = table;
            }
        }

        if(tableToDelete != null){
            tables.remove(tableToDelete);
            saveToFile();
        }else{
            throw new TableNotFoundException(this.getName(), tableName);
        }
    }

    public Table getTable(String tableName) throws TableNotFoundException {
        for(Table table : tables){
            if(table.getName().equalsIgnoreCase(tableName)){
                return table;
            }
        }

        throw new TableNotFoundException(this.getName(), tableName);
    }

    public int howManyTables(){
        return tables.size();
    }

    public String getName() {
        return name;
    }

    public void saveToFile() throws JSONException, IOException {
        JSONObject jsonDatabase = new JSONObject();
        jsonDatabase.put("Name", name);
        JSONArray jsonTableNames = new JSONArray();
        for(Table table : tables){
            jsonTableNames.put(table.getName());
        }
        jsonDatabase.put("Tables", jsonTableNames);
        ResourceManager resourceManager = new ResourceManager("res/Storages/Databases/", name);
        resourceManager.saveJSONToResource(jsonDatabase);


    }
}
