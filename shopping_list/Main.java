import java.io.*;
import java.util.*;

class FileHandler {

  File file;
  @SuppressWarnings("rawtypes")
  HashMap<String, ArrayList> productList= new HashMap<>();

  FileHandler(String fileName){
    file = new File(fileName);
  }

  @SuppressWarnings({ "ConvertToTryWithResources", "unchecked", "rawtypes" })
  void readFile() throws Exception{
    int currentCategory;
    Scanner readedFile = new Scanner(file);
    String nextLine = readedFile.nextLine();
    String[] categories = nextLine.split(";");
    ArrayList<String> products = new ArrayList<>();
    for (String category : categories) productList.put(category, (ArrayList) products.clone());
    while(readedFile.hasNextLine()){
      currentCategory = 0;
      for (String product : readedFile.nextLine().split(";")){
        currentCategory++;
        if("".equals(product)) continue;
        productList.get(categories[currentCategory - 1]).add(product);
      }
    }
    readedFile.close();
  }

    @SuppressWarnings({"rawtypes"})
    HashMap<String, ArrayList> getShoppingList() throws Exception {
      readFile();
      return productList;
    }

    void saveShoppingList(){

    }
}


class UserAction {

  FileHandler fileToRead;
  @SuppressWarnings("rawtypes")
  HashMap<String, ArrayList> shopping_list;
  Set<String> keysSet;

    @SuppressWarnings({ })
    UserAction(String file) throws Exception {
      fileToRead = new FileHandler(file);
      shopping_list = fileToRead.getShoppingList();
      keysSet = shopping_list.keySet();
    }

    @SuppressWarnings("unchecked")
    boolean addProduct(String category, String product){
      if(keysSet.contains(category)){
        shopping_list.get(category).add(product);
        return true;
      }
      return false;
    }

    boolean removeProduct(String category, String product){
      if(keysSet.contains(category)){
        shopping_list.remove(category, product);
        return true;
      }
      return false;
    }

    @SuppressWarnings("rawtypes")
    void addCategory(String category){
      ArrayList<String> products = new ArrayList<> ();
      shopping_list.put(category, (ArrayList) products.clone());
    }

    boolean removeCategory(String category){
      if(keysSet.contains(category)){
        shopping_list.remove(category);
        return true;
      }
      return false;
    }
}


class UI {

  UserAction action;

  UI(String file) throws Exception{
    action = new UserAction(file);
  }

  void clear(){
    System.out.print("\033\143");
  }

  void displayMainMenu(){
    System.out.print("""
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

  String createShoppingList(){
    String shopping_list = "";
    for(String category : action.keysSet){
      shopping_list += (category + ":\n");
        for (Object product : action.shopping_list.get(category)) {
          shopping_list += ("\t" + product + "\n");
        }
    }
    return shopping_list;
  }

  void displayShoppingList(Scanner select){
    while(!"e".equals(select.nextLine())){
      clear();
      System.out.print(createShoppingList() + "\nPress \"e\" to go back to main menu\n");
    }
  }

  void displayAddProductProcess(){
    String category, product;
    category = "";
    product = "";
    action.addProduct(category, product);
  }

  void displayRemoveProductProcess(){
    String category, product;
    category = "";
    product = "";
    action.removeProduct(category, product);
  }

  void displayAddCategoryProcess(){
    String category;
    category = "";
    action.addCategory(category);
  }

  void displayRemoveCategoryProcess(){
    String category;
    category = "";
    action.removeCategory(category);
  }


  void displayInfo(Scanner select){
    while(!"e".equals(select.nextLine())){
      clear();
      System.out.print("""
        Program to generate txt shopping list from csv product list
        Pick options in menu to modify the list

        Press "e" to go back to main menu
        """);
    }
  }

  boolean manageMainMenu(Scanner select, UserAction action){
    String input = select.next();
    switch(input){
      case "1" -> displayShoppingList(select);
      case "2" -> action.fileToRead.saveShoppingList();
      case "3" -> displayAddProductProcess();
      case "4" -> displayRemoveProductProcess();
      case "5" -> displayAddCategoryProcess();
      case "6" -> displayRemoveCategoryProcess();
      case "7" -> displayInfo(select);
      case "8" -> {return false;}
    }
    return true;
  }

  void mainUI() throws Exception{
    Scanner select = new Scanner(System.in);
    boolean run = true;
    while(run) {
      clear();
      displayMainMenu();
      run = manageMainMenu(select, action);
    }
  }
}

public class Main {
  public static void main(String[] args) throws Exception {
    String file = "JAVA\\shopping_list\\shopping_list.csv";
    UI ui = new UI(file);
    ui.mainUI();
    ui.clear();
  }
}
