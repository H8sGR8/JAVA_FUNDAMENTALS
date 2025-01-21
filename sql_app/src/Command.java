import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

abstract class Command {

     public static Set getColumns(JSONObject record){
        return ((JSONObject) record.get("Data")).keySet();
    }
}

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
        if(getValueForDataKey(joinedTables, key) == null) return null;
        if(!(getValueForDataKey(joinedTables, key) instanceof Pair)) return getValueForDataKey(joinedTables, key).toString();
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

    boolean selectRecords(Comparator comparator, Pair<String, JSONObject> record, Pair<String, Object> item, Object value) {
        if(!record.getFirst().equals(item.getFirst())) {
            if (item.getSecond() == "ID") return comparator.compare(record.getSecond().get("ID"), value);
            return comparator.compare(((JSONObject) record.getSecond().get("Data")).get(item), value);
        }
        else for(Object v : ((JSONObject) record.getSecond().get("Data")).values()) if(v instanceof Pair)
            return selectRecords(comparator, (Pair) v, item, value);
        return false;
    }

    HashMap<String, JSONArray> where(HashMap<String, JSONArray> databases, Comparator comparator, Pair<String, Object> item, Object value) {
        HashMap<String, JSONArray> where = new HashMap<>();
        for(String database: databases.keySet()) {
            JSONArray whereArray = new JSONArray();
            for (Object o : databases.get(database)) {
                JSONObject record = (JSONObject) o;
                if (selectRecords(comparator, new Pair<>(database, record), item, value)) whereArray.add(record);
            }
            where.put(database, whereArray);
        }
        return where;
    }
}

abstract class JoinCommand extends Command{

    Pair<JSONObject, JSONObject> selectRecords(Pair<String, JSONObject> record, Pair<String, JSONArray> dataToJoin,  Pair<String, String> referenceValue, String value){
        if(record.getFirst().equals(referenceValue.getFirst())) for (Object o2 : dataToJoin.getSecond()) {
            JSONObject record2 = (JSONObject) o2;
            if ((new IsEqual()).compare(((JSONObject) (record.getSecond().get("Data"))).get(referenceValue.getSecond()), record2.get(value))) {
                ((JSONObject) record.getSecond().get("Data")).put(referenceValue.getSecond(), new Pair(dataToJoin.getFirst(), record2));
                return new Pair(record.getSecond(), record2);
            }
        }
        else for (Object v : ((JSONObject)record.getSecond().get("Data")).values())
            if(v instanceof Pair) return selectRecords((Pair)v, dataToJoin, referenceValue, value);
        return null;
    }

    abstract HashMap<String, JSONArray> join(HashMap<String, JSONArray> databases, Pair<String, JSONArray> dataToJoin,  Pair<String, String> referenceValue, String value);
}

class InnerJoinCommand extends JoinCommand{

    HashMap<String, JSONArray> join(HashMap<String, JSONArray> databases, Pair<String, JSONArray> dataToJoin,  Pair<String, String> referenceValue, String value) {
        HashMap<String, JSONArray> join = new HashMap<>();
        for (String database : databases.keySet()) {
            if(database.equals(dataToJoin.getFirst())) continue;
            JSONArray joinArray = new JSONArray();
            for (Object o : databases.get(database)) {
                JSONObject record = (JSONObject) o;
                Pair<JSONObject, JSONObject> result = selectRecords(new Pair(database, record), dataToJoin, referenceValue, value);
                if(result == null) continue;
                joinArray.add(result.getFirst());
            }
            join.put(database, joinArray);
        }
        return join;
    }
}

class LeftJoinCommand extends JoinCommand{

    HashMap<String, JSONArray> join(HashMap<String, JSONArray> databases, Pair<String, JSONArray> dataToJoin,  Pair<String, String> referenceValue, String value) {
        HashMap<String, JSONArray> join = new HashMap<>();
        for (String database : databases.keySet()) {
            if(database.equals(dataToJoin.getFirst())) continue;
            JSONArray joinArray = new JSONArray();
            for (Object o : databases.get(database)) {
                JSONObject record = (JSONObject) o;
                Pair<JSONObject, JSONObject> result = selectRecords(new Pair(database, record), dataToJoin, referenceValue, value);
                if(result == null) joinArray.add(record);
                else joinArray.add(result.getFirst());
            }
            join.put(database, joinArray);
        }
        return join;
    }
}

class RightJoinCommand extends JoinCommand{

    HashMap<String, JSONArray> join(HashMap<String, JSONArray> databases, Pair<String, JSONArray> dataToJoin,  Pair<String, String> referenceValue, String value) {
        HashMap<String, JSONArray> join = new HashMap<>();
        join.put(dataToJoin.getFirst(), (JSONArray) dataToJoin.getSecond().clone());
        for (String database : databases.keySet()) {
            if(database.equals(dataToJoin.getFirst())) continue;
            JSONArray joinArray = new JSONArray();
            for (Object o : databases.get(database)) {
                JSONObject record = (JSONObject) o;
                Pair<JSONObject, JSONObject> result = selectRecords(new Pair(database, record), dataToJoin, referenceValue, value);
                if(result == null) continue;
                joinArray.add(result.getFirst());
                join.get(dataToJoin.getFirst()).remove(result.getSecond());
            }
            join.put(database, joinArray);
        }
        return join;
    }
}

class FullJoinCommand extends JoinCommand{

    HashMap<String, JSONArray> join(HashMap<String, JSONArray> databases, Pair<String, JSONArray> dataToJoin,  Pair<String, String> referenceValue, String value) {
        HashMap<String, JSONArray> join = new HashMap<>();
        join.put(dataToJoin.getFirst(), (JSONArray) dataToJoin.getSecond().clone());
        for (String database : databases.keySet()) {
            if(database.equals(dataToJoin.getFirst())) continue;
            JSONArray joinArray = new JSONArray();
            for (Object o : databases.get(database)) {
                JSONObject record = (JSONObject) o;
                Pair<JSONObject, JSONObject> result = selectRecords(new Pair(database, record), dataToJoin, referenceValue, value);
                if(result == null) joinArray.add(record);
                else{
                    joinArray.add(result.getFirst());
                    join.get(dataToJoin.getFirst()).remove(result.getSecond());
                }
            }
            join.put(database, joinArray);
        }
        return join;
    }
}

