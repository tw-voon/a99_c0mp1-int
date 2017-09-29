package kuchingitsolution.betterpepperboard.new_complaint;

public class ReportTypeModel {
    private String categoryName;
    private int categoryIcon;
    private String categoryID;

    public ReportTypeModel (String categoryName, String categoryID){
        this.categoryName = categoryName;
        this.categoryID = categoryID;
    }

    public  ReportTypeModel (){

    }

    public String getCategoryName(){
        return categoryName;
    }

    public int getCategoryIcon(){
        return categoryIcon;
    }

    public String getCategoryID(){ return categoryID;}

    public void setCategoryID(String categoryID){ this.categoryID = categoryID; }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public void setCategoryIcon(int categoryIcon){
        this.categoryIcon = categoryIcon;
    }

}
