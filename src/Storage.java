import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.powder.Exception.CurrentDatabaseNotSetException;
import com.powder.Exception.DatabaseAlreadyExistsException;
import com.powder.Exception.DatabaseNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Storage {
    private String name;
    private List<Database> databases;
    private Database currentDatabase;
    private String pathToDatabases = System.getProperty("user.dir") + "Storages/Databases";
    private String pathToMyFolder = System.getProperty("user.dir") + "Storages";

    public Storage(String _name){
        name = _name;
        databases = new ArrayList<>();
    }

    public Storage(JSONObject jsonStorage) throws JSONException, FileNotFoundException {
        name = jsonStorage.getString("Name");
        JSONArray jsonDatabaseNames = jsonStorage.getJSONArray("DatabaseNames");
        List<String> databaseNames = new ArrayList<>();
        for(int i = 0; i < jsonDatabaseNames.length(); i++){
            databaseNames.add(jsonDatabaseNames.getString(i));
        }

        for(String databaseName : databaseNames){
            File file = new File(pathToDatabases + "/" + databaseName + ".json");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\Z");
            JSONObject jsonDatabase = new JSONObject(scanner.next());
            Database database = new Database(jsonDatabase);
            databases.add(database);
        }
        String currentDatabaseName = jsonStorage.getString("CurrentDatabase");
        if(!currentDatabaseName.equals("null")){
            for(Database database : databases){
                if(database.getName().equalsIgnoreCase(currentDatabaseName)){
                    currentDatabase = database;
                }
            }
        }
    }

    void addDatabase(Database _database) throws DatabaseAlreadyExistsException, JSONException {
        for(Database database : databases){
            if(database.getName().equals(_database.getName())){
                throw new DatabaseAlreadyExistsException();
            }
        }

        if(databases.isEmpty()){
            currentDatabase = _database;
        }
        databases.add(_database);
        saveToFile();
    }

    void deleteDatabase(String whichDatabase) throws DatabaseNotFoundException, JSONException {
        Database toDelete = null;
        for(Database database : databases){
            if(database.getName().equals(whichDatabase)){
                toDelete = database;
            }
        }

        if(toDelete != null){
            databases.remove(toDelete);
            if(databases.isEmpty()){
                currentDatabase = null;
            }
            saveToFile();
        }else{
            throw new DatabaseNotFoundException();
        }
    }

    public Response executeQuery(Query query) throws JSONException {
        return query.execute(this);
    }

    int howManyDatabases(){
        return databases.size();
    }

    public void setCurrentDatabase(String databaseName) throws DatabaseNotFoundException {
        for(Database database : databases){
            if(database.getName().equals(databaseName)){
                currentDatabase = database;
                return;
            }
        }
        throw new DatabaseNotFoundException();
    }

    public Database getCurrentDatabase() throws CurrentDatabaseNotSetException {
        if(currentDatabase != null){
            return currentDatabase;
        }else{
            throw new CurrentDatabaseNotSetException();
        }
    }

    public void saveToFile() throws JSONException {
        File file = new File(pathToMyFolder);
        if(!file.exists()){
            file.mkdir();
        }
        JSONObject jsonStorage = new JSONObject();
        jsonStorage.put("Name", name);
        jsonStorage.put( "CurrentDatabase", currentDatabase != null ? currentDatabase.getName() : "null");
        JSONArray jsonDatabases = new JSONArray();
        for(Database database : databases){
            jsonDatabases.put(database.getName());
        }
        jsonStorage.put("DatabaseNames", jsonDatabases);
        try(Writer writer = new FileWriter(pathToMyFolder + "/" + name + ".json")){
            Gson gson = new GsonBuilder().create();
            gson.toJson(jsonStorage, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
