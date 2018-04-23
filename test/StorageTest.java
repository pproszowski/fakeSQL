import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest{

    Storage storage = new Storage("test");
    Database database = new Database("testDatabase1");
    Database anotherDatabase = new Database("testDatabase2");
    Database anotherDatabase2 = new Database("testDatabase3");

    @Test
    void addingDatabaseToStorageShouldIncreaseAmountOfDatabases(){
            assertEquals(0, database.howManyTables());
        try {
            storage.addDatabase(database);
            assertEquals(1, storage.howManyDatabases());
            storage.addDatabase(anotherDatabase);
            assertEquals(2, storage.howManyDatabases());
            storage.addDatabase(anotherDatabase2);
            assertEquals(3, storage.howManyDatabases());
        } catch (DatabaseAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    @Test
    void when_table_duplicates_another_exception_should_be_thrown(){
        try {
            storage.addDatabase(database);
            storage.addDatabase(database);
            fail("Exception should be thrown but wasn't");
        } catch (DatabaseAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    @Test
    void delete_non_existing_table_throws_exception(){
        try {
            storage.deleteDatabase("razdwatrzy");
            fail("Exception should be thrown but wasn't");
        } catch (DatabaseNotFoundException e) {
            e.printStackTrace();
        }
    }

}

