package shopping_list_creator;

import java.io.*;
import java.util.*;

class ErrorHandler {

    static boolean couldNotWriteToFile(){
      return false;
    }
  
    static void couldNotOpenProductFile(){
      ShoppingListApp.clear();
      System.out.print("Could not open a product file");
      System.exit(1);
    }
  }
  
  
  public class FileHandler {
    static String filePath = "shopping_list_creator\\product_list.csv";
    static String savePath = "shopping_list_creator\\shopping_list.txt";
  
  
    public static Scanner readFile(){
      try {
        Scanner readedFile;
        readedFile = new Scanner(new File(filePath));
        return readedFile;
      } catch (FileNotFoundException exception) {
        ErrorHandler.couldNotOpenProductFile();
      }
      return null;
    }
  
    public static boolean saveShoppingList(String shoppingList){
      try {
        FileWriter fileToWrite;
        fileToWrite = new FileWriter(savePath);
        fileToWrite.write(shoppingList);
        fileToWrite.close();
      } catch (IOException exception) {
        return ErrorHandler.couldNotWriteToFile();
      }
      return true;
    }
  }
