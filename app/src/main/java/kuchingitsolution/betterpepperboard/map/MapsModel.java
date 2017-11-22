package kuchingitsolution.betterpepperboard.map;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsModel {

    private double latitude, longitude;
    private String reportID, location_id;
    public String reportTitle, reportDescription, imgLink, location_name, created_at;

    public MapsModel(JSONObject jsonObject){
        try {
            this.reportID = jsonObject.getString("id");
            this.location_id = jsonObject.getString("location_id");
            this.latitude = jsonObject.getDouble("lat");
            this.longitude = jsonObject.getDouble("lon");
            this.location_name = jsonObject.getString("name");
            this.reportTitle = jsonObject.getString("title");
            this.reportDescription = jsonObject.getString("description");
            this.imgLink = jsonObject.getString("image_link");
            this.created_at = jsonObject.getString("created_at");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MapsModel(){}

    public void setLatitude (double latitude){
        this.latitude = latitude;
    }

    public void setLongitude (double longitude){
        this.longitude = longitude;
    }

    public void setReportID (String reportID){
        this.reportID = reportID;
    }

    public void setReportTitle (String reportTitle){
        this.reportTitle = reportTitle;
    }

    public void setImgLink(String imgLink){ this.imgLink = imgLink; }

    public void setReportDescription (String reportDescription){
        this.reportDescription = reportDescription;
    }

    public void setLocation_name(String location_name){this.location_name = location_name;}

    public void setCreated_at(String created_at){ this.created_at = created_at;}

    public void setLocation_id(String location_id){ this.location_id = location_id; }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getReportID(){
        return reportID;
    }

    public String getReportTitle(){
        return reportTitle;
    }

    public String getReportDescription(){
        return reportDescription;
    }

    public String getImgLink(){ return imgLink; }

    public String getLocation_name(){ return location_name;}

    public String getLocation_id(){return location_id;}

    public String getCreated_at(){return created_at;}
}
