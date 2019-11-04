package kuchingitsolution.betterpepperboard.complaint;


import org.json.JSONException;
import org.json.JSONObject;

public class ComplaintModel {

    private String id, username, type_name, location_name, status_name, officer_name, link, last_action, title, description, created_at, suggestion;
    private int user_id, type_id, location_id, status_id, officer_id, media_type, affected, support;
    private double lat, lon;

    public ComplaintModel(JSONObject complaint) {
        try {

            this.id = complaint.getString("id");
            this.user_id = complaint.getInt("user_id");
            this.username = complaint.getString("username");
            this.type_id = complaint.getInt("type_id");
            this.type_name = complaint.getString("type_name");
            this.location_id = complaint.getInt("location_id");
            this.location_name = complaint.getString("location_name");
            this.lat = complaint.getDouble("lat");
            this.lon = complaint.getDouble("lon");
            this.status_id = complaint.getInt("status_id");
            this.status_name = complaint.getString("status_name");
            this.officer_id = complaint.getInt("officer_id");
            this.officer_name = complaint.optString("officer_name", "NULL");
            this.title = complaint.getString("title");
            this.description = complaint.getString("description");
            this.suggestion = complaint.optString("suggestion", null);
            this.media_type = complaint.getInt("media_type");
            this.link = complaint.getString("link");
            this.last_action = complaint.getString("last_action");
            this.affected = complaint.getInt("affected");
            this.support = complaint.getInt("support");
            this.created_at = complaint.getString("created_at");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return username;
    }

    public String getReportTitle() {
        return title;
    }

    public String getNewsDescription() {
        return description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getLink() {
        return link;
    }

    public String getId() {
        return id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public int getAffected() {
        return affected;
    }

    public int getSupported() {
        return support;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public int getType_id() {
        return type_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public int getOfficer_id() {
        return officer_id;
    }

    public int getMedia_type() {
        return media_type;
    }

    public int getSupport() {
        return support;
    }

    public String getOfficer_name() {
        return officer_name;
    }

    public String getType_name() {
        return type_name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setOfficer(int id, String name) {
        this.officer_id = id;
        this.officer_name = name;
    }

    public void support() {
        this.support = this.support + 1;
    }

    public void noSupport() {
        this.support = this.support - 1;
    }

    public void affected() {
        this.affected = this.affected + 1;
    }

    public void noAffected() {
        this.affected = this.affected - 1;
    }
}
