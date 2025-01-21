import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

abstract class CommandSyntax{
    HashSet commandsAlternatingDatabases;
    HashSet joinOptions;
    ArrayList<String> words;
    HashMap<String, JSONArray> databases;
    HashMap<String, Set<String>> keys;

    CommandSyntax(ArrayList<String> words){
        commandsAlternatingDatabases = new HashSet<>();
        commandsAlternatingDatabases.add("WHERE");
        joinOptions = new HashSet<>();
        this.words = words;
        databases = new HashMap<>();
        keys = new HashMap<>();
    }

    void addDatabaseToDatabases(String database) throws Exception {
        new FromCommand().fromFunction(database, databases);
        keys.put(database, new HashSet<>());
        keys.get(database).add("ID");
        for(Object key :  Command.getColumns((JSONObject) databases.get(database).getFirst())) keys.get(database).add(key.toString());
    }

    Pair<String, String> findCorrespondingTable(String item) throws Exception {
        if(item.contains(".")) {
            if(keys.containsKey(item.split("\\.")[0])) return new Pair<>(item.split("\\.")[0], item.split("\\.")[1]);
            throw new Exception(item.split("\\.")[0] + " is not a valid database name");
        }
        ArrayList<String> found = new ArrayList<>();
        for (String key : keys.keySet()) if(keys.get(key).contains(item)) found.add(key);
        if(found.isEmpty()) throw new Exception(item + " could not be found in any database");
        if(found.size() > 1) throw new Exception(item + " can be found in those databases " + found);
        return new Pair<>(found.getFirst(), item);
    }

    int executeWhere(int i, String instructionsForDatabases) throws Exception {
        String whereInstruction = instructionsForDatabases.substring(i);
        whereInstruction = whereInstruction.replace(";", "");
        ArrayList<String> whereInstructionElements = new ArrayList<>(Arrays.asList(whereInstruction.split(" ")));
        Comparator comparator = switch (whereInstructionElements.get(1)){
            case "=" -> new IsEqual();
            case "!=" -> new IsNotEqual();
            case ">" -> new IsGreater();
            case "<" -> new IsLess();
            case ">=" -> new IsGreaterOrEqual();
            case "<=" -> new IsLessOrEqual();
            default -> throw new Exception(whereInstructionElements.get(1) + " is not a comparator");
        };
        databases = new WhereCommand().where(databases, comparator,
                findCorrespondingTable(whereInstructionElements.getFirst()), whereInstructionElements.getLast().replace("\"", ""));
        return instructionsForDatabases.indexOf(';');
    }

    abstract int executeCommandsAlternatingDatabases(int i, String instructionsForDatabases, String word, int end, int brackets) throws Exception;

    int analiseInstructionForDatabase(String instructionsForDatabases, int start, int end, int brackets) throws Exception {
        int bracketsCount = brackets;
        int startingPoint = 0;

        String word = "";
        for(int i = start; i < end; i++) {
            if(instructionsForDatabases.charAt(i) == '('){
                bracketsCount++;
                startingPoint = i + 1;
            }
            else if(instructionsForDatabases.charAt(i) == ')' && --bracketsCount == 0)
                i = analiseInstructionForDatabase(instructionsForDatabases, startingPoint, i - 1, brackets);
            if(instructionsForDatabases.charAt(i) == ';' && bracketsCount != 0)
                throw new Exception((bracketsCount < 0)? "Not every bracket opened" : "Not every bracket closed");
            if(instructionsForDatabases.charAt(i) == ' ' || instructionsForDatabases.charAt(i) == ';') {
                if (!commandsAlternatingDatabases.contains(word) && !joinOptions.contains(word)) addDatabaseToDatabases(word);
                if (instructionsForDatabases.charAt(i) == ';') return i;
                i = executeCommandsAlternatingDatabases(i, instructionsForDatabases, word, end, brackets);
                word = "";
            }
            else word += instructionsForDatabases.charAt(i);
        }
        return end + 1;
    }

    void getAndAnaliseInstruction(int index) throws Exception {
        StringBuilder instructionsForDatabases = new StringBuilder();
        while (!words.get(++index).endsWith(";")) instructionsForDatabases.append(words.get(index)).append(" ");
        instructionsForDatabases.append(words.get(index));
        analiseInstructionForDatabase(instructionsForDatabases.toString(), 0, instructionsForDatabases.length(), 0);
    }

    String createOutputString(int data){
        return data + " rows affected";
    }

    abstract String executeCommand() throws Exception;
}

class SelecCommandSyntax extends CommandSyntax{
    String joinType;

    SelecCommandSyntax(ArrayList<String> words){
        super(words);
        commandsAlternatingDatabases.add("JOIN");
        joinOptions.add("LEFT");
        joinOptions.add("RIGHT");
        joinOptions.add("FULL");
        joinOptions.add("INNER");
        joinType = "";
    }

    ArrayList<Pair<String, String>> createSelectedItemsForSelectCommand(ArrayList<String> selectedItems) throws Exception  {
        ArrayList<Pair<String, String>> selectedItemsForSelectCommand = new ArrayList<>();
        if(selectedItems.isEmpty()) throw new Exception("No items selected");
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

    int executeJoin(int i, String instructionsForDatabases, String word) throws Exception {
        String joinInstruction;
        int startOfCommand = i;
        i = instructionsForDatabases.indexOf("=", i);
        while(instructionsForDatabases.charAt(i) != ' ' && instructionsForDatabases.charAt(i) != ';' && instructionsForDatabases.charAt(i) != ')') i++;
        joinInstruction = instructionsForDatabases.substring(startOfCommand, i);
        ArrayList<String> instructionElements = new ArrayList<>(Arrays.asList(joinInstruction.split(" ")));
        String joinedDatabase = instructionElements.getFirst();
        addDatabaseToDatabases(joinedDatabase);
        JoinCommand joinCommand = switch (joinType) {
            case "LEFT" -> new LeftJoinCommand();
            case "RIGHT" -> new RightJoinCommand();
            case "FULL" -> new FullJoinCommand();
            case "INNER" -> new InnerJoinCommand();
            default -> throw new Exception(word + " is not a valid join command");
        };
        databases = joinCommand.join(databases, new Pair(joinedDatabase, databases.get(joinedDatabase)),
                getElementsToJoinOn(0, instructionElements), getElementsToJoinOn(1, instructionElements).getSecond());
        return i;
    }

    int executeCommandsAlternatingDatabases(int i, String instructionsForDatabases, String word, int end, int brackets) throws Exception {
        if(commandsAlternatingDatabases.contains(word) || joinOptions.contains(word)){
            if(joinOptions.contains(word)) joinType = word;
            if(word.equals("JOIN")){
                i = executeJoin(i + 1, instructionsForDatabases, word);
                return analiseInstructionForDatabase(instructionsForDatabases, i + 1, end, brackets);
            }
            if(word.equals("WHERE")){
                i = executeWhere(i + 1, instructionsForDatabases);
                return analiseInstructionForDatabase(instructionsForDatabases, i + 1, end, brackets);
            }
        }
        return i;
    }

    String createOutputString(ArrayList<ArrayList<String>> data){
        String output = "";
        for(ArrayList<String> item : data) output += item.toString() + "\n";
        return output;
    }

    String executeCommand() throws Exception {
        ArrayList<String> selectedItems = new ArrayList<>();
        if (!words.contains("FROM")) throw new Exception("Could not find FROM");
        int index;
        for (index = 1; index < words.indexOf("FROM"); index++) selectedItems.add(words.get(index).replace(",", ""));
        getAndAnaliseInstruction(index);
        return createOutputString(new SelectCommand().select(databases, createSelectedItemsForSelectCommand(selectedItems)));
    }
}

class InsertCommandSyntax extends CommandSyntax {

    InsertCommandSyntax(ArrayList<String> words) {
        super(words);
    }

    int executeCommandsAlternatingDatabases(int i, String instructionsForDatabases, String word, int end, int brackets){
        return 0;
    }

    String executeCommand() {
        return "insert";
    }
}

class DeleteCommandSyntax extends CommandSyntax {

    DeleteCommandSyntax(ArrayList<String> words) {
        super(words);
    }

    int executeCommandsAlternatingDatabases(int i, String instructionsForDatabases, String word, int end, int brackets) throws Exception {
        if(word.equals("WHERE")){
            i = executeWhere(i + 1, instructionsForDatabases);
            return analiseInstructionForDatabase(instructionsForDatabases, i + 1, end, brackets);
        }
        return i;
    }

    String executeCommand() throws Exception {
        if (!words.contains("FROM")) throw new Exception("Could not find FROM");
        int index = words.indexOf("FROM");
        getAndAnaliseInstruction(index);
        return createOutputString(
                new DeleteCommand().delete(words.get(words.indexOf("FROM") + 1).replace(";", ""),
                        databases.get(words.get(words.indexOf("FROM") + 1).replace(";", ""))));
    }
}

class UpdateCommandSyntax extends CommandSyntax {

    ArrayList<Pair<String, String>> columnsToUpdate;

    UpdateCommandSyntax(ArrayList<String> words) {
        super(words);
        columnsToUpdate = new ArrayList<>();
    }

    int executeCommandsAlternatingDatabases(int i, String instructionsForDatabases, String word, int end, int brackets) throws Exception {
        if(word.equals("WHERE")){
            i = executeWhere(i + 1, instructionsForDatabases);
            return analiseInstructionForDatabase(instructionsForDatabases, i + 1, end, brackets);
        }
        return i;
    }

    int getColumnsUpdates(int index){
        int endIndex;
        if(words.contains("WHERE")) endIndex = words.indexOf("WHERE") - 1;
        else endIndex = words.indexOf(words.getLast());
        StringBuilder columnsWithItems = new StringBuilder();
        for (; index <= endIndex; index++) columnsWithItems.append(words.get(index));
        String[] columns = columnsWithItems.toString().split(",");
        for (String column : columns) columnsToUpdate.add(new Pair<>(column.split("=")[0], column.split("=")[1]));
        return index;
    }

    String executeCommand() throws Exception {
        addDatabaseToDatabases(words.get(words.indexOf("UPDATE") + 1));
        if (!words.contains("SET")) throw new Exception("Could not find SET");
        int index = words.indexOf("SET") + 1;
        getAndAnaliseInstruction(getColumnsUpdates(index) - 1);
        return createOutputString(new UpdateCommand().update(words.get(words.indexOf("UPDATE") + 1),
                databases.get(words.get(words.indexOf("UPDATE") + 1)), columnsToUpdate));
    }
}
