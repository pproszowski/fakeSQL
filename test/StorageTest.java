import com.powder.Exception.DatabaseAlreadyExistsException;
import com.powder.Exception.DatabaseNotFoundException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest{

    Storage storage = new Storage("test");
    Database database = new Database("testDatabase1");
    Database anotherDatabase = new Database("testDatabase2");
    Database anotherDatabase2 = new Database("testDatabase3");

    StorageTest() throws IOException, JSONException {
    }

    @Test
    void addingDatabaseToStorageShouldIncreaseAmountOfDatabases() throws JSONException, IOException {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void when_table_duplicates_another_exception_should_be_thrown() throws JSONException, IOException {
        try {
            storage.addDatabase(database);
            storage.addDatabase(database);
            fail("Exception should be thrown but wasn't");
        } catch (DatabaseAlreadyExistsException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void delete_non_existing_table_throws_exception() throws JSONException, IOException {
        try {
            storage.deleteDatabase("razdwatrzy");
            fail("Exception should be thrown but wasn't");
        } catch (DatabaseNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

