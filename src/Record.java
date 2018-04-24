import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Record {
    private Map<String, Tuple> values;

    public Record(Map<String, Tuple> _values){
        values = new HashMap<>(_values);
    }

    public Record(Record _record){
        this.values = new HashMap<>(_record.values);
    }

    public Record(Record _record, List<String> columnNames) {
        values = new HashMap<>(_record.getValues());
        Map<String, Tuple> toRemove = new HashMap<>();
        for(Map.Entry<String, Tuple> entry : values.entrySet()){
            if(!(columnNames.contains(entry.getKey()))){
                toRemove.put(entry.getKey(), entry.getValue());
            }
        }
        values.entrySet().removeAll(toRemove.entrySet());
    }

    public Record(JSONObject record) throws JSONException {
        values = new Gson().fromJson(
                record.getJSONObject("NewValues").toString(), new TypeToken<HashMap<String, Tuple>>(){}.getType()
        );
    }

    public Record getRecordWithOnlySpecifiedColumns(List<String> whichColumns){
        Map<String, Tuple> newValues = new HashMap<>();
        for(Map.Entry entry : values.entrySet()){
            String columnName = (String) entry.getKey();
            if(whichColumns.contains(columnName)){
                newValues.put((String)entry.getKey(), (Tuple)entry.getValue());
            }
        }
        return new Record(newValues);
    }


    public Map<String, Tuple> getValues(){
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sB = new StringBuilder();
        for(Map.Entry<String, Tuple> entry : values.entrySet()){
            sB.append(entry.getValue().toString()).append(" ").append("\n");
        }

        return sB.toString();
    }

}
