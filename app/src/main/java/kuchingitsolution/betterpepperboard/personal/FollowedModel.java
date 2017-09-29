package kuchingitsolution.betterpepperboard.personal;

public class FollowedModel {

    private String timestamp, username, avatar, title, preview, report_id;
    private String status;

    FollowedModel(
            String username, String avatar, String timestamp, String title, String link, String report_id, String status){
        this.username = username;
        this.timestamp = timestamp;
        this.title = title;
        this.avatar = avatar;
        this.status = status;
        this.preview = link;
        this.report_id = report_id;
    }

    public String getTimestamp(){return timestamp;}
    public String getUsername(){return username;}
    public String getPreview(){return preview;}
    public String getReport_id(){ return report_id; }
    public String getTitle(){return title;}
    public String getStatus(){return status;}

}
