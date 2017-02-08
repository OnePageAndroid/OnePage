package kr.nexters.onepage.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by hoody on 2017-01-26.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class LocationInfo {

    private String name;
    private int totalPageSize;
    private int periodPageSize;

}
