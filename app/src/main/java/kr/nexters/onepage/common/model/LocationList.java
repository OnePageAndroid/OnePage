package kr.nexters.onepage.common.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by hoody on 2017-02-01.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LocationList {
    private List<Loc> locations;
    private int resultCount;
}

