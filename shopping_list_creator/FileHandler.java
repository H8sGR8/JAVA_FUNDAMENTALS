package shopping_list_creator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class FileHandler {
  static final String FILE_PATH = "shopping_list_creator\\product_list.csv";
  static final String SAVE_PATH = "shopping_list_creator\\shopping_list.txt";


  public static Scanner readFile() throws FileNotFoundException{
    Scanner readFile;
    readFile = new Scanner(new File(FILE_PATH));
    return readFile;
  }

  public static void saveShoppingList(String shoppingList) throws IOException{
    FileWriter fileToWrite;
    fileToWrite = new FileWriter(SAVE_PATH);
    fileToWrite.write(shoppingList);
    fileToWrite.close();
  }
}
