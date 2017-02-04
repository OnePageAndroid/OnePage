package kr.nexters.onepage.common.model;

import lombok.Data;

/**
 * Created by hoody on 2017-02-05.
 */

@Data
public class PageCount {
    Long locationId;
    int totalPageCount;
    int pageCountByPeriod;
    String startDate;
    String endDate;
}
