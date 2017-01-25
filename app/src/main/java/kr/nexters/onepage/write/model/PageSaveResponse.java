package kr.nexters.onepage.write.model;

import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 1. 25..
 */

@Data
public class PageSaveResponse {
    private long id;
    private String message;

    public boolean isSuccess() {
        return message.equalsIgnoreCase("SUCCESS");
    }
}
