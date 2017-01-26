package kr.nexters.onepage.common.model;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by hoody on 2017-01-17.
 */

public class Poi {

    private Long locationId;
    private String address;
    private double latitude;
    private double longitude;
    private String name;
    private Marker marker;

    public Poi() {
    }

    public Poi(Long locationId, String address, String name, double latitude, double longitude, Marker marker) {
        this.locationId = locationId;
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

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Poi{" +
                "address='" + address + '\'' +
                ", locationId=" + locationId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", marker=" + marker +
                '}';
    }

    public Long getLocationId(Marker marker) {
        return locationId;
    }
}
