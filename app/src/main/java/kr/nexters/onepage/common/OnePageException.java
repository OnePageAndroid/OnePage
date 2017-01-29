package kr.nexters.onepage.common;

public class OnePageException extends RuntimeException {
    public OnePageException() {
    }

    public OnePageException(String message) {
        super(message);
    }

    public OnePageException(String message, Throwable cause) {
        super(message, cause);
    }
}
