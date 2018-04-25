import com.powder.Exception.*;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args){
        Storage storage = new Storage("test");
        String command = "CREATE DATABASE test";
        try {
            JSONObject jsonQuery = Parser.parseSQLtoJSON(command);
            Query query = QueryFactory.getInstance().getQuery(jsonQuery);
            storage.executeQuery(query);
            command = "CREATE TABLE pracownicy(\n" +
                    "    imie string(30),\n" +
                    "    nazwisko string(50),\n" +
                    "    posada string(50),\n" +
                    "    wiek int" +
                    "); ";
            jsonQuery = Parser.parseSQLtoJSON(command);
            query = QueryFactory.getInstance().getQuery(jsonQuery);
            Response response = storage.executeQuery(query);
            System.out.println(response.getMessage());

            command = "INSERT INTO PRACOWNICY (imie, nazwisko, posada, wiek) VALUES (\"Piotr\", \"Proszowski\", \"Programista Java\", 21)";
            jsonQuery = Parser.parseSQLtoJSON(command);
            query = QueryFactory.getInstance().getQuery(jsonQuery);
            response = storage.executeQuery(query);
            System.out.println(response.getMessage());

            command = "INSERT INTO PRACOWNICY (imie, nazwisko, posada, wiek) VALUES (\"Andrzej\", \"Proszowski\", \"Programista Java\", 41)";
            jsonQuery = Parser.parseSQLtoJSON(command);
            query = QueryFactory.getInstance().getQuery(jsonQuery);
            response = storage.executeQuery(query);
            System.out.println(response.getMessage());

            command = "SELECT imie, nazwisko, posada, wiek FROM pracownicy";
            jsonQuery = Parser.parseSQLtoJSON(command);
            query = QueryFactory.getInstance().getQuery(jsonQuery);
            response = storage.executeQuery(query);
            System.out.println(response.getMessage());
        } catch (InvalidKeyWordException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IncorrectSyntaxException e) {
            e.printStackTrace();
        } catch (BadQueryTypeException e) {
            e.printStackTrace();
        } catch (UnknownTypeException e) {
            e.printStackTrace();
        }

    }
}
