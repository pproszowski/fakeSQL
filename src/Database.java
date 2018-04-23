import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Table> tables;
    private String name;

    public Database(String _name){
        name = _name;
        tables = new ArrayList<>();
    }

    public void addTable(Table _table) throws TableAlreadyExistsException {
        for(Table table : tables){
            if(table.getName().equals(_table.getName())){
                throw new TableAlreadyExistsException();
            }
        }
        tables.add(_table);
    }

    public void removeTable(String tableName) throws TableNotFoundException {
        Table tableToDelete = null;
        for(Table table : tables){
            if(table.getName().equals(tableName)){
                tableToDelete = table;
            }
        }

        if(tableToDelete != null){
            tables.remove(tableToDelete);
        }else{
            throw new TableNotFoundException();
        }
    }

    public Table getTable(String tableName) throws TableNotFoundException {
        for(Table table : tables){
            if(table.getName().equals(tableName)){
                return table;
            }
        }

        throw new TableNotFoundException();
    }

    public int howManyTables(){
        return tables.size();
    }

    public String getName() {
        return name;
    }
}
