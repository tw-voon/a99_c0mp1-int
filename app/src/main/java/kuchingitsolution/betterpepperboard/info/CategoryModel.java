package kuchingitsolution.betterpepperboard.info;

public class CategoryModel {

    private String categoryName;
    private int category_id;

    public CategoryModel(int category_id, String categoryName){
        this.category_id = category_id;
        this.categoryName = categoryName;
    }

    public String getCategoryName(){ return categoryName; }

    public int getCategory_id() { return category_id; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public void setCategory_id(int category_id) { this.category_id = category_id; }

}
