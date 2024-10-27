import java.io.*;
import java.util.*;


class Product {

  String productName;
  
  Product(String name){
    this.productName = name;
  }

  String getProductName(){
    return productName;
  }

}

class Category {

  String categoryName;
  ArrayList<Product> products = new ArrayList<>();

  Category(String name) {
      this.categoryName = name;
  }

  String getCategoryName(){
    return categoryName;
  }

  String getProductsNames(){
    String productsNames = "|| ";
    for (Product product : products) productsNames += (product.getProductName() + " || ");
    return productsNames;
  }

  Product checkIfProductExist(String productName){
    for (Product product : products) if (product.getProductName().equals(productName)) return product;
    return null;
  }

  boolean addProductToCategory(String productName){
    if (checkIfProductExist(productName) != null) return false;
    Product product = new Product(productName);
    this.products.add(product);
    return true;
  }

  boolean removeProductFromCategory(String producrName){
    Product product;
    if ((product = checkIfProductExist(producrName)) == null) return false;
    this.products.remove(product);
    return true;
  }
}


public class ShoppingList {

  ArrayList<Category> categories = new ArrayList<>();

  Category checkIfCategoryExist(String categoryName) {
    for (Category category : categories) if(category.getCategoryName().equals(categoryName)) return category;
    return null;
  }

  String getCategoriesNames(){
    String categoriesNames = "|| ";
    for (Category category : categories) categoriesNames += (category.getCategoryName() + " || ");
    return categoriesNames;
  }

  boolean addCategory(String categoryName){
    if (checkIfCategoryExist(categoryName) != null) return false;
    Category category = new Category(categoryName);
    categories.add(category);
    return true;
  }

  boolean removeCategory(String categoryName){
    Category category;
    if((category = checkIfCategoryExist(categoryName)) == null) return false;
    categories.remove(category);
    return true;
  }

  String converToString(){
    String shopping_list = "";
    for (Category category : categories){
      shopping_list += (category.getCategoryName() + "\n");
      for (Product product : category.products) shopping_list += ("\t" + product.getProductName() + "\n");
    }
    return shopping_list;
  }
}


class ErrorHandler {

  static boolean couldNotWriteToFile(){
    return false;
  }

  static void couldNotOpenProductFile(){
    UI.clear();
    System.out.print("Could not open a product file");
    System.exit(1);
  }
}


class FileHandler {

  File pruductList;
  ShoppingList shoppingList = new ShoppingList();
  final String filePath = "JAVA\\shopping_list\\product_list.csv";
  final String savePath = "JAVA\\shopping_list\\shopping_list.txt";

  FileHandler(){
    pruductList = new File(filePath);
  }


  void readFile(){
    int currentCategory;
    Scanner readedFile;
    try {
      readedFile = new Scanner(pruductList);
    } catch (FileNotFoundException exception) {
      ErrorHandler.couldNotOpenProductFile();
      return;
    }
    String nextLine = readedFile.nextLine();
    String[] categoryNames = nextLine.split(";");
    for (String categoryName : categoryNames) shoppingList.addCategory(categoryName);
    while(readedFile.hasNextLine()){
      currentCategory = 0;
      for (String productName : readedFile.nextLine().split(";")){
        currentCategory++;
        if("".equals(productName)) continue;
        shoppingList.categories.get(currentCategory - 1).addProductToCategory(productName);
      }
    }
    readedFile.close();
  }

  ShoppingList getShoppingList(){
    readFile();
    return shoppingList;
  }

  boolean saveShoppingList(String shoppingList){
    FileWriter fileToWrite;
    try {
      fileToWrite = new FileWriter(savePath);
      fileToWrite.write(shoppingList);
      fileToWrite.close();
    } catch (IOException exception) {
      return ErrorHandler.couldNotWriteToFile();
    }
    return true;
  }
}


class UI {

  ShoppingList shoppingList;
  FileHandler fileToRead = new FileHandler();
  Scanner select;

  UI(){
    shoppingList = fileToRead.getShoppingList();
    select = new Scanner(System.in);
  }

  static void clear(){
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

  void displayShoppingList(){
    do{
      clear();
      System.out.print(shoppingList.converToString() + "\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displaySaveShoppingListProcess(){
    String outputMessage = "Could not save a shopping list";
    if (fileToRead.saveShoppingList(shoppingList.converToString())) outputMessage = "A shopping list saved in txt file";
    do{
      clear();
      System.out.print(outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayAddProductProcess(){
    String categoryName, productName;
    String outputMessage = "Could not add a product";
    clear();
    System.out.println("Choose a category by inserting its name\nCetegories: " + shoppingList.getCategoriesNames());
    categoryName = select.nextLine();
    Category category;
    if((category = shoppingList.checkIfCategoryExist(categoryName)) != null){
      clear();
      System.out.println("Insert a name of a product to add");
      productName = select.nextLine();
      if (category.addProductToCategory(productName)) outputMessage = "A product successfully added";
    }
    do{
      clear();
      System.out.print(outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayRemoveProductProcess(){
    String categoryName, productName;
    String outputMessage = "Could not remove a product";
    clear();
    System.out.println("Choose a category by inserting its name\nCetegories: " + shoppingList.getCategoriesNames());
    categoryName = select.nextLine();
    Category category;
    if((category = shoppingList.checkIfCategoryExist(categoryName)) != null){
      clear();
      System.out.println("Insert a name of a product to remove\nProducts: " + category.getProductsNames());
      productName = select.nextLine();
      if (category.removeProductFromCategory(productName)) outputMessage = "A product successfully removed";
    }
    do{
      clear();
      System.out.print(outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayAddCategoryProcess(){
    String categoryName;
    String outputMessage = "Could not add a category";
    clear();
    System.out.println("Insert a name of a category to add");
    categoryName = select.nextLine();
    if(shoppingList.addCategory(categoryName)) outputMessage = "A category added successfully";
    do{
      clear();
      System.out.print( outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayRemoveCategoryProcess(){
    String categoryName;
    String outputMessage = "Could not remove a category";
    clear();
    System.out.println("Insert a name of a category to remove\nCategories: " + shoppingList.getCategoriesNames());
    categoryName = select.nextLine();
    if (shoppingList.removeCategory(categoryName)) outputMessage = "A category successfully removed";
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

  boolean manageMainMenu(){
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

  void mainUI(){
    boolean run = true;
    while(run) {
      clear();
      displayMainMenu();
      run = manageMainMenu();
    }
  }

  public static void main(String[] args){
    UI ui = new UI();
    ui.mainUI();
    UI.clear();
  }
}
