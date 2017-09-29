package kuchingitsolution.betterpepperboard.hotline;

public class HotlineModel {

    private int id;
    private String title, desc, contact_no;

    public HotlineModel(int id, String title, String contact_no, String desc) {
        this.id = id;
        this.title = title;
        this.contact_no = contact_no;
        this.desc = desc;
    }

    public HotlineModel(){

    }

    public String getTitle(){
        return  title;
    }

    public String getContact_no(){
        return  contact_no;
    }

    public String getDesc(){
        return  desc;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setContact_no(String contact_no){
        this.contact_no = contact_no;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

}
