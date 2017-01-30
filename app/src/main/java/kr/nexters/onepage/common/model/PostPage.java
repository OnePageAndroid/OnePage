package kr.nexters.onepage.common.model;

import java.io.File;

public class PostPage {

    private String locationId;
    private String email;
    private File image;
    private String content;

    public PostPage() {
    }

    public PostPage( String locationId, String email, File image, String content) {
        this.content = content;
        this.email = email;
        this.image = image;
        this.locationId = locationId;
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

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "Page{" +
                "content='" + content + '\'' +
                ", locationId='" + locationId + '\'' +
                ", email='" + email + '\'' +
                ", image=" + image +
                '}';
    }
}
