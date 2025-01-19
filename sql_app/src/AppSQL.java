import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;


public class AppSQL {

    public HashMap<String, JSONArray> databases;

    AppSQL(){
        databases = new HashMap<>();
    }

    void testFrom() throws Exception {
        SQL.fromFunction("students", databases);
        SQL.fromFunction("teachers", databases);
    }

    void testSelect(){
        ArrayList<Pair<String, String>> selectedItems = new ArrayList<>();
        selectedItems.add(new Pair<>("students", "ID"));
        selectedItems.add(new Pair<>("teachers", "Name"));
        selectedItems.add(new Pair<>("students", "Surname"));
        Comparator comparator = new IsNotEqual();
        Pair<String, JSONArray> y = new Pair<>("students", SQL.joinFunction(databases.get("students"), new Pair("teachers", databases.get("teachers")), "TeacherID", "ID"));
        System.out.println(SQL.selectFunction(y, selectedItems));
    }

    void testInsert() {
        ArrayList<String> selectedItems = new ArrayList<>();
        selectedItems.add("Name");
        selectedItems.add("Surname");
        selectedItems.add("Profile");
        ArrayList<ArrayList<String>> objects = new ArrayList<>();
        ArrayList<String> object = new ArrayList<>();
        object.add("Anna");
        object.add("Hasiura");
        object.add("MatPhys");
        objects.add(object);
        SQL.insertFunction(databases.get("students"), selectedItems, objects);
    }

    void testDelete() {
        JSONArray data = new JSONArray();
        data.add(databases.get("students").get(0));
        data.add(databases.get("students").get(2));
        SQL.deleteFunction(databases.get("students"), data);
    }

    void testUpdate() {
        SQL.updateFunction(SQL.whereFunction(databases.get("students"), new IsEqual(), "Name", "Anna"), "Name", "Ania");
    }

    public static void main(String[] args) throws Exception {
        AppSQL app = new AppSQL();
        app.testFrom();
        app.testInsert();
        //app.testDelete();
        app.testUpdate();
        app.testSelect();

    }
}