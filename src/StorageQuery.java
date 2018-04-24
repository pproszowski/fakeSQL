public class StorageQuery extends Query {
    private String databaseName;
    private String type;
    private Database database;

    protected StorageQuery(String _databaseName, String _type){
        this(null, _databaseName, _type);
    }

    protected StorageQuery(Database _database, String _databaseName, String _type){
        databaseName = _databaseName;
        type = _type;
        database = _database;
    }

    @Override
    public Response execute(Storage storage) {
        Response response = new Response();
        switch(type.toLowerCase()){
            case "adddatabase":
                try {
                    storage.addDatabase(database);
                } catch (DatabaseAlreadyExistsException e) {
                    //change Response
                }
                break;
            case "setcurrentdatabase":
                try {
                    storage.setCurrentDatabase(databaseName);
                } catch (DatabaseNotFoundException e) {
                    //change Response
                }
                break;
            case "removedatabase":
                try {
                    storage.deleteDatabase(databaseName);
                } catch (DatabaseNotFoundException e) {
                    //change Response
                }
                break;
        }

        return response;
    }
}
