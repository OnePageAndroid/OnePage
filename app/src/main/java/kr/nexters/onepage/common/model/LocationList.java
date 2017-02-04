package kr.nexters.onepage.common.model;

import java.util.List;

import lombok.Data;

/**
 * Created by hoody on 2017-02-01.
 */

@Data
public class LocationList {
    private List<Poi> locations;
    private int resultCount;
}

