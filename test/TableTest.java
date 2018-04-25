import com.powder.Exception.ColumnNotFoundException;
import com.powder.Exception.DifferentTypesException;
import com.powder.Exception.DuplicateColumnsException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {
    Column column = new Column("imie", "string");
    Column column1 = new Column("nazwisko", "string");
    Column column2 = new Column("stanowisko", "string");
    Column column3 = new Column("wiek", "number");
    List<Column> columns = new ArrayList<>();
    {
        columns.add(column);
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);
    }
    Map<String, Tuple> data = new HashMap<>();
    {
        data.put("imie", new Tuple<>("Piotr"));
        data.put("Nazwisko", new Tuple<>("Piotr"));
        data.put("stanowisko", new Tuple<>("Programista Java"));
        data.put("wiek", new Tuple<>(21));
    }
    Record record = new Record(data);
    {
        data.clear();
        data.put("imie", new Tuple<>("Andrzej"));
        data.put("Nazwisko", new Tuple<>("Ziemniak"));
        data.put("stanowisko", new Tuple<>("Front-end"));
        data.put("wiek", new Tuple<>(24));
    }
    Record anotherRecord = new Record(data);

    @Test
    void duplicateColumnNamesIsImpossible(){
        Column _column = new Column("imie", "string");
        try {
            Table test = new Table("test", Arrays.asList(_column, _column));
            fail("Exception should be thrown...");
        } catch (DuplicateColumnsException e) {
            e.getMessage();
        }
    }

    @Test
    void selectAllReturnsExpectedTable() throws ColumnNotFoundException {
        try {
            Table table = new Table("testTable", columns);
            table.insert(record);
            table.insert(anotherRecord);
            assertEquals(table, table.selectAll());
        } catch (DuplicateColumnsException | ColumnNotFoundException | DifferentTypesException e) {
            e.printStackTrace();
        }
    }

    @Test
    void selectFewColumnsReturnsExpectedTable(){
        try {
            Table table = new Table("testTable", columns);
            table.insert(record);
            table.insert(anotherRecord);
            Table selectedTable = table.select(Arrays.asList("Imie", "Nazwisko"));
            Column name = new Column("Imie", "String");
            Column surName = new Column("Nazwisko", "String");
            Table anotherTable = new Table("testTable", Arrays.asList(name, surName));
            Record newRecord = new Record(record, Arrays.asList("Imie", "Nazwisko"));
            Record anotherNewRecord = new Record(anotherRecord, Arrays.asList("Imie", "Nazwisko"));
            anotherTable.insert(Arrays.asList(newRecord, anotherNewRecord));

            assertEquals(selectedTable.toString(), anotherTable.toString());
        } catch (DuplicateColumnsException | ColumnNotFoundException | DifferentTypesException e) {
            e.printStackTrace();
        }
    }
}