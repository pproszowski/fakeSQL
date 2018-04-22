import java.util.List;

public class Storage {
    private String name;
    private List<Database> databases;
    private Database currentDatabase;

    public Storage(String _name){
        name = _name;
    }

    private boolean addDatabase(Database database){
        return true;
    }

    private boolean deleteDatabase(String whichDatabase){
        return true;
    }

    public Response executeQuery(Query query){
        return new Response();
    }

    public int howManyDatabases(){
        return databases.size();
    }

}
