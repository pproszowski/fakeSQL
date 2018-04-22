import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table {
    private String name;
    private List<Column> columns;
    private List<Record> records;

    public Table(String name){
        this.name = name;
        this.columns = new ArrayList<>();
        this.records = new ArrayList<>();
    }

    //METHODS

    public Table select(List<String> whichColumns) {

        return new Table("default");
    }

    public boolean insert(Record record){
        return false;
    }

    public boolean delete(Condition condition){
        return false;
    }

    public boolean update(Condition condition, Map<String, Tuple> newValues){
        return false;
    }

    public boolean insert(List<Record> records){
        return false;
    }

    //GETTERS
    public String getName() {
        return name;
    }
}


