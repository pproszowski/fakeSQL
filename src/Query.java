import org.json.JSONException;

public abstract class Query {
    public abstract Response execute(Storage storage) throws JSONException;
}
