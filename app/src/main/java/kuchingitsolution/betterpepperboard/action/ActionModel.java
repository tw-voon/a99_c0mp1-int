package kuchingitsolution.betterpepperboard.action;

public class ActionModel {
    private String action_taken, created_at, link, report_id;
    private int status_id, media_type;

    public ActionModel(String action_taken, String created_at, String link, String report_id, int status_id, int media_type){
        this.action_taken = action_taken;
        this.created_at = created_at;
        this.link = link;
        this.report_id = report_id;
        this.status_id = status_id;
        this.media_type = media_type;
    }

    public String getAction_taken(){
        return action_taken;
    }

    public String getCreated_at(){
        return created_at;
    }

    public String getLink(){
        return link;
    }

    public String getReport_id(){
        return report_id;
    }

    public int getStatus_id(){
        return status_id;
    }

    public int getMedia_type(){
        return media_type;
    }
}
