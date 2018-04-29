import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourcesTest {
    @Test
     void readingRessourcesIsPossible() throws IOException {
        InputStream res = ResourcesTest.class.getResourceAsStream("/storageNames.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(res));
        String line = null;
        while((line = reader.readLine()) != null ){
            System.out.println(line);
        }
        reader.close();

    }

    @Test
    void save() throws IOException {
        ResourceManager resourceManager = new ResourceManager("/", "storageNames.txt" );
        resourceManager.saveJSONToResource(new JSONObject());
    }
}
