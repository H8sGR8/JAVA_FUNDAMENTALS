import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class SyntaxReader {

    Scanner scanner;
    String input;
    HashMap<String, JSONArray> databases;
    HashMap<String, Set<String>> keys;
    HashSet startingCommands;
    HashSet commandsAlternatingDatabases;
    HashSet joinOptions;

    SyntaxReader() {
        scanner = new Scanner(System.in);
        databases = new HashMap<>();
        keys = new HashMap<>();
        startingCommands = new HashSet<>();
        startingCommands.add("SELECT");
        startingCommands.add("INSERT");
        startingCommands.add("UPDATE");
        startingCommands.add("DELETE");
        commandsAlternatingDatabases = new HashSet<>();
        commandsAlternatingDatabases.add("WHERE");
        commandsAlternatingDatabases.add("JOIN");
        joinOptions = new HashSet<>();
        joinOptions.add("LEFT");
        joinOptions.add("RIGHT");
        joinOptions.add("FULL");
        joinOptions.add("INNER");
    }

    void readInput() {
        input = scanner.nextLine();
    }

    void printOutput(ArrayList<ArrayList<String>> output) {
        for (ArrayList<String> row : output) System.out.println(row);
    }

    void addDatabaseToDatabases(String database) throws Exception {
        new FromCommand().fromFunction(database, databases);
        keys.put(database, new HashSet<>());
        keys.get(database).add("ID");
        for(Object key :  Command.getColumns((JSONObject) databases.get(database).getFirst())) keys.get(database).add(key.toString());
    }

    Pair<String, String> findCorrespondingTable(String item) throws Exception {
        if(item.contains("."))  return new Pair<>(item.split("\\.")[0], item.split("\\.")[1]);
        ArrayList<String> found = new ArrayList<>();
        for (String key : keys.keySet()) if(keys.get(key).contains(item)) found.add(key);
        if(found.isEmpty()) throw new NoSuchDatabaseException(item + " could not be found in any database");
        if(found.size() > 1) throw new ItemWithNoSpecifierInManyDatabasesException(item + " could not be found in those databases" + found);
        return new Pair<>(found.getFirst(), item);
    }

    ArrayList<Pair<String, String>> createSelectedItemsForSelectCommand(ArrayList<String> selectedItems) throws Exception  {
        ArrayList<Pair<String, String>> selectedItemsForSelectCommand = new ArrayList<>();
        if(selectedItems.isEmpty()) throw new EmptySelectException("No items selected");
        if(selectedItems.size() == 1 && selectedItems.getFirst().equals("*")) {
            for(String key: keys.keySet()) for(String item: keys.get(key)) selectedItemsForSelectCommand.add(new Pair<>(key, item));
            return selectedItemsForSelectCommand;
        }
        for (String item : selectedItems) selectedItemsForSelectCommand.add(findCorrespondingTable(item));
        return selectedItemsForSelectCommand;
    }
    
    Pair<String, String> getElementsToJoinOn(int index, ArrayList<String> instructionElements) throws Exception {
        return findCorrespondingTable(instructionElements.get(instructionElements.indexOf("ON") + 1).split("=")[index]);
    }
    
    int handleJoin(int i, String instructionsForDatabases, String joinType, String word) throws Exception {
        String joinInstruction;
        int startOfCommand = i;
        i = instructionsForDatabases.indexOf("=", i);
        while(instructionsForDatabases.charAt(i) != ' ' && instructionsForDatabases.charAt(i) != ';' && instructionsForDatabases.charAt(i) != ')') i++;
        joinInstruction = instructionsForDatabases.substring(startOfCommand + 1, i);
        ArrayList<String> instructionElements = new ArrayList<>(Arrays.asList(joinInstruction.split(" ")));
        String joinedDatabase = instructionElements.getFirst();
        addDatabaseToDatabases(joinedDatabase);
        JoinCommand joinCommand = switch (joinType) {
            case "LEFT" -> new LeftJoinCommand();
            case "RIGHT" -> new RightJoinCommand();
            case "FULL" -> new FullJoinCommand();
            case "INNER" -> new InnerJoinCommand();
            default -> throw new NotACommandException(word + " is not a valid join command");
        };
        databases = joinCommand.join(databases, new Pair(joinedDatabase, databases.get(joinedDatabase)),
                getElementsToJoinOn(0, instructionElements), getElementsToJoinOn(1, instructionElements).getSecond());
        return i;
    }

    int handleCommandsAlternatingDatabases(int i, String instructionsForDatabases, String joinType, String word, int end, int brackets) throws Exception {
        if(commandsAlternatingDatabases.contains(word) || joinOptions.contains(word)){
            if(joinOptions.contains(word)) joinType = word;
            if(word.equals("JOIN")){
                i = handleJoin(i, instructionsForDatabases, joinType, word);
                return analiseInstructionForDatabase(instructionsForDatabases, i + 1, end, brackets);
            }
            if(word.equals("WHERE")){
                return analiseInstructionForDatabase(instructionsForDatabases, i + 1, end, brackets);
            }
        }
        return i;
    }

    int analiseInstructionForDatabase(String instructionsForDatabases, int start, int end, int brackets) throws Exception {
        int bracketsCount = brackets;
        int startingPoint = 0;
        String joinType = "";
        String word = "";
        for(int i = start; i < end; i++) {
            if(instructionsForDatabases.charAt(i) == '('){
                bracketsCount++;
                startingPoint = i + 1;
            }
            else if(instructionsForDatabases.charAt(i) == ')' && --bracketsCount == 0)
                i = analiseInstructionForDatabase(instructionsForDatabases, startingPoint, i - 1, brackets);
            if(instructionsForDatabases.charAt(i) == ';' && bracketsCount != 0)
                throw new NotEveryBracketClosedOROpenedException((bracketsCount < 0)? "Not every bracket opened" : "Not every bracket closed");
            if(instructionsForDatabases.charAt(i) == ' ' || instructionsForDatabases.charAt(i) == ';') {
                if (!commandsAlternatingDatabases.contains(word) && !joinOptions.contains(word)) addDatabaseToDatabases(word);
                if (instructionsForDatabases.charAt(i) == ';') return i;
                i = handleCommandsAlternatingDatabases(i, instructionsForDatabases, joinType, word, end, brackets);
                word = "";
            }
            else word += instructionsForDatabases.charAt(i);
        }
        return end + 1;
    }

    void analiseInput() throws Exception {
        readInput();
        ArrayList<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
        if(!startingCommands.contains(words.getFirst())) throw new NotACommandException(words.getFirst() + " is not a command");
        if (words.getFirst().equals("SELECT")){
            ArrayList<String> selectedItems = new ArrayList<>();
            if (!words.contains("FROM")) throw new NoFromCommandException("Could not find FROM");
            int index;
            for (index = 1; index < words.indexOf("FROM"); index++) selectedItems.add(words.get(index).replace(",", ""));
            StringBuilder instructionsForDatabases = new StringBuilder();
            while (!words.get(++index).endsWith(";")) instructionsForDatabases.append(words.get(index)).append(" ");
            instructionsForDatabases.append(words.get(index));
            analiseInstructionForDatabase(instructionsForDatabases.toString(), 0, instructionsForDatabases.length(), 0);
            System.out.println(databases);
            printOutput(new SelectCommand().select(databases, createSelectedItemsForSelectCommand(selectedItems)));
        }
    }
}

// SELECT students.ID, students.Name, teachers.Surname FROM students FULL JOIN teachers ON students.TeacherID=teachers.ID;