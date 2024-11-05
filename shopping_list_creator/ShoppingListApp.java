package shopping_list_creator;

import java.util.*;


public class ShoppingListApp {

  ShoppingList shoppingList = new ShoppingList();
  Scanner select;

  ShoppingListApp(){
    select = new Scanner(System.in);
    shoppingList.fillList();
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
      5. Add a category
      6. Delete a category
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
    if (FileHandler.saveShoppingList(shoppingList.converToString())) outputMessage = "A shopping list saved in txt file";
    do{
      clear();
      System.out.print(outputMessage + "\n\nPress \"q\" to go back to main menu\n");
    }while(!"q".equals(select.nextLine()));
  }

  void displayAddProductProcess(){
    String categoryName, productName;
    String outputMessage = "Could not find a category";
    clear();
    System.out.println("Choose a category by inserting its name\nCetegories: " + shoppingList.getCategoriesNames());
    categoryName = select.nextLine();
    Category category;
    if((category =  shoppingList.checkIfCategoryExist(categoryName)) != null){
      outputMessage = "could not find a product";
      clear();
      System.out.println("Insert a name of a product to add");
      productName = select.nextLine();
      if (category.checkIfProductExist(productName) == null) {
        outputMessage = "A product successfully added";
        category.addProductToCategory(productName);
      }
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
      if (category.products.isEmpty()) shoppingList.removeCategory(categoryName);
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
    if(shoppingList.checkIfCategoryExist(categoryName) == null){
      shoppingList.addCategory(categoryName);
      outputMessage = "A category added successfully";
    }
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
    if (shoppingList.checkIfCategoryExist(categoryName) != null){
      shoppingList.removeCategory(categoryName);
      outputMessage = "A category successfully removed";
    }
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
    ShoppingListApp app = new ShoppingListApp();
    app.mainUI();
    ShoppingListApp.clear();
  }
}
