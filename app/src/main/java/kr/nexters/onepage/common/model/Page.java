package kr.nexters.onepage.common.model;

import com.google.common.collect.Lists;

import java.util.List;

public class Page {
    private Long pageId;
    private String locationName;
    private String locationAddress;
    private String email;
    private String content;
    private List<PageImage> images = Lists.newArrayList();
    private int pageNum;

    public Page(int resId, String content) {
        this.content = content;
        images.add(PageImage.of(resId));
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getFirstImageUrl() {
        if (images.size() > 0 && images.get(0) != null) {
            return images.get(0).getUrl();
        }
        return "";
    }
}
