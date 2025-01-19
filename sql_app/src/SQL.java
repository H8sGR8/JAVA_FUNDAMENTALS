import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

class Pair<K, V>{
    K key;
    V value;
    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K getFirst() {
        return key;
    }

    V getSecond() {
        return value;
    }
}

class SQL {

    static Set getColumns(JSONObject record){
        return ((JSONObject) record.get("Data")).keySet();
    }

    static JSONObject createObject(JSONArray database){
        JSONObject object = new JSONObject();
        object.put("ID", getNextID(database));
        JSONObject data = new JSONObject();
        for(Object key : getColumns((JSONObject) database.getFirst())) data.put(key, null);
        object.put("Data", data);
        return object;
    }

    static void fromFunction(String dataFile, HashMap<String, JSONArray> databases) throws Exception {
        databases.put(dataFile, FileHandler.getFileData(dataFile));
    }

    static ArrayList<ArrayList<String>> selectFunction(Pair<String, JSONArray> data, ArrayList<Pair<String, String>> selectedItems){
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        for(Pair key : selectedItems) row.add(key.getFirst() + "." + key.getSecond());
        result.add((ArrayList<String>) row.clone());
        for(Object o : data.getSecond()){
            row.clear();
            JSONObject record = (JSONObject) o;
            for(Pair key : selectedItems) {
                if (key.getFirst().equals(data.getFirst())) {
                    if (key.getSecond().equals("ID")) row.add(record.get(key.getSecond()).toString());
                    else if (((JSONObject) record.get("Data")).containsKey(key.getSecond()))
                        row.add(((JSONObject) record.get("Data")).get(key.getSecond()).toString());
                } else {
                    boolean broken = false;
                    for (Object item : ((JSONObject) record.get("Data")).keySet())
                        if (((JSONObject) record.get("Data")).get(item) instanceof Pair &&
                                ((Pair) ((JSONObject) record.get("Data")).get(item)).getFirst() == key.getFirst()) {
                            row.add(((JSONObject) (((Pair) ((JSONObject) record.get("Data")).get(item)).getSecond())).get(key.getSecond()).toString());
                            broken = true;
                            break;
                        }
                    if (!broken) row.add("");
                }
            }
            result.add((ArrayList<String>) row.clone());
        }
        return result;
    }

    static long getNextID(JSONArray data){
        if (data.isEmpty()) return 1;
        JSONObject record = (JSONObject) data.getLast();
        return (long)record.get("ID") + 1;
    }

    static void insertFunction(JSONArray data, ArrayList<String> selectedItems, ArrayList<ArrayList<String>> newObjects) {
        for(ArrayList<String> object : newObjects) {
            JSONObject newObject = createObject(data);
            for (String item : selectedItems) if (getColumns((JSONObject) data.getFirst()).contains(item))
                ((JSONObject) newObject.get("Data")).put(item, object.get(selectedItems.indexOf(item)));
            data.add(newObject);
        }
    }

    static void deleteFunction(JSONArray data, JSONArray dataToDelete) {
        for (Object o : dataToDelete) data.remove(o);
    }

    static void updateFunction(JSONArray data, String item, String value) {
        for (Object o : data){
            JSONObject record = (JSONObject) o;
            if(((JSONObject) record.get("Data")).containsKey(item)) ((JSONObject) record.get("Data")).put(item, value);
        }
    }

    static JSONArray whereFunction(JSONArray data, Comparator comparator, Object item, Object value) {
        JSONArray where = new JSONArray();
        for (Object o : data) {
            JSONObject record = (JSONObject) o;
            if(item == "ID") {if (comparator.compare( record.get("ID"), value)) where.add(record);}
            else if(comparator.compare(((JSONObject) record.get("Data")).get(item), value)) where.add(record);
        }
        return where;
    }

    static JSONArray joinFunction(JSONArray data, Pair<String, JSONArray> dataToJoin, Object referenceValue, Object value) {
        JSONArray join = new JSONArray();
        for (Object o : data) {
            JSONObject record = (JSONObject) o;
            join.add(record);
            for (Object o2 : dataToJoin.getSecond()) {
                JSONObject record2 = (JSONObject) o2;
                if((new IsEqual()).compare(((JSONObject)((JSONObject)join.getLast()).get("Data")).get(referenceValue), record2.get(value))) {
                    ((JSONObject) ((JSONObject) join.getLast()).get("Data")).put(referenceValue, new Pair(dataToJoin.getFirst(), record2.get("Data")));
                    break;
                }
            }
        }
        return join;
    }
}
