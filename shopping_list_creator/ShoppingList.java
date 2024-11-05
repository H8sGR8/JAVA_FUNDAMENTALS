package shopping_list_creator;

public class ShoppingList extends List {

  public void removeCategory(String categoryName){
    Category category;
    if ((category = checkIfCategoryExist(categoryName)) == null) return;
    categories.remove(category);
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
