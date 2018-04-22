import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    Database database = new Database("test");
    Table table = new Table("testTable1");
    Table anotherTable = new Table("testTable2");
    Table anotherTable2 = new Table("testTable3");

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