public class DatabaseQuery extends Query{
    private String tableName;
    private String type;
    private Table table;
    private Database database;

    public DatabaseQuery(String _tableName, String _type){
        tableName = _tableName;
        type = _type;
    }

    public DatabaseQuery(Table _table, String _type){
        this(_table.getName(), _type);
        table = _table;
    }

    @Override
    public Response execute(Storage storage) {
        Response response = new Response();
        try {
            database = storage.getCurrentDatabase();
        } catch (CurrentDatabaseNotSetException e) {
            e.printStackTrace();
        }
        switch(type.toLowerCase()){
            case "addtable":
                try {
                    database.addTable(table);
                } catch (TableAlreadyExistsException e) {
                }
                break;
            case "deletetable":
                try {
                    database.removeTable(tableName);
                } catch (TableNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }

        return response;
    }
}
