package vn.ditagis.com.tanhoa.qlsc.entities;

public class DAddress {
    private double longtitude;
    private double latitude;
    private String subAdminArea;
    private String locality;
    private String location;

    public DAddress(double longtitude, double latitude, String subAdminArea, String locality, String location) {
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.subAdminArea = subAdminArea;
        this.locality = locality;
        this.location = location;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public String getLocality() {
        return locality;
    }

    public String getLocation() {
        return location;
    }
}
