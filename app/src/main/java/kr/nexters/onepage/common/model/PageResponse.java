package kr.nexters.onepage.common.model;

import java.util.List;

import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 1. 24..
 */

@Data
public class PageResponse {
    private int pageNumber;
    private List<Page> pages;
    private int perPageSize;
    private int resultCount;
    private int totalSize;
}
