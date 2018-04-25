import com.powder.Exception.IncorrectSyntaxException;
import com.powder.Exception.InvalidKeyWordException;
import com.powder.Exception.NotSpecifiedFromWhichTableException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    public void parseSelectCommand(){
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

    @Test void parseInsertCommand(){
        String command = "INsERt    inTO superTable (zbiki, dziecioly) ValuEs (\"szczecin\" , \"wlasnoscPanstwa\")";
        String command2 = "Insert into pracownicy (imie, nazwisko, wiek, posada) \n VALUES (\"Piotr\", \"Proszowski\", 21, \"programista Java\")";
        try {
            System.out.println(Parser.parseSQLtoJSON(command));
            System.out.println(Parser.parseSQLtoJSON(command2));
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




}