import com.powder.Exception.IncorrectSyntaxException;
import com.powder.Exception.InvalidKeyWordException;
import com.powder.Exception.NotSpecifiedFromWhichTableException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static JSONObject parseSQLtoJSON(String command) throws InvalidKeyWordException, JSONException, NotSpecifiedFromWhichTableException, IncorrectSyntaxException {
        JSONObject jsonObject;

        Pattern pattern = Pattern.compile("\\s*(\\w+)\\s+.*");
        Matcher matcher = pattern.matcher(command);
        if(!matcher.find()){
            throw new InvalidKeyWordException();
        }

        String keyWord = matcher.group(1).toUpperCase();

        switch(keyWord){
            case "CREATE":
                jsonObject = createCase(command);
                break;
            case "USE":
                jsonObject = setCase(command);
                break;
            case "INSERT":
                jsonObject = insertCase(command);
                break;
            case "SELECT":
                jsonObject = selectCase(command);
                break;
            case "UPDATE":
                jsonObject = updateCase(command);
                break;
            case "DELETE":
                jsonObject = deleteCase(command);
                break;
            default:
                throw new InvalidKeyWordException();
        }

        return jsonObject;
    }

    private static JSONObject createCase(String command) throws JSONException, IncorrectSyntaxException, InvalidKeyWordException {
        Pattern pattern = Pattern.compile("\\s*(\\w+)\\s+(\\w+).*");
        Matcher matcher = pattern.matcher(command);
        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

       String keyWord = matcher.group(2).toUpperCase();

        JSONObject jsonObject = null;
        switch(keyWord){
            case "TABLE":
                jsonObject = createTableCase(command);
                jsonObject.put("Target", "Database");
                break;
            case "DATABASE":
                jsonObject = createDatabaseCase(command);
                jsonObject.put("Target", "Storage");
                break;
        }

        return jsonObject;
    }

    private static JSONObject createTableCase(String command) throws JSONException, IncorrectSyntaxException {
        Pattern pattern = Pattern.compile("\\s*\\w+\\s+\\w+\\s+(\\w+)\\s*\\((.*)\\)[;]?\\s*");
        Matcher matcher = pattern.matcher(command);
        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        String tableName = matcher.group(1);
        String columnsString = matcher.group(2);

        String[] columns = columnsString.split(",");
        JSONArray jsonColumns = new JSONArray();
        for(String column : columns){
            JSONObject jsonColumn = new JSONObject();
            Pattern columnPattern = Pattern.compile("\\s*(\\w+)\\s+(\\w+)(\\s*\\(\\s*\\d+\\s*\\)\\s*)?[,]?");
            Matcher columnMatcher = columnPattern.matcher(column);
            if(!columnMatcher.find()){
                throw new IncorrectSyntaxException();
            }
            jsonColumn.put("Name", columnMatcher.group(1));
            JSONObject jsonType = new JSONObject();
            jsonType.put("Name", columnMatcher.group(2));
            if(columnMatcher.group(3) != null){
                jsonType.put("Limit", columnMatcher.group(3).replace("(", "")
                                                      .replace(")", "")
                                                      .replaceAll("\\s+", ""));
            }else{
                jsonType.put("Limit", 255);
            }
            jsonColumns.put(jsonColumn);
        }

        JSONObject jsonObject = new JSONObject();
        JSONObject query = new JSONObject();
        query.put("Operation", "addtable");
        JSONObject table = new JSONObject();
        table.put("Name", tableName);
        table.put("Columns", jsonColumns);
        table.put("Records", new JSONObject());
        query.put("Table", table);
        jsonObject.put("Query", query);

        return jsonObject;

    }

    private static JSONObject createDatabaseCase(String command) throws JSONException, IncorrectSyntaxException {
        JSONObject jsonObject = new JSONObject();

        Pattern pattern = Pattern.compile("\\s*(\\w+)\\s+(\\w+)\\s+(\\S+)\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        JSONObject query = new JSONObject();
        query.put("Operation", "adddatabase");
        JSONObject database = new JSONObject();
        database.put("Name", matcher.group(3));
        database.put("Tables", new JSONArray());
        query.put("Database", database);
        jsonObject.put("Query", query);

        return jsonObject;
    }

    private static JSONObject selectCase(String command) throws JSONException, IncorrectSyntaxException, InvalidKeyWordException {
        JSONObject jsonObject = new JSONObject();

        Pattern pattern = Pattern.compile("\\s*\\w+\\s+(.*)\\s+(\\w+)\\s+(\\w+)\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        String columnsString = matcher.group(1);
        String[] columnNames = columnsString.split(",");
        if(!matcher.group(3).toUpperCase().equals("FROM")){
            throw new InvalidKeyWordException();
        }
        String tableName = matcher.group(3);
        JSONArray jsonColumnNames = new JSONArray();
        for(String columnName : columnNames){
            jsonColumnNames.put(columnName);
        }

        JSONObject query = new JSONObject();

        query.put("Name", tableName);
        query.put("Operation", "select");
        query.put("ColumnNames", jsonColumnNames);
        query.put("Records", new JSONArray());

        jsonObject.put("Query", query);

        jsonObject.put("Target", "table");
        return jsonObject;
    }

    private static JSONObject insertCase(String command) throws InvalidKeyWordException, IncorrectSyntaxException, JSONException {
        JSONObject jsonObject = new JSONObject();
        Pattern pattern = Pattern.compile("\\s*\\w+\\s+(\\w+)\\s+(\\w+)\\s*\\(\\s*(.*)?\\s*\\)\\s*(\\w+)\\s*\\(\\s*(.*)\\s*\\)\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);
        if(!matcher.find() || matcher.matches()){
            throw new IncorrectSyntaxException();
        }
        if(!matcher.group(1).equalsIgnoreCase("INTO")){
            throw new InvalidKeyWordException();
        }

        String tableName = matcher.group(2);
        String columnNamesString = matcher.group(3);
        String[] columnNames = null;
        if(columnNamesString != null){
            columnNames = columnNamesString.split(",");
        }

        if(!matcher.group(4).equalsIgnoreCase("VALUES")){
            throw new InvalidKeyWordException();
        }

        String[] v = matcher.group(5).split(",");
        List<String> values = new ArrayList<>();
        for(String value : v){
            value = value.replace("'", "").replace("\"", "");
            value = value.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            values.add(value);
        }

        jsonObject.put("Target", "table");
        JSONObject query = new JSONObject();
        query.put("Operation", "insert");
        query.put("Name", tableName);
        Map<String, Tuple> records = new HashMap<>();
        if(columnNames != null){
            if(columnNames.length != values.size()){
                throw new IncorrectSyntaxException();
            }

            for(int i = 0; i < columnNames.length; i++){
                String columName = columnNames[i].replaceAll("^\\s+", "").replaceAll("\\s+$", "");
                records.put(columName, new Tuple(values.get(i)));
            }
        }else{
            for(int i = 0; i < values.size(); i++){
                records.put(String.valueOf(i), new Tuple(values.get(i)));
            }
        }

        query.put("Record", records);
        jsonObject.put("Query", query);



        return jsonObject;
    }

    private static JSONObject updateCase(String command) throws JSONException, IncorrectSyntaxException, InvalidKeyWordException {
        JSONObject jsonObject = new JSONObject();
        Pattern pattern = Pattern.compile("\\s*\\w+\\s*(\\w+)\\s*(\\w+)\\s*(.*)\\s*(\\w+\\s*(.*))?[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        String tableName = matcher.group(1);
        if(!matcher.group(2).equalsIgnoreCase("SET")){
            throw new InvalidKeyWordException();
        }

        String[] changes = matcher.group(3).split(",");

        jsonObject.put("Target", "table");
        JSONObject query = new JSONObject();
        query.put("Operation", "update");
        Map<String, Tuple> updates = new HashMap<>();
        for(String change : changes){
            Pattern changePattern = Pattern.compile("(\\w+)\\s*=\\s*(\\S*)\\s*[,]?");
            Matcher changeMatcher = changePattern.matcher(change);
            if(!changeMatcher.find()){
                throw new IncorrectSyntaxException();
            }
            String value = changeMatcher.group(2).replace("'", "").replace("\"","");
            updates.put(changeMatcher.group(1), new Tuple(value));
        }

        query.put("Changes", updates);
        query.put("Condition", new JSONObject());
        query.put("Name", tableName);
        jsonObject.put("Query", query);
        return jsonObject;
    }

    private static JSONObject setCase(String command) throws JSONException, IncorrectSyntaxException {
        JSONObject jsonObject = new JSONObject();
        Pattern pattern = Pattern.compile("\\s*\\w+\\s+(\\w+)\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        jsonObject.put("Target", "Storage");
        JSONObject query = new JSONObject();
        query.put("Operation", "setcurrentdatabase");
        query.put("Name", matcher.group(1));
        jsonObject.put("Query", query);
        return jsonObject;
    }

    private static JSONObject deleteCase(String command) throws JSONException, IncorrectSyntaxException, InvalidKeyWordException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Target", "table");
        Pattern pattern = Pattern.compile("\\s*\\w+\\s+(\\w+)\\s+(\\w+)(\\s+\\w+\\s+(.*))?\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        if(!matcher.group(1).equalsIgnoreCase("from")){
            throw new InvalidKeyWordException();
        }

        String tableName = matcher.group(2);
        JSONObject query = new JSONObject();
        query.put("Name", tableName);
        query.put("Condition", new JSONObject());
        query.put("Operation", "delete");
        jsonObject.put("Query", query);


        return jsonObject;
    }
}








