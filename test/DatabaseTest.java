import com.powder.Exception.DuplicateColumnsException;
import com.powder.Exception.TableAlreadyExistsException;
import com.powder.Exception.TableNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    Database database = new Database("test");
    List<Column> columns = Collections.singletonList(new Column("wiek", "number"));
    Table table;
    Table anotherTable;
    Table anotherTable2;
    {
        try {
            table = new Table("testTable1", columns);
            anotherTable = new Table("testTable2", columns);
            anotherTable2 = new Table("testTable3", columns);
        } catch (DuplicateColumnsException e) {
            e.printStackTrace();
        }
    }


    @Test
    void addingTableToDatabaseShouldIncreaseAmountOfTables(){
        try{
            assertEquals(0, database.howManyTables());
            database.addTable(table);
            assertEquals(1, database.howManyTables());
            database.addTable(anotherTable);
            assertEquals(2, database.howManyTables());
            database.addTable(anotherTable2);
            assertEquals(3, database.howManyTables());
        }catch (TableAlreadyExistsException e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    void when_table_duplicates_another_exception_should_be_thrown(){
        try {
            database.addTable(table);
            database.addTable(table);
            fail("Exception should be thrown but wasn't");
        } catch (TableAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void delete_non_existing_table_throws_exception(){
        try {
            database.removeTable("razdwatrzy");
            fail("Exception should be thrown but wasn't");
        } catch (TableNotFoundException tableNotFound) {
            tableNotFound.printStackTrace();
        }
    }

}