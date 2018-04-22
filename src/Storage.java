import java.util.List;

public class Storage {
    private String name;
    private List<Database> databases;
    private Database currentDatabase;

    public Storage(String _name){
        name = _name;
    }

    private boolean addTable(Table table){
        return true;
    }

    private boolean deleteTable(String whichTable){
        return true;
    }

    public Response executeQuery(Query query){
        return new Response();
    }

    public int howManyDatabases(){
        return databases.size();
    }

}
