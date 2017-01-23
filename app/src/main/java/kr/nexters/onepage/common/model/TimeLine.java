package kr.nexters.onepage.common.model;

/**
 * Created by OhJaeHwan on 2017-01-10.
 */

public class TimeLine {
    private int imgRes;
    private String imgUrl;
    private String text;
    private int pageNumber;

    public TimeLine() {}

    public TimeLine(int imgRes, String text) {
        this.imgRes = imgRes;
        this.text = text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }
}
