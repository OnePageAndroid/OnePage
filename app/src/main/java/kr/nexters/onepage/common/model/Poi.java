package kr.nexters.onepage.common.model;

import com.google.android.gms.maps.model.Marker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poi {
    private Long locationId;
    private String address;
    private double latitude;
    private double longitude;
    private String name;
    private Marker marker;

    public Poi(Long locationId, String address, String name, double latitude, double longitude, Marker marker) {
        this.locationId = locationId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.marker = marker;
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
