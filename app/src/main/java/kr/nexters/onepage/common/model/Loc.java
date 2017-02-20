package kr.nexters.onepage.common.model;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Loc implements Serializable {
    private Long locationId;
    private String address;
    private double latitude;
    private double longitude;
    private String name;
    private String engName;
    private Marker marker;
}
