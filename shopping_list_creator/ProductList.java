package shopping_list_creator;

import java.util.*;

public final class ProductList extends List {

    public void fillProductList() {
        int currentCategory;
        Scanner readedFile = FileHandler.readFile();
        String nextLine = readedFile.nextLine();
        String[] categoryNames = nextLine.split(";");
        for (String categoryName : categoryNames) addCategory(categoryName);
        while(readedFile.hasNextLine()){
            currentCategory = 0;
            for (String productName : readedFile.nextLine().split(";")){
                currentCategory++;
                if("".equals(productName)) continue;
                categories.get(currentCategory - 1).addProductToCategory(productName);
            }
        }

    }
}
