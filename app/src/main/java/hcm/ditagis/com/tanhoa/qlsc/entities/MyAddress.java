package hcm.ditagis.com.tanhoa.qlsc.entities;

public class MyAddress {
    private double longtitude;
    private double latitude;
    private String subAdminArea;
    private String location;

    public MyAddress(double longtitude, double latetitude, String subAdminArea, String location) {
        this.longtitude = longtitude;
        this.latitude = latetitude;
        this.subAdminArea = subAdminArea;
        this.location = location;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public void setSubAdminArea(String subAdminArea) {
        this.subAdminArea = subAdminArea;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
