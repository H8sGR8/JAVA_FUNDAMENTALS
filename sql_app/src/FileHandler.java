import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

public class FileHandler {
    static JSONArray getFileData(String fileName) throws Exception {
        return (JSONArray) new JSONParser().parse(new FileReader("databases\\" + fileName + ".json"));
    }

    static void saveFile(String fileName, JSONArray data) throws Exception {
        FileWriter file = new FileWriter("databases\\" + fileName + ".json");
        file.write(data.toJSONString());
        file.close();
    }
}
