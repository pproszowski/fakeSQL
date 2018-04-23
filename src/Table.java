import java.util.*;

public class Table {
    private String name;
    private List<Column> columns;
    private List<Record> records;

    public Table(Table _table) throws DuplicateColumnsException {
        this(_table.name, _table.columns, _table.records);
    }

    public Table(String name, List<Column> columns) throws DuplicateColumnsException {
        this(name, columns, new ArrayList<>());
    }

    private Table(String name, List<Column> columns, List<Record> _records) throws DuplicateColumnsException {
        this.name = name;
        List<String> columnNames = new ArrayList<>();
        for(Column column : columns){
            columnNames.add(column.getName());
        }
        Set<String> setOfColumns = new HashSet<>(columnNames);
        if(setOfColumns.size() < columnNames.size()){
            throw new DuplicateColumnsException();
        }
        this.columns = new ArrayList<>(columns);
        this.records = new ArrayList<>(_records);
    }

    public Table selectAll() throws DuplicateColumnsException {
        List<String> columnNames = new ArrayList<>();
        for(Column column : columns){
            columnNames.add(column.getName());
        }
        return select(columnNames);
    }

    public Table select(List<String> whichColumns) throws DuplicateColumnsException {

        List<Record> newRecords = new ArrayList<>();
        for(Record record : records){
            newRecords.add(record.getRecordWithOnlySpecifiedColumns(whichColumns));
        }
        List<Column> _columns = new ArrayList<>();
        for(String columnName : whichColumns){
            for(Column column : columns){
                if(columnName.equals(column.getName())){
                    _columns.add(column);
                }
            }
        }
        return new Table(name, _columns, newRecords);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Table){
            Table table = (Table) obj;
            return name.equals(table.name);
        }else{
            return false;
        }
    }

    public void insert(Record record) throws DifferentTypesException, ColumnNotFoundException {

        Record copy = new Record(record);
        for(Map.Entry<String, Tuple> entry : record.getValues().entrySet()){
           for(Column column : columns){
               if(column.getName().equals(entry.getKey())){
                   Tuple tuple = entry.getValue();
                   if(!column.getType().equals(tuple.getType())){
                       throw new DifferentTypesException();
                   }
                   copy.getValues().remove(entry.getKey());
               }
           }
        }

        if(copy.getValues().size() == 0){
            for(Column column : columns){
                if(!record.getValues().containsKey(column.getName())){
                    record.getValues().put(column.getName(), new Tuple<>("null"));
                }
            }
            records.add(record);
        }else{
            throw new ColumnNotFoundException();
        }
    }

    public void insert(List<Record> _records) throws DifferentTypesException, ColumnNotFoundException {
        Table copy;
        try {
            copy = new Table(this);
            for(Record record : _records){
                copy.insert(record);
            }

            this.records = copy.records;
        } catch (DuplicateColumnsException e) {
            //impossible to happen
        }
    }

    public void delete(Condition condition){
    }

    public void update(Condition condition, Map<String, Tuple> newValues){
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sB = new StringBuilder();
        sB.append(name);
        sB.append("\nColumns:\n");
        for(Column column : columns){
            sB.append(column.getName()).append("  ");
        }
        for(Record record : records){
            sB.append(record.toString()).append(" ").append("\n");
        }

        return sB.toString();
    }
}


