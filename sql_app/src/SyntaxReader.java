import java.util.*;

public class SyntaxReader {

    Scanner scanner;
    String input;
    HashSet startingCommands;


    SyntaxReader() {
        scanner = new Scanner(System.in);
        startingCommands = new HashSet<>();
        startingCommands.add("SELECT");
        startingCommands.add("INSERT");
        startingCommands.add("UPDATE");
        startingCommands.add("DELETE");
    }

    void readInput() {
        input = scanner.nextLine();
    }

    void analiseInput() throws Exception {
        readInput();
        ArrayList<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
        CommandHandler handler = switch (words.getFirst()){
            case "SELECT" -> new SelectHandler(words);
            case "UPDATE" -> null;
            case "DELETE" -> null;
            case "INSERT" -> null;
            default -> throw new NotACommandException(words.getFirst() + " is not a command");
        };
        System.out.print(handler.handleCommand());
    }
}

// SELECT students.ID, students.Name, teachers.Surname FROM students FULL JOIN teachers ON students.TeacherID=teachers.ID;