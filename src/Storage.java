import java.util.List;

public class Storage {
    private String name;
    private List<Database> databases;
    private Database currentDatabase;

    public Storage(String _name){
        name = _name;
    }


    // function are protected only because of first tests, in further version they'll change to private

    protected boolean addDatabase(Database database) throws DatabaseAlreadyExistsException {
        return true;
    }

    protected boolean deleteDatabase(String whichDatabase) throws DatabaseNotFoundException{
        return true;
    }

    public Response executeQuery(Query query){
        return new Response();
    }

    protected int howManyDatabases(){
        return databases.size();
    }

}
