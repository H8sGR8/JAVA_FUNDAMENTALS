import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class SyntaxReader {

    Scanner scanner;
    String input;
    HashMap<String, JSONArray> databases;

    SyntaxReader() {
        scanner = new Scanner(System.in);
        databases = new HashMap<>();
    }

    void readInput() {
        input = scanner.nextLine();
    }

    void printOutput(ArrayList<ArrayList<String>> output) {
        for (ArrayList<String> row : output) System.out.println(row);
    }

    void analiseInput() throws Exception {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
        if (words.getFirst().equals("SELECT")){
            ArrayList<Pair<String, String>> selectedItems = new ArrayList<>();
            for (int i = 1; i < words.indexOf("FROM"); i++) {
                selectedItems.add(new Pair(words.get(i).replace(",", "").split("\\.")[0], words.get(i).replace(",", "").split("\\.")[1]));
            }
            String database = words.get(words.indexOf("FROM") + 1);
            new FromCommand().fromFunction(database, databases);
            printOutput(new SelectCommand().select(databases, selectedItems));
        }
    }
}

// SELECT students.ID, students.Name, students.Surname FROM students