import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

public class FileHandler {
    static JSONArray getFileData(String fileName) throws Exception {
        return (JSONArray) new JSONParser().parse(new FileReader("databases\\" + fileName + ".json"));
    }

    static String createSavingString(String data){
        data = data.replace(":{", ":{\t");
        data = data.replace("null,", "null,\t");
        data = data.replace("\",", "\",\t");
        data = data.replace(",", ",\n\t");
        data = data.replace("{", "{\n\t");
        data = data.replace("},", "\n\t},");
        data = data.replace("}]", "\n}]");
        return data;
    }

    static void saveFile(String fileName, JSONArray data) throws Exception {
        FileWriter file = new FileWriter("databases\\" + fileName + ".json");
        file.write(createSavingString(data.toJSONString()));
        file.close();
    }
}
