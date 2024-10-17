import java.io.*;
import java.util.*;

class FileHendler {

  File file;
  @SuppressWarnings("rawtypes")
  HashMap<String, ArrayList> shoppingList= new HashMap<>();

  FileHendler(String fileName){
    file = new File(fileName);
  }

  @SuppressWarnings({ "ConvertToTryWithResources", "unchecked", "rawtypes" })
  void readFile() throws Exception{
    int currentCategory;
    Scanner readedFile = new Scanner(file);
    String nextLine = readedFile.nextLine();
    String[] categories = nextLine.split(";");
    ArrayList<String> products = new ArrayList<>();
    for (String category : categories) shoppingList.put(category, (ArrayList) products.clone());
    while(readedFile.hasNextLine()){
      currentCategory = 0;
      for (String product : readedFile.nextLine().split(";")){
        if("".equals(product)) continue;
        shoppingList.get(categories[currentCategory]).add(product);
        currentCategory++;
      }
    }
    readedFile.close();
  }

    @SuppressWarnings({"rawtypes"})
    HashMap<String, ArrayList> getShoppingList() throws Exception {
      readFile();
      return shoppingList;
    }
}


class UserAction {

  FileHendler fileToRead;
  @SuppressWarnings("rawtypes")
  HashMap<String, ArrayList> shopping_list;
  Set<String> keysSet;

    @SuppressWarnings({ })
    UserAction(String file) throws Exception {
      fileToRead = new FileHendler(file);
      shopping_list = fileToRead.getShoppingList();
      keysSet = shopping_list.keySet();
    }

    void addProduct(String caregory, String product){

    }

    void removeProduct(String caregory, String product){

    }

    void addCategory(String caregory){

    }

    void removeCategory(String caregory){

    }

}


class UI {

  UserAction action;

  UI(String file) throws Exception{
    action = new UserAction(file);
  }

  void displayMainMenu(){
    System.out.print
    ("""
     1. Display shopping list
     2. Save shopping list
     3. Add element to category
     4. Delete element from category
     5. Add category
     6. Delete category
     7. Info
     8. Exit
     """);
  }

  boolean manageMainMenu(Scanner menuSelect, UserAction action){
    String input = menuSelect.nextLine();
    switch(input){
      case "1" -> {}
      case "2" -> {}
      case "3" -> {}
      case "4" -> {}
      case "5" -> {}
      case "6" -> {}
      case "7" -> {}
      case "8" -> {return false;}
    }
    return true;
  }

  void mainUI() throws Exception{
    Scanner menuSelect = new Scanner(System.in);
    boolean run = true;
    while(run) {
      System.out.print("\033\143");
      displayMainMenu();
      run = manageMainMenu(menuSelect, action);
    }
  }
}

public class Main {
  public static void main(String[] args) throws Exception {
    String file = "shopping_list\\shopping_list.csv";
    UI ui = new UI(file);
    ui.mainUI();
  }
}
