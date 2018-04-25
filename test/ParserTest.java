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
        String json1 = "{\"Target\":\"table\",\"Query\":{\"TableName\":\"superTable\",\"Keys\":[\"zbiki\",\"dziecioly\"],\"Values\":[\"szczecin\",\"wlasnoscPanstwa\"],\"Operation\":\"insert\"}}";
        String json2 = "{\"Target\":\"table\",\"Query\":{\"TableName\":\"pracownicy\",\"Keys\":[\"imie\",\"nazwisko\",\"wiek\",\"posada\"],\"Values\":[\"Piotr\",\"Proszowski\",\"21\",\"programista Java\"],\"Operation\":\"insert\"}}";
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






}