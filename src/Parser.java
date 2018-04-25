import com.powder.Exception.IncorrectSyntaxException;
import com.powder.Exception.InvalidKeyWordException;
import com.powder.Exception.NotSpecifiedFromWhichTableException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static JSONObject parseSQLtoJSON(String command) throws InvalidKeyWordException, JSONException, NotSpecifiedFromWhichTableException, IncorrectSyntaxException {
        JSONObject jsonObject = null;

        List<String> words = Arrays.asList(command.split("\\s+"));


        switch(words.get(0).toUpperCase()){
            case "SELECT":
                jsonObject = selectCase(words);
                break;
            case "INSERT":
                jsonObject = insertCase(words);
                break;
            case "UPDATE":
                break;
            case "DELETE":
                break;
            case "CREATE":
                break;
            case "SET":
                break;
            default:
                throw new InvalidKeyWordException();
        }

        jsonObject.put("Target", "table");
        return jsonObject;
    }

    private static JSONObject selectCase(List<String> words) throws NotSpecifiedFromWhichTableException, JSONException {
        JSONObject jsonObject = new JSONObject();
        int howManyColumns = 0;
        for(int i = 1; i < words.size(); i++){
            if(words.get(i).equalsIgnoreCase("from")){
               break;
            }else{
                howManyColumns++;
            }
        }

        List<String> columnNames = new ArrayList<>();
        for(String columnName : words.subList(1, howManyColumns + 1)){
            if(!columnName.equals(",")){
                columnNames.add(columnName.replace(",", ""));
            }
        }

        String tableName;
        if(howManyColumns == words.size() - 1){
            throw new NotSpecifiedFromWhichTableException();
        }else{
            //TODO: implement nested selects possibillity
            tableName = words.get(howManyColumns + 2);
        }

        if(howManyColumns + 2 == words.size() - 1){
            JSONObject query = new JSONObject();
            query.put("Operation", "select");
            query.put("TableName", tableName);
            JSONArray JSONcolumnNames = new JSONArray();
            JSONcolumnNames.put(columnNames);
            query.put("ColumnNames", JSONcolumnNames);
            jsonObject.put("Query", query);
        }else{
            //TODO: implement WHERE and JOIN
        }

        return jsonObject;
    }

    private static JSONObject insertCase(List<String> words) throws InvalidKeyWordException, IncorrectSyntaxException, JSONException {
        JSONObject jsonObject= new JSONObject();
        if(!(words.get(1).equalsIgnoreCase("into"))){
            throw new InvalidKeyWordException();
        }

        if(words.get(2).equalsIgnoreCase("select")){
            return new JSONObject();//TODO: implement insert table to another table
        }

        String tableName = words.get(2);

        if(!(words.get(3).startsWith("("))){
            throw new IncorrectSyntaxException();
        }

        int numberOfColumns = 0;
        for(int i = 3; i < words.size(); i++){
            if(words.get(i).equalsIgnoreCase("values")){
                break;
            }else{
                numberOfColumns++;
            }
        }

        List<String> columnNames = getKeys(words.subList(3, 3 + numberOfColumns));

        if(!(words.get(3 + numberOfColumns + 1).startsWith("("))){
            throw new IncorrectSyntaxException();
        }
        int numberOfValues = 0;
        for(int i = 3 + numberOfColumns + 1; i < words.size(); i++){
            if(words.get(i).endsWith(")")){
                break;
            }else{
                numberOfValues++;
            }
        }

        List<String> values = getValues(words.subList(3 + numberOfColumns + 1, 3 + numberOfColumns + 1 + numberOfValues + 1));


        JSONObject query = new JSONObject();
        JSONArray JSONcolumnNames = new JSONArray(columnNames);
        JSONArray JSONvalues = new JSONArray(values);
        query.put("Operation", "insert");
        query.put("TableName", tableName);
        query.put("Keys", JSONcolumnNames);
        query.put("Values", JSONvalues);
        jsonObject.put("Query", query);
        return jsonObject;
    }

    private static List<String> getKeys(List<String> words) throws IncorrectSyntaxException {

        StringBuilder sB = new StringBuilder();
        for(String str : words){
            sB.append(str);
        }
        String columnNamesString = sB.toString();
        if(! (columnNamesString.startsWith("(") && columnNamesString.endsWith(")"))){
            throw new IncorrectSyntaxException();
        }

        columnNamesString = columnNamesString.replace("(", "").replace(")", "");

        return Arrays.asList(columnNamesString.split(","));
    }

    private static List<String> getValues(List<String> words) throws IncorrectSyntaxException {
        //TODO: work on not deleting space between two words
        String valuesString = listToStringWithSpaces(words);
        if(!(valuesString.startsWith("(") && valuesString.endsWith(")"))){
            throw new IncorrectSyntaxException();
        }

        valuesString = valuesString.replace("(", "").replace(")", "");

        List<String> values = new ArrayList<>();
        for(String value : valuesString.split(",")){
            values.add(value.replace("\"", "").replace("\\", ""));
        }
        return values;
    }

    private static String listToStringWithSpaces(List<String> words) {
        Pattern pattern = Pattern.compile("\".+\"");
        StringBuilder toReturn = new StringBuilder();
        for(String word : words){
            toReturn.append(word);
            Matcher matcher = pattern.matcher(word);
            if(!matcher.find()){
                if(!word.endsWith("\"") && !word.endsWith(",") && !word.endsWith(")")){
                    toReturn.append(" ");
                }
            }
        }
        return toReturn.toString();
    }
}








