package kr.nexters.onepage.common.model;

import java.io.File;

/**
 * Created by hoody on 2017-01-17.
 */

public class Post {
    private String location = null;
    private String email = null;
    private File image = null;
    private String content = null;

    public Post() {
    }

    public Post(String content, String email, File image, String location) {
        this.content = content;
        this.email = email;
        this.image = image;
        this.location = location;
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

    @Override
    public String toString() {
        return "Post{" +
                "content='" + content + '\'' +
                ", location='" + location + '\'' +
                ", email='" + email + '\'' +
                ", image=" + image +
                '}';
    }
}
