import com.powder.Exception.IncorrectSyntaxException;
import com.powder.Exception.InvalidKeyWordException;
import com.powder.Exception.NotSpecifiedFromWhichTableException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parseSelectCommand(){
        String command = "SELECT dzialy,  AnanaaSym   , siema FROM pracownicy";
        String json = "{\"Target\":\"table\",\"Query\":{\"TableName\":\"pracownicy\",\"ColumnNames\":[[\"dzialy\",\"AnanaaSym\",\"siema\"]],\"Operation\":\"select\"}}";
        try {
            assertEquals(json, Parser.parseSQLtoJSON(command).toString());
        } catch (InvalidKeyWordException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NotSpecifiedFromWhichTableException e) {
            e.printStackTrace();
        } catch (IncorrectSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    void parseInsertCommand(){
        String command = "INsERt    inTO superTable (zbiki, dziecioly) ValuEs (\"szczecin\" , \"wlasnoscPanstwa\")";
        String command2 = "Insert into pracownicy (imie, nazwisko, wiek, posada) \n VALUES (\"Piotr\", \"Proszowski\", 21, \"programista Java\")";
        String json1 = "{\"Target\":\"table\",\"Query\":{\"Record\":{\"dziecioly\":{\"value\":\"wlasnoscPanstwa\"},\"zbiki\":{\"value\":\"szczecin\"}},\"Operation\":\"insert\",\"Name\":\"superTable\"}}";
        String json2 = "{\"Target\":\"table\",\"Query\":{\"Record\":{\"imie\":{\"value\":\"Piotr\"},\"nazwisko\":{\"value\":\"Proszowski\"},\"posada\":{\"value\":\"programista Java\"},\"wiek\":{\"value\":\"21\"}},\"Operation\":\"insert\",\"Name\":\"pracownicy\"}}";

        try {
            assertEquals(json1, Parser.parseSQLtoJSON(command).toString());
            assertEquals(json2, Parser.parseSQLtoJSON(command2).toString());
        } catch (InvalidKeyWordException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NotSpecifiedFromWhichTableException e) {
            e.printStackTrace();
        } catch (IncorrectSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    void parseValidCreateDatabaseCommand(){
        String command = "     cReATE dATABasE jasIMalgosia";
        String jsonObject = "{\"Target\":\"Storage\",\"Query\":{\"Database\":{\"Tables\":[],\"Name\":\"jasIMalgosia\"},\"Operation\":\"adddatabase\"}}";
        try {
            assertEquals(jsonObject, Parser.parseSQLtoJSON(command).toString());
        } catch (InvalidKeyWordException | JSONException | IncorrectSyntaxException | NotSpecifiedFromWhichTableException e) {
            e.printStackTrace();
        }
    }

    @Test
    void parseInvalidCreateDatabaseCommand(){
        String command = "     cReATE dATABasE ";
        try {
            System.out.println(Parser.parseSQLtoJSON(command));
            fail("Exception should be thrown");
        } catch (InvalidKeyWordException | JSONException | IncorrectSyntaxException | NotSpecifiedFromWhichTableException e) {
        }
    }

    @Test
    void parseValidCreateTableCommand(){
        String command = "     cReATE    tABLe ewEliNAKOnerskwiaA(" +
                "personID int," +
                "imie varchar(255));";
        try {
            System.out.println(Parser.parseSQLtoJSON(command).toString());
        } catch (InvalidKeyWordException | JSONException | IncorrectSyntaxException | NotSpecifiedFromWhichTableException e) {
            e.printStackTrace();
        }
    }

    @Test
    void parseInvalidCreateTableCommand(){
        String command = "     cReATE   TablE dafsa fsf sa";
        try {
            System.out.println(Parser.parseSQLtoJSON(command));
            fail("Exception should be thrown");
        } catch (InvalidKeyWordException | JSONException | IncorrectSyntaxException | NotSpecifiedFromWhichTableException e) {
        }
    }

    @Test
    void parseAnotherInvalidCreateDatabaseCommand(){
        String command = "     cReATE dATABasE Siema elo ";
        try {
            System.out.println(Parser.parseSQLtoJSON(command));
            fail("Exception should be thrown");
        } catch (InvalidKeyWordException | JSONException | IncorrectSyntaxException | NotSpecifiedFromWhichTableException e) {
        }
    }

    @Test
    void parseUpdateCommand() throws JSONException, NotSpecifiedFromWhichTableException, InvalidKeyWordException, IncorrectSyntaxException {
        String command = "UPDATE table_name\n" +
                "SET column1 = value1, column2 = value2" +
                " WHERE sth=sthOther";
        System.out.println(Parser.parseSQLtoJSON(command));
    }

    @Test
    void parseDeleteCommand() throws JSONException, NotSpecifiedFromWhichTableException, InvalidKeyWordException, IncorrectSyntaxException {
        String command = "DELETE FROM table_name\n" +
                "WHERE condition;";
        System.out.println(Parser.parseSQLtoJSON(command));
    }

    @Test
    void parseUseCommand() throws JSONException, NotSpecifiedFromWhichTableException, InvalidKeyWordException, IncorrectSyntaxException {
        String command = "USE pieRniczek";
        System.out.println(Parser.parseSQLtoJSON(command));
    }






}