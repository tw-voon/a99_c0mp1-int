package kuchingitsolution.betterpepperboard.map;

public class MapsModel {

    private double latitude, longitude;
    private String reportID;
    public String reportTitle, reportDescription, imgLink;

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
}
