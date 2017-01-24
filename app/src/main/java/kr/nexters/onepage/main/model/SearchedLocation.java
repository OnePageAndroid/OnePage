package kr.nexters.onepage.main.model;

import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 1. 24..
 */

@Data
public class SearchedLocation {
    private long locationId;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
}
