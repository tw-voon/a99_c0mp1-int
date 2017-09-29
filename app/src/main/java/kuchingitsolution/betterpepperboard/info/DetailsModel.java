package kuchingitsolution.betterpepperboard.info;

public class DetailsModel {
    public String tips_name, tips_desc, category_id, id;

    DetailsModel(String id, String category_id, String tips_name, String tips_desc){
        this.id = id;
        this.category_id = category_id;
        this.tips_desc = tips_desc;
        this.tips_name = tips_name;
    }

    public String getTips_name(){return tips_name; }
    public String getTips_desc(){ return tips_desc; }
}
