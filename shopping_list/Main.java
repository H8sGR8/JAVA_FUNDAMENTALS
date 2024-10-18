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

  @SuppressWarnings("ConvertToTryWithResources")
    void saveShoppingList(String shoping_list) throws IOException{
      FileWriter fileToWrite = new FileWriter("JAVA\\shopping_list\\shopping_list.txt");
      fileToWrite.write(shoping_list);
      fileToWrite.close();
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
      if(shopping_list.get(category).contains(product)){
        shopping_list.get(category).remove(product);
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
  Scanner select;

  UI(String file) throws Exception{
    action = new UserAction(file);
    select = new Scanner(System.in);
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

  void displayShoppingList(){
    do{
      clear();
      System.out.print(createShoppingList() + "\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displaySaveShoppingListProcess() throws IOException{
    action.fileToRead.saveShoppingList(createShoppingList());
    do{
      clear();
      System.out.print("A shopping list saved in txt file\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayAddProductProcess(){
    String category, product;
    String outputMessage = "Could not add a product";
    clear();
    System.out.println("Choose a category by inserting its name\nCetegories: " + action.keysSet);
    category = select.nextLine();
    clear();
    System.out.println("Insert a name of a product to add");
    product = select.nextLine();
    if (action.addProduct(category, product)) outputMessage = "A product successfully added";
    do{
      clear();
      System.out.print(outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayRemoveProductProcess(){
    String category, product;
    String outputMessage = "Could not remove a product";
    clear();
    System.out.println("Choose a category by inserting its name\nCetegories: " + action.keysSet);
    category = select.nextLine();
    if(action.keysSet.contains(category)){
      clear();
      System.out.println("Insert a name of a product to remove\nProducts: " + action.shopping_list.get(category));
      product = select.nextLine();
      if (action.removeProduct(category, product)) outputMessage = "A product successfully removed";
    }
    do{
      clear();
      System.out.print(outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayAddCategoryProcess(){
    String category;
    clear();
    System.out.println("Insert a name of a category to add");
    category = select.nextLine();
    action.addCategory(category);
    do{
      clear();
      System.out.print( "A category successfully added\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayRemoveCategoryProcess(){
    String category;
    String outputMessage = "Could not remove a category";
    clear();
    System.out.println("Insert a name of a category to remove\nCategories: " + action.keysSet);
    category = select.nextLine();
    if (action.removeCategory(category)) outputMessage = "A category successfully removed";
    do{
      clear();
      System.out.print(outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayInfo(){
    do{
      clear();
      System.out.print("""
        Program to generate txt shopping list from csv product list
        Pick option by pressing assigned number in menu to modify the list

        Press "q" to go back to main menu
        """);
    }while(!"q".equals(select.nextLine()));
  }

  boolean manageMainMenu(UserAction action) throws IOException{
    switch(select.nextLine()){
      case "1" -> displayShoppingList();
      case "2" -> displaySaveShoppingListProcess();
      case "3" -> displayAddProductProcess();
      case "4" -> displayRemoveProductProcess();
      case "5" -> displayAddCategoryProcess();
      case "6" -> displayRemoveCategoryProcess();
      case "7" -> displayInfo();
      case "8" -> {return false;}
    }
    return true;
  }

  void mainUI() throws Exception{
    boolean run = true;
    while(run) {
      clear();
      displayMainMenu();
      run = manageMainMenu(action);
    }
  }
}


public class Main {
  public static void main(String[] args) throws Exception {
    String file = "JAVA\\shopping_list\\product_list.csv";
    UI ui = new UI(file);
    ui.mainUI();
    ui.clear();
  }
}
