import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.powder.Exception.TableAlreadyExistsException;
import com.powder.Exception.TableNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Database {
    private List<Table> tables;
    private String name;
    private static final String pathToMyFolder = System.getProperty("user.dir") + "Storages/Databases";
    private static final String pathToTables= System.getProperty("user.dir") + "Storages/Databases/Tables";

    public Database(String _name){
        name = _name;
        tables = new ArrayList<>();
    }

    public Database(JSONObject database) throws JSONException, FileNotFoundException {
        name = database.getString("Name");
        tables = new ArrayList<>();
        JSONArray jsonTables = database.getJSONArray("Tables");
        List<String> tableNames = new ArrayList<>();
        for(int i = 0; i < jsonTables.length(); i++){
            tableNames.add(jsonTables.getString(i));
        }
        for(String tableName : tableNames){
            File file = new File(pathToTables + "/" + tableName + ".json");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\Z");
            JSONObject jsonTable = new JSONObject(scanner.next());
            Table table = new Table(jsonTable);
            tables.add(table);
        }
    }

    public void addTable(Table _table) throws TableAlreadyExistsException, JSONException {
        for(Table table : tables){
            if(table.getName().equals(_table.getName())){
                throw new TableAlreadyExistsException(table.getName() ,this.getName());
            }
        }
        tables.add(_table);
        saveToFile();
    }

    public void removeTable(String tableName) throws TableNotFoundException, JSONException {
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

    private void saveToFile() throws JSONException {
        File file = new File(pathToMyFolder);
        if(!file.exists()){
            file.mkdir();
        }
        JSONObject jsonDatabase = new JSONObject();
        jsonDatabase.put("Name", name);
        JSONArray jsonTableNames = new JSONArray();
        for(Table table : tables){
            jsonTableNames.put(table.getName());
        }
        jsonDatabase.put("Tables", jsonTableNames);

        try(Writer writer = new FileWriter(pathToMyFolder + "/" + name + ".json")){
            Gson gson = new GsonBuilder().create();
            gson.toJson(jsonDatabase, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
