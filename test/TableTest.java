import com.powder.Exception.*;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {
    Column firstName = new Column("imie", "string");
    Column surname = new Column("nazwisko", "string");
    Column profession = new Column("stanowisko", "string");
    Column age = new Column("wiek", "number");
    List<Column> columns = new ArrayList<>();
    {
        columns.add(firstName);
        columns.add(surname);
        columns.add(profession);
        columns.add(age);
    }
    Map<String, Tuple> data = new HashMap<>();
    {
        data.put("imie", new Tuple<>("Piotr"));
        data.put("Nazwisko", new Tuple<>("Proszowski"));
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
    void checkIfTwoTheSameTablesAreEqual() throws DuplicateColumnsException, JSONException, IOException, ColumnNotFoundException, DifferentTypesException, UnknownTypeException {
        Table table = new Table("test", columns);
        table.insert(record);
        table.insert(anotherRecord);
        assertEquals(table, table);
    }

    @Test
    void insertionOfColumnWithTheSameNamesToOneTableCauseException() throws DuplicateColumnsException {
        Column _column = new Column("imie", "string");
        new Table("test", Arrays.asList(_column, _column));
        fail("Exception should be thrown...");
}

    @Test
    void selectAllReturnsExpectedTable() throws UnknownTypeException, IOException, DuplicateColumnsException, ColumnNotFoundException, DifferentTypesException, JSONException {
        Table table = new Table("testTable", columns);
        table.insert(record);
        table.insert(anotherRecord);
        assertEquals(table, table.selectAll(Collections.emptyList()));
}

    @Test
    void selectFewColumnsReturnsExpectedTable() throws UnknownTypeException, IOException, ColumnNotFoundException, DifferentTypesException, JSONException, DuplicateColumnsException {
        Table table = new Table("testTable", columns);
        table.insert(record);
        table.insert(anotherRecord);
        Table selectedTable = table.select(Arrays.asList("Imie", "Nazwisko"), Collections.emptyList());

        Table anotherTable = new Table("testTable", Arrays.asList(firstName, surname));
        anotherTable.insert(new Record(record, Arrays.asList("Imie", "Nazwisko")));
        anotherTable.insert(new Record(anotherRecord, Arrays.asList("Imie", "Nazwisko")));

        assertEquals(anotherTable, selectedTable);
}

    @Test
    void testingVisualisation() throws DuplicateColumnsException, DifferentTypesException, ColumnNotFoundException, UnknownTypeException, JSONException, IOException {
        Table table = new Table("test", columns);
        table.insert(record);
        table.insert(anotherRecord);
        System.out.println(table.show());
    }

    @Test
    void ifRecordHasEmptyValueForSomeColumnsTheseColumnsShouldBeNull() throws DuplicateColumnsException, DifferentTypesException, ColumnNotFoundException {
        Table table = new Table("testTable", columns);
        Map<String, Tuple> nameAndSurname = new HashMap<>();
        nameAndSurname.put("Imie", new Tuple("Piotr"));
        nameAndSurname.put("Nazwisko", new Tuple("Proszowski"));
        table.insert(new Record(nameAndSurname));

        Table anotherTable = new Table("testTable", columns);
        Map<String, Tuple> dataWithNulls = new HashMap<>();
        dataWithNulls.put("Imie", new Tuple("Piotr"));
        dataWithNulls.put("Nazwisko", new Tuple("Proszowski"));
        dataWithNulls.put("stanowisko", new Tuple("null"));
        dataWithNulls.put("wiek", new Tuple("null"));
        anotherTable.insert(new Record(dataWithNulls));

        assertEquals(table, anotherTable);
    }

    @Test
    void deleteWithConditions() throws DifferentTypesException, ColumnNotFoundException, DuplicateColumnsException, IOException, JSONException {
        Table table = new Table("test", columns);
        table.insert(record);

        Table anotherTable = new Table("test", columns);
        anotherTable.insert(record);
        anotherTable.insert(anotherRecord);
        Condition condition = new Condition("imie", new Tuple("AnDrzej"), null, "=");
        assertEquals(1,anotherTable.delete(Collections.singletonList(condition)));
        assertEquals(table, anotherTable);
    }

    @Test
    void updateWithConditions() throws DuplicateColumnsException, DifferentTypesException, ColumnNotFoundException, IOException, JSONException {
        Table table = new Table("test", columns);
        table.insert(record);
        table.insert(anotherRecord);

        Map<String, Tuple> changes = new HashMap<>();
        changes.put("Nazwisko", new Tuple("Kornicki"));

        List<Condition> conditions = new ArrayList<>();
        conditions.add(new Condition("Nazwisko", new Tuple("ProszowsKi"), null, "="));

        assertEquals(1, table.update(conditions, changes));
        //table.update(conditions,changes) affects records which it change, which is good.
        assertTrue("kornicki".equalsIgnoreCase(record.getValueFromColumn("nazwisko").getValue().toString()));


    }

    @Test
    void selectWithWhere() throws DuplicateColumnsException, DifferentTypesException, ColumnNotFoundException {
        Table table = new Table("testTable", columns);
        table.insert(record);
        table.insert(anotherRecord);

        Condition condition = new Condition("Nazwisko", new Tuple("Proszowski"), "null", "=");
        Table selectedTable = table.selectAll(Collections.emptyList()).where(Arrays.asList(condition));

        Table anotherTable = new Table("testTable", Arrays.asList(firstName, surname));
        anotherTable.insert(new Record(record, Arrays.asList("Imie", "Nazwisko")));

        assertEquals(table, selectedTable);
        
    }


}

