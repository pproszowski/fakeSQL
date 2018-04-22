import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Table> tables;
    private String name;

    public Database(String _name){
        name = _name;
        tables = new ArrayList<>();
    }

    public void addTable(Table _table) throws TableAlreadyExists{
        for(Table table : tables){
            if(table.getName().equals(_table.getName())){
                throw new TableAlreadyExists();
            }
        }
    }
    public void removeTable(String tableName) throws TableNotFound{
        Table tableToDelete = null;
        for(Table table : tables){
            if(table.getName().equals(tableName)){
                tableToDelete = table;
            }
        }

        if(tableToDelete != null){
            tables.remove(tableToDelete);
        }else{
            throw new TableNotFound();
        }
    }
    public Table getTable(String tableName) throws TableNotFound{
        for(Table table : tables){
            if(table.getName().equals(tableName)){
                return table;
            }
        }

        throw new TableNotFound();
    }
    public int howManyTables(){
        return tables.size();
    }
}
