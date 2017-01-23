package kr.nexters.onepage.common.model;

public class PageImage {
    private Long pageId;
    private Long locationId;
    private String url;
    private String name;
    private int resourceId;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public static PageImage of(int resId) {
        PageImage image = new PageImage();
        image.setResourceId(resId);
        return image;
    }
}
