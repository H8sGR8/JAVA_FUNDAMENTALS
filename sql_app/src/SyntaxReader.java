import java.util.*;

public class SyntaxReader {

    Scanner scanner;
    String input;
    HashSet<String> keywords;


    SyntaxReader() {
        scanner = new Scanner(System.in);
        keywords = new HashSet();
        keywords.add("select");
        keywords.add("insert");
        keywords.add("delete");
        keywords.add("update");
        keywords.add("from");
        keywords.add("where");
        keywords.add("values");
        keywords.add("set");
        keywords.add("into");
        keywords.add("left");
        keywords.add("right");
        keywords.add("inner");
        keywords.add("full");
        keywords.add("join");
        keywords.add("on");
    }

    void readInput() {
        input = scanner.nextLine();
    }
    
    void replaceLowerCaseToUpperCase(ArrayList<String> words) {
        for (String key : keywords) while (words.contains(key)) words.set(words.indexOf(key), key.toUpperCase());
    }

    void analiseInput() throws Exception {
        readInput();
        if (!input.endsWith(";")) throw new Exception("Missing ending semicolon");
        ArrayList<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
        replaceLowerCaseToUpperCase(words);
        CommandSyntax syntax = switch (words.getFirst()){
            case "SELECT" -> new SelecCommandSyntax(words);
            case "UPDATE" -> new UpdateCommandSyntax(words);
            case "DELETE" -> new DeleteCommandSyntax(words);
            case "INSERT" -> new InsertCommandSyntax(words);
            default -> throw new Exception(words.getFirst() + " is not a command");
        };
        System.out.print(syntax.executeCommand());
    }
}

// SELECT students.ID, students.Name, students.Surname, Profile, teachers.Surname, Subject FROM students FULL JOIN teachers ON TeacherID=teachers.ID WHERE Profile = MatPhys;