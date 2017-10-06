package kuchingitsolution.betterpepperboard.personal;

public class FollowedModel {

    private String timestamp, username, category, title, preview, report_id, location;
    private String status;

    FollowedModel(
            String username, String category, String timestamp, String title, String link, String report_id, String status, String location){
        this.username = username;
        this.timestamp = timestamp;
        this.title = title;
        this.category = category;
        this.status = status;
        this.preview = link;
        this.report_id = report_id;
        this.location = location;
    }

    public String getTimestamp(){return timestamp;}
    public String getUsername(){return username;}
    public String getPreview(){return preview;}
    public String getCategory(){return category;}
    public String getTitle(){return title;}
    public String getReport_id(){return report_id;}
    public String getStatus(){return status;}
    public String getLocation(){return location;}

}
