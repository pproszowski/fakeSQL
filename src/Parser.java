import com.powder.Exception.IncorrectSyntaxException;
import com.powder.Exception.InvalidKeyWordException;
import com.powder.Exception.UnknownTypeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static JSONObject parseSQLtoJSON(String command) throws InvalidKeyWordException, JSONException, IncorrectSyntaxException, UnknownTypeException {
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
                jsonObject = useCase(command);
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
            case "DROP":
                jsonObject = dropCase(command);
                break;
            default:
                throw new InvalidKeyWordException();
        }

        return jsonObject;
    }

    private static JSONObject createCase(String command) throws JSONException, IncorrectSyntaxException {
        Pattern pattern = Pattern.compile("\\s*(\\w+)\\s+(\\w+)[\\s\\S]*");
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
        Pattern pattern = Pattern.compile("(?i)\\s*create\\s+table\\s+(\\w+)\\s*\\(([\\s\\S]+)\\)[;]?\\s*");
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
            jsonColumn.put("Type", jsonType);
            jsonColumns.put(jsonColumn);
        }

        JSONObject jsonObject = new JSONObject();
        JSONObject query = new JSONObject();
        query.put("Operation", "addtable");
        JSONObject table = new JSONObject();
        table.put("Name", tableName);
        table.put("Columns", jsonColumns);
        table.put("Records", new JSONArray());
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

        Pattern pattern;
        pattern = Pattern.compile("(?i)\\s*select\\s+(.+)\\s+from\\s+(\\S+)(\\s+where\\s+(\\w+.*))?\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        String columnsString = matcher.group(1);
        String[] columnNames = columnsString.split(",");

        String tableName = matcher.group(2);
        JSONArray jsonColumnNames = new JSONArray();
        for(String columnName : columnNames){
            columnName = columnName.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            jsonColumnNames.put(columnName);
        }

        JSONArray jsonConditions;
        if(matcher.groupCount() > 3){
            jsonConditions = getConditions(matcher.group(4));
        }else{
            jsonConditions = new JSONArray();
        }

        JSONObject query = new JSONObject();

        query.put("Name", tableName);
        query.put("Operation", "select");
        query.put("ColumnNames", jsonColumnNames);
        query.put("Records", new JSONArray());
        query.put("Conditions", jsonConditions);

        jsonObject.put("Query", query);

        jsonObject.put("Target", "table");
        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    private static JSONObject insertCase(String command) throws InvalidKeyWordException, IncorrectSyntaxException, JSONException, UnknownTypeException {
        JSONObject jsonObject = new JSONObject();
        Pattern pattern = Pattern.compile("\\s*\\w+\\s+(\\w+)\\s+(\\w+)\\s*(\\(\\s*[\\s\\S]*\\s*\\))?\\s+(\\w+)\\s*\\(\\s*([\\s\\S]*)\\s*\\)\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);
        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }
        if(!matcher.group(1).equalsIgnoreCase("INTO")){
            throw new InvalidKeyWordException();
        }

        String tableName = matcher.group(2);
        String columnNamesString = matcher.group(3);
        columnNamesString = columnNamesString.replace("(", "").replace(")", "");
        String[] columnNames;
        columnNames = columnNamesString.split(",");

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
        Map<String, Tuple> record = new HashMap<>();
        if(columnNames.length != values.size()){
            throw new IncorrectSyntaxException();
        }

        for(int i = 0; i < columnNames.length; i++){
            String columnName = columnNames[i].replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            record.put(columnName, new Tuple(values.get(i)));
        }

        query.put("Record", record);
        jsonObject.put("Query", query);


        return jsonObject;
    }

    private static JSONObject updateCase(String command) throws JSONException, IncorrectSyntaxException, InvalidKeyWordException, UnknownTypeException {
        JSONObject jsonObject = new JSONObject();
        Pattern pattern = Pattern.compile("(?i)\\s*update\\s+(\\S+)\\s+set\\s+((\\S+\\s*=\\s*\\S+\\s+)+)(where\\s+(.+))?\\s*?[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find()){
            throw new IncorrectSyntaxException();
        }

        String tableName = matcher.group(1);
        String[] changes = matcher.group(2).split(",");

        jsonObject.put("Target", "table");
        JSONObject query = new JSONObject();
        query.put("Operation", "update");
        Map<String, Tuple> updates = new HashMap<>();

        Pattern changePattern = Pattern.compile("(\\w+)\\s*=\\s*(\\S*)\\s*[,]?");
        for(String change : changes){
            Matcher changeMatcher = changePattern.matcher(change);
            if(!changeMatcher.find()){
                throw new IncorrectSyntaxException();
            }
            String value = changeMatcher.group(2).replace("'", "").replace("\"","");
            updates.put(changeMatcher.group(1), new Tuple(value));
        }

        JSONArray jsonConditions = getConditions(matcher.group(4));

        query.put("Changes", updates);
        query.put("Conditions", jsonConditions);
        query.put("Name", tableName);
        jsonObject.put("Query", query);

        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    private static JSONObject useCase(String command) throws JSONException, IncorrectSyntaxException {
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
        Pattern pattern = Pattern.compile("(?i)\\s*delete\\s+from\\s+(\\S+)(\\s+where\\s+(.+))?\\s*[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find() || !matcher.matches()){
            throw new IncorrectSyntaxException();
        }

        String tableName = matcher.group(1);
        JSONObject query = new JSONObject();
        JSONArray jsonConditions;
        if(matcher.groupCount() > 2){
            jsonConditions = getConditions(matcher.group(3));
        }else{
            jsonConditions = new JSONArray();
        }
        query.put("Name", tableName);
        query.put("Conditions", jsonConditions);
        query.put("Operation", "delete");
        jsonObject.put("Query", query);


        return jsonObject;
    }

    private static JSONArray getConditions(String conditions) throws JSONException {
        JSONArray jsonConditions = new JSONArray();
        if(conditions != null){
            Pattern pattern = Pattern.compile("(?i)(\\s*(\\S+)\\s*([>=<])\\s*(\\S+)(\\s+(AND|OR))?\\s*).*");
            Matcher matcher = pattern.matcher(conditions);
            while (matcher.find()){
                JSONObject jsonCondition = new JSONObject();
                String connector = matcher.group(5);
                if(connector != null){
                    connector = connector.replaceAll("\\s", "");
                }
                System.out.println(connector);
                if(connector == null){
                    jsonCondition.put("Connector", "null");
                }else{
                    jsonCondition.put("Connector", connector);
                }
                jsonCondition.put("Tuple", matcher.group(4).replaceAll("\"", ""));
                jsonCondition.put("ColumnName", matcher.group(2));
                jsonCondition.put("Symbol", matcher.group(3));
                jsonConditions.put(jsonCondition);
                conditions = conditions.replace(matcher.group(1), "");
                matcher = pattern.matcher(conditions);
            }
        }

        for(int i = 0; i < jsonConditions.length(); i++){
            System.out.println(jsonConditions.get(i).toString());
        }

        return jsonConditions;
    }

    private static JSONObject dropCase(String command) throws IncorrectSyntaxException, JSONException {
        Pattern pattern = Pattern.compile("(?i)\\s*drop\\s+(database|table)\\s+(\\S+)[;]?\\s*");
        Matcher matcher = pattern.matcher(command);

        if(!matcher.find()){
            throw new IncorrectSyntaxException();
        }

        JSONObject jsonObject = null;
        String name = matcher.group(2);
        String keyWord = matcher.group(1);
        switch(keyWord.toUpperCase()){
            case "TABLE":
                jsonObject = dropTableCase(name);
                jsonObject.put("Target", "Database");
                break;
            case "DATABASE":
                jsonObject = dropDatabaseCase(name);
                jsonObject.put("Target", "Storage");
                break;
        }

        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    private static JSONObject dropTableCase(String name) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonQuery = new JSONObject();
        jsonQuery.put("Operation", "droptable");
        jsonQuery.put("Name", name);
        jsonObject.put("Query", jsonQuery);
        return jsonObject;
    }

    private static JSONObject dropDatabaseCase(String name) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonQuery = new JSONObject();
        jsonQuery.put("Operation", "dropdatabase");
        jsonQuery.put("Name", name);
        jsonObject.put("Query", jsonQuery);
        return jsonObject;
    }
}








