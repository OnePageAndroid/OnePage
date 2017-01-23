package kr.nexters.onepage.common.model;

import java.io.File;

public class Page {

    private long pageId;
    private String location = null;
    private String email = null;
    private File image = null;
    private String content = null;

    public Page() {

    }

    public Page(String content, String email, File image, String location, long pageId) {

        this.content = content;
        this.email = email;
        this.image = image;
        this.location = location;
        this.pageId = pageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }
}
