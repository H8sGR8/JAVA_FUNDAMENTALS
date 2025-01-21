import java.util.*;

public class SyntaxReader {

    Scanner scanner;
    String input;


    SyntaxReader() {
        scanner = new Scanner(System.in);
    }

    void readInput() {
        input = scanner.nextLine();
    }

    void analiseInput() throws Exception {
        readInput();
        if (!input.endsWith(";")) throw new Exception("Missing ending semicolon");
        ArrayList<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
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

// SELECT students.ID, students.Name, teachers.Surname FROM students FULL JOIN teachers ON students.TeacherID=teachers.ID WHERE students.ID >= 2;