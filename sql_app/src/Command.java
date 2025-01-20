import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

abstract class Command {}

class FromCommand extends Command{
    void fromFunction(String dataFile, HashMap<String, JSONArray> databases) throws Exception {
        databases.put(dataFile, FileHandler.getFileData(dataFile));
    }
}

class SelectCommand extends Command{

    ArrayList<String> addHeaders(ArrayList<Pair<String, String>> selectedItems){
        ArrayList<String> row = new ArrayList<>();
        for(Pair key : selectedItems) row.add(key.getFirst() + "." + key.getSecond());
        return row;
    }

    void gatherAllJoinedTables(HashMap<String, JSONObject> joinedTables, Pair<String, JSONObject> record){
        joinedTables.put(record.getFirst(), record.getSecond());
        for(Object value : ((JSONObject) record.getSecond().get("Data")).values())
            if (value instanceof Pair) gatherAllJoinedTables(joinedTables, (Pair) value);
    }

    Object getValueForDataKey(HashMap<String, JSONObject> joinedTables, Pair<String, String> key){
        return ((JSONObject)joinedTables.get(key.getFirst()).get("Data")).get(key.getSecond());
    }

    String getValueForKey(HashMap<String, JSONObject> joinedTables, Pair<String, String> key){
        if(!joinedTables.containsKey(key.getFirst())) return "";
        if(key.getSecond().equals("ID")) return joinedTables.get(key.getFirst()).get(key.getSecond()).toString();
        if(!(getValueForDataKey(joinedTables, key) instanceof Pair))
                return getValueForDataKey(joinedTables, key).toString();
        return ((JSONObject)((Pair)(getValueForDataKey(joinedTables, key))).getSecond()).get("ID").toString();
    }

    ArrayList<String> addValuesToHeaders(HashMap<String, JSONObject> joinedTables, ArrayList<Pair<String, String>> selectedItems){
        ArrayList<String> row = new ArrayList<>();
        for(Pair key : selectedItems) row.add(getValueForKey(joinedTables, key));
        return row;
    }

    ArrayList<ArrayList<String>> select(HashMap<String, JSONArray> data, ArrayList<Pair<String, String>> selectedItems){
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(addHeaders(selectedItems));
        for(String key : data.keySet()){
            if(data.get(key) == null) continue;
            for(Object o : data.get(key)) {
                HashMap<String, JSONObject> joinedTables = new HashMap<>();
                JSONObject record = (JSONObject) o;
                gatherAllJoinedTables(joinedTables, new Pair<>(key, record));
                result.add(addValuesToHeaders(joinedTables, selectedItems));
            }
        }
        return result;
    }
}

class InsertCommand extends Command{

    Set getColumns(JSONObject record){
        return ((JSONObject) record.get("Data")).keySet();
    }

    JSONObject createObject(JSONArray database){
        JSONObject object = new JSONObject();
        object.put("ID", getNextID(database));
        JSONObject data = new JSONObject();
        for(Object key : getColumns((JSONObject) database.getFirst())) data.put(key, null);
        object.put("Data", data);
        return object;
    }

    long getNextID(JSONArray data){
        if (data.isEmpty()) return 1;
        JSONObject record = (JSONObject) data.getLast();
        return (long)record.get("ID") + 1;
    }

    void insert(String fileName, ArrayList<String> selectedItems, ArrayList<ArrayList<String>> newObjects) throws Exception {
        JSONArray data = FileHandler.getFileData(fileName);
        for(ArrayList<String> object : newObjects) {
            JSONObject newObject = createObject(data);
            for (String item : selectedItems) if (getColumns((JSONObject) data.getFirst()).contains(item))
                ((JSONObject) newObject.get("Data")).put(item, object.get(selectedItems.indexOf(item)));
            data.add(newObject);
        }
        FileHandler.saveFile(fileName, data);
    }
}

class DeleteCommand extends Command{
    void delete(String fileName, JSONArray dataToDelete) throws Exception {
        JSONArray data = FileHandler.getFileData(fileName);
        for (Object o : dataToDelete) data.remove(o);
        FileHandler.saveFile(fileName, data);
    }
}

class UpdateCommand extends Command{
    void update(String fileName, JSONArray dataToUpdate, String item, String value) throws Exception {
        JSONArray data = FileHandler.getFileData(fileName);
        for (Object o : data){

            if (!dataToUpdate.contains(o)) continue;
            if(((JSONObject) ((JSONObject) o).get("Data")).containsKey(item)) ((JSONObject) ((JSONObject) o).get("Data")).put(item, value);
        }
        FileHandler.saveFile(fileName, data);
    }
}

class WhereCommand extends Command{
    JSONArray where(JSONArray data, Comparator comparator, Object item, Object value) {
        JSONArray where = new JSONArray();
        for (Object o : data) {
            JSONObject record = (JSONObject) o;
            if(item == "ID") {if (comparator.compare( record.get("ID"), value)) where.add(record);}
            else if(comparator.compare(((JSONObject) record.get("Data")).get(item), value)) where.add(record);
        }
        return where;
    }
}

abstract class JoinCommand extends Command{

    abstract Pair<JSONArray, JSONArray> join(JSONArray data, Pair<String, JSONArray> dataToJoin, Object referenceValue, Object value);
}

class InnerJoinCommand extends JoinCommand{

    Pair<JSONArray, JSONArray> join(JSONArray data, Pair<String, JSONArray> dataToJoin, Object referenceValue, Object value) {
        JSONArray join = new JSONArray();
        for (Object o : data) {
            JSONObject record = (JSONObject) o;
            for (Object o2 : dataToJoin.getSecond()) {
                JSONObject record2 = (JSONObject) o2;
                if((new IsEqual()).compare(((JSONObject)(record.get("Data"))).get(referenceValue), record2.get(value))) {
                    ((JSONObject) record.get("Data")).put(referenceValue, new Pair(dataToJoin.getFirst(), record2));
                    join.add(record);
                    break;
                }
            }
        }
        return new Pair(join, null);
    }
}

class LeftJoinCommand extends JoinCommand{

    Pair<JSONArray, JSONArray> join(JSONArray data, Pair<String, JSONArray> dataToJoin, Object referenceValue, Object value) {
        JSONArray join = new JSONArray();
        for (Object o : data) {
            JSONObject record = (JSONObject) o;
            join.add(record);
            for (Object o2 : dataToJoin.getSecond()) {
                JSONObject record2 = (JSONObject) o2;
                if((new IsEqual()).compare(((JSONObject)(record.get("Data"))).get(referenceValue), record2.get(value))) {
                    ((JSONObject) ((JSONObject) join.getLast()).get("Data")).put(referenceValue, new Pair(dataToJoin.getFirst(), record2));
                    break;
                }
            }
        }
        return new Pair(join, null);
    }
}

class RightJoinCommand extends JoinCommand{

    Pair<JSONArray, JSONArray> join(JSONArray data, Pair<String, JSONArray> dataToJoin, Object referenceValue, Object value) {
        JSONArray join = new JSONArray();
        JSONArray notJoined = (JSONArray) dataToJoin.getSecond().clone();
        for (Object o : data) {
            JSONObject record = (JSONObject) o;
            for (Object o2 : dataToJoin.getSecond()) {
                JSONObject record2 = (JSONObject) o2;
                if((new IsEqual()).compare(((JSONObject)(record.get("Data"))).get(referenceValue), record2.get(value))) {
                    ((JSONObject) record.get("Data")).put(referenceValue, new Pair(dataToJoin.getFirst(), record2));
                    join.add(record);
                    notJoined.remove(record2);
                    break;
                }
            }
        }
        return new Pair(join, notJoined);
    }
}

class FullJoinCommand extends JoinCommand{

    Pair<JSONArray, JSONArray> join(JSONArray data, Pair<String, JSONArray> dataToJoin, Object referenceValue, Object value) {
        JSONArray join = new JSONArray();
        JSONArray notJoined = (JSONArray) dataToJoin.getSecond().clone();
        for (Object o : data) {
            JSONObject record = (JSONObject) o;
            join.add(record);
            for (Object o2 : dataToJoin.getSecond()) {
                JSONObject record2 = (JSONObject) o2;
                if((new IsEqual()).compare(((JSONObject)(record.get("Data"))).get(referenceValue), record2.get(value))) {
                    ((JSONObject) ((JSONObject) join.getLast()).get("Data")).put(referenceValue, new Pair(dataToJoin.getFirst(), record2));
                    notJoined.remove(record2);
                    break;
                }
            }
        }
        return new Pair(join, notJoined);
    }
}

