package kr.nexters.onepage.main.model;

import java.util.List;

import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 1. 24..
 */

@Data
public class LocationSearchResponse {
    private List<SearchedLocation> locations;
    private int resultCount;
}
