package kuchingitsolution.betterpepperboard.search;

public class SearchModel {
    private String report_id, title, desc;

    public SearchModel(String report_id, String title, String desc){
        this.report_id = report_id;
        this.title = title;
        this.desc = desc;
    }

    public String getReport_id(){ return report_id;}
    public String getTitle(){return title; }
    public String getDesc(){ return  desc; }
}
