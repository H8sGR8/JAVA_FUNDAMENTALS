package shopping_list_creator;

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
  
    public Category(String name) {
        this.categoryName = name;
    }
  
    String getCategoryName(){
      return categoryName;
    }
  
    public String getProductsNames(){
      String productsNames = "|| ";
      for (Product product : products) productsNames += (product.getProductName() + " || ");
      return productsNames;
    }
  
    public Product checkIfProductExist(String productName){
      for (Product product : products) if (product.getProductName().equals(productName)) return product;
      return null;
    }
  
    public void addProductToCategory(String productName){
      Product product = new Product(productName);
      this.products.add(product);
    }
  
    public boolean removeProductFromCategory(String producrName){
      Product product;
      if ((product = checkIfProductExist(producrName)) == null) return false;
      this.products.remove(product);
      return true;
    }
  }
  
  
  public class ShoppingList {
  
    ArrayList<Category> categories = new ArrayList<>();

    public void fillList(){
      int currentCategory;
      Scanner readFile = FileHandler.readFile();
      String nextLine = readFile.nextLine();
      String[] categoryNames = nextLine.split(";");
      for (String categoryName : categoryNames) addCategory(categoryName);
      while(readFile.hasNextLine()){
          currentCategory = 0;
          for (String productName : readFile.nextLine().split(";")){
              currentCategory++;
              if("".equals(productName)) continue;
              categories.get(currentCategory - 1).addProductToCategory(productName);
          }
      }
    }
  
    Category checkIfCategoryExist(String categoryName) {
      for (Category category : categories) if(category.getCategoryName().equals(categoryName)) return category;
      return null;
    }

    public void addCategory(String categoryName){
      if (checkIfCategoryExist(categoryName) != null) return;
      Category category = new Category(categoryName);
      categories.add(category);
    }

    public void removeCategory(String categoryName){
      Category category;
      if ((category = checkIfCategoryExist(categoryName)) == null) return;
      categories.remove(category);
    }

    public String getCategoriesNames(){
      String categoriesNames = "|| ";
      for (Category category : categories) categoriesNames += (category.getCategoryName() + " || ");
      return categoriesNames;
    }

    public String converToString(){
      String shopping_list = "";
      for (Category category : categories){
        shopping_list += (category.getCategoryName() + "\n");
        for (Product product : category.products) shopping_list += ("\t" + product.getProductName() + "\n");
      }
      return shopping_list;
    }
  }
