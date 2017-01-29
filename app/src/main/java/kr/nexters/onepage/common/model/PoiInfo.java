package kr.nexters.onepage.common.model;

/**
 * Created by hoody on 2017-01-26.
 */

public class PoiInfo {

    private String name;
    private int totalPageSize;
    private int todayPageSize;

    public PoiInfo() {
    }

    public PoiInfo(String name, int todayPageSize, int totalPageSize) {
        this.name = name;
        this.todayPageSize = todayPageSize;
        this.totalPageSize = totalPageSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTodayPageSize() {
        return todayPageSize;
    }

    public void setTodayPageSize(int todayPageSize) {
        this.todayPageSize = todayPageSize;
    }

    public int getTotalPageSize() {
        return totalPageSize;
    }

    public void setTotalPageSize(int totalPageSize) {
        this.totalPageSize = totalPageSize;
    }
}
