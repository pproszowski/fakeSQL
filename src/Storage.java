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

    public Storage(String _name) throws IOException, JSONException {
        name = _name;
        databases = new ArrayList<>();
        addNameOfStorageToList();
    }

    public Storage(JSONObject jsonStorage) throws JSONException, IOException {
        databases = new ArrayList<>();
        System.out.println(jsonStorage.toString());
        name = jsonStorage.getString("Name");
        JSONArray jsonDatabaseNames = jsonStorage.getJSONArray("DatabaseNames");
        List<String> databaseNames = new ArrayList<>();
        for(int i = 0; i < jsonDatabaseNames.length(); i++){
            databaseNames.add(jsonDatabaseNames.getString(i));
        }

        for(String databaseName : databaseNames){
            ResourceManager resourceManager = new ResourceManager("res/Storages/Databases/",databaseName);
            JSONObject jsonDatabase = new JSONObject(resourceManager.readFromResource());
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
        addNameOfStorageToList();
    }

    void addDatabase(Database _database) throws DatabaseAlreadyExistsException, JSONException, IOException {
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

    void deleteDatabase(String whichDatabase) throws DatabaseNotFoundException, JSONException, IOException {
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

    public Response executeQuery(Query query) throws JSONException, IOException {
        return query.execute(this);
    }

    int howManyDatabases(){
        return databases.size();
    }

    public void setCurrentDatabase(String databaseName) throws DatabaseNotFoundException, IOException, JSONException {
        for(Database database : databases){
            if(database.getName().equals(databaseName)){
                currentDatabase = database;
                saveToFile();
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

    public void saveToFile() throws JSONException, IOException {
        JSONObject jsonStorage = new JSONObject();
        jsonStorage.put("Name", name);
        jsonStorage.put( "CurrentDatabase", currentDatabase != null ? currentDatabase.getName() : "null");
        JSONArray jsonDatabases = new JSONArray();
        for(Database database : databases){
            jsonDatabases.put(database.getName());
        }
        jsonStorage.put("DatabaseNames", jsonDatabases);

        ResourceManager resourceManager = new ResourceManager("res/Storages/", name);
        resourceManager.saveJSONToResource(jsonStorage);
    }

    private void addNameOfStorageToList() throws IOException {
        File file = new File("res/storageNames.txt");
        Scanner scanner = new Scanner(file);
        StringBuilder toSave = new StringBuilder();
        String tmp;
        while(scanner.hasNextLine()){
            tmp = scanner.nextLine();
            if(name.equalsIgnoreCase(tmp)){
                return;
            }
            toSave.append(tmp).append("\n");
        }
        toSave.append(name);
        Writer fileWriter = new FileWriter(file);
        fileWriter.write(toSave.toString());
        fileWriter.close();
    }
}
