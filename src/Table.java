import com.powder.Exception.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    public Table(JSONObject jsonTable) {
        try {
            name = jsonTable.getString("Name");
            columns = new ArrayList<>();
            records = new ArrayList<>();
            JSONArray _columns = jsonTable.getJSONArray("Columns");
            JSONArray _records = jsonTable.getJSONArray("Records");

            for (int i = 0; i < _columns.length(); i++) {
                columns.add(new Column(_columns.getJSONObject(i)));
            }

            for (int i = 0; i < _records.length(); i++) {
                records.add(new Record(_records.getJSONObject(i)));
            }
            jsonTable.put("Columns", _columns);
            jsonTable.put("Records", _records);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                if(columnName.equalsIgnoreCase(column.getName())){
                    _columns.add(column);
                }
            }
        }
        return new Table(name, _columns, newRecords);
    }


    public void insert(Record record) throws DifferentTypesException, ColumnNotFoundException {

        Record copy = new Record(record);
        for(Map.Entry<String, Tuple> entry : record.getValues().entrySet()){
           for(Column column : columns){
               if(column.getName().equalsIgnoreCase(entry.getKey())){
                   Tuple tuple = entry.getValue();
                   if(!column.getType().getName().equalsIgnoreCase(tuple.getTypeName())){
                       if(! (tuple.getValue() instanceof String && tuple.getValue().equals("null"))){
                           throw new DifferentTypesException();
                       }
                   }
                   if(tuple.toString().length() > column.getWidth()){
                       column.expandWidth(tuple.toString().length() + 2);
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

    public int delete(List<Condition> conditions) throws JSONException, IOException {
        List<Record> recordsToRemove = new ArrayList<>();
        int sizeBefore = records.size();
        if(conditions.isEmpty()){
            records.clear();
        }else{
           for(Record record : records){
                if(record.meetConditions(conditions)){
                    recordsToRemove.add(record);
                }
           }

           records.removeAll(recordsToRemove);
        }

        return sizeBefore - records.size();
    }

    public Table where(List<Condition> conditions) throws DuplicateColumnsException, DifferentTypesException, ColumnNotFoundException {
        Table table = new Table(this.name, this.columns);
        List<Record> recordsToInsert = new ArrayList<>();
        for(Record record : records){
            if(record.meetConditions(conditions)){
                recordsToInsert.add(record);
            }
        }
        table.insert(recordsToInsert);
        return table;
    }

    public int update(Condition condition, Map<String, Tuple> newValues) throws JSONException, IOException {
        int howMany = 0;
        if(condition.isEmpty()){
            for(Map.Entry entry : newValues.entrySet()){
                for(Record record : records){
                    if(record.update((String)entry.getKey(), (Tuple)entry.getValue())){
                        howMany++;
                    }
                }
            }
        }
        saveToFile();
        return howMany;
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
            sB.append(column.toString()).append("\n");
        }
        sB.append("\nRecords:\n");
        for(Record record : records){
            sB.append(record.toString()).append("\n");
        }

        return sB.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Table){
            Table table = (Table) obj;
            return name.equals(table.name)
                    && this.records.containsAll(table.records)
                    && this.columns.containsAll(table.columns);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        for(Column column : columns){
            hashcode += column.hashCode();
        }
        for(Record record : records){
            hashcode += record.hashCode();
        }
        hashcode += name.hashCode();
        return hashcode;
    }

    public String show() {
        StringBuilder top = null;
        StringBuilder bottom = null;
        StringBuilder mid = null;
        try {
            top = new StringBuilder();
            for (Column column : columns) {
                top.append("+");
                for (int i = 0; i < column.getWidth() + 2; i++) {
                    top.append("-");
                }
            }
            top.append("+\n");
            bottom = new StringBuilder(top);
            for (Column column : columns) {
                top.append("|");
                top.append(" ");
                top.append(column.getName());
                int howMuchSpaceLeft = column.getWidth() - column.getName().length() + 1;
                for (int i = 0; i < howMuchSpaceLeft; i++) {
                    top.append(" ");
                }
            }
            top.append("|\n");

            mid = new StringBuilder();
            for (Record record : records) {
                for (Column column : columns) {
                    mid.append("|");
                    mid.append(" ");
                    mid.append(record.getValueFromColumn(column.getName()));
                    int howMuchSpaceLeft = column.getWidth() - record.getValueFromColumn(column.getName()).toString().length() + 1;
                    for (int i = 0; i < howMuchSpaceLeft; i++) {
                        mid.append(" ");
                    }
                }
                mid.append("|\n");
            }
        } catch (ColumnNotFoundException e) {
            e.printStackTrace();
        }

        return top.toString() + bottom.toString() + mid.toString() + bottom.toString();
    }

    public void saveToFile() throws JSONException, IOException {
        JSONObject jsonTable= new JSONObject();
        jsonTable.put("Name", name);
        JSONArray jsonColumns = new JSONArray();
        for(Column column : columns){
            JSONObject jsonColumn = new JSONObject();
            jsonColumn.put("Name", column.getName());
            JSONObject jsonType = new JSONObject();
            jsonType.put("Name", column.getType().getName());
            jsonType.put("Limit", column.getType().getLimit());
            jsonColumn.put("Type", jsonType);
            jsonColumns.put(jsonColumn);
        }

        JSONArray jsonRecords = new JSONArray();
        for(Record record : records){
            jsonRecords.put(record.getValues());
        }

        jsonTable.put("Columns", jsonColumns);
        jsonTable.put("Records", jsonRecords);

        ResourceManager resourceManager = new ResourceManager("res/Storages/Databases/Tables/", name);
        resourceManager.saveJSONToResource(jsonTable);
    }

}


