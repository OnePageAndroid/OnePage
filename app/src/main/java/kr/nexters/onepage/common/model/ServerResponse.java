package kr.nexters.onepage.common.model;

/**
 * Created by ohjaehwan on 2017. 1. 16..
 */

public class ServerResponse {
    public String message;
    public String status;

    public boolean isSuccess() {
        return status.equalsIgnoreCase("SUCCESS");
    }
}
