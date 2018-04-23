import java.util.ArrayList;
import java.util.List;

public class Storage {
    private String name;
    private List<Database> databases;
    private Database currentDatabase;

    public Storage(String _name){
        name = _name;
        databases = new ArrayList<>();
    }


    void addDatabase(Database _database) throws DatabaseAlreadyExistsException {
        for(Database database : databases){
            if(database.getName().equals(_database.getName())){
                throw new DatabaseAlreadyExistsException();
            }
        }
        databases.add(_database);
    }

    void deleteDatabase(String whichDatabase) throws DatabaseNotFoundException{
        Database toDelete = null;
        for(Database database : databases){
            if(database.getName().equals(whichDatabase)){
                toDelete = database;
            }
        }

        if(toDelete != null){
            databases.remove(toDelete);
        }else{
            throw new DatabaseNotFoundException();
        }
    }

    public Response executeQuery(Query query){
        return new Response();
    }

    int howManyDatabases(){
        return databases.size();
    }

}
