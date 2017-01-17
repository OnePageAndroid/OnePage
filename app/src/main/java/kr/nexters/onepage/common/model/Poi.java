package kr.nexters.onepage.common.model;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by hoody on 2017-01-17.
 */

public class Poi {
    private double latitude;
    private double longitude;
    private String name;
    private String address;

    private Marker marker;


    public Poi() {
    }

    public Poi(String address, double latitude, double longitude, String name, Marker marke) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.marker = marker;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public String toString() {
        return "Poi{" +
                "address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", marker=" + marker +
                '}';
    }
}
