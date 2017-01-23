package kr.nexters.onepage.util;

public class Pageable {
    private int firstNumber;
    private int pageNumber;
    private int perPageSize;

    public static Pageable of() {
        return new Pageable();
    }

    public static Pageable of(int firstNumber) {
        Pageable pageable = new Pageable();
        pageable.setFirstNumber(firstNumber);
        pageable.setPageNumber(firstNumber);
        return pageable;
    }

    public void initPage() {
        this.pageNumber = firstNumber;
    }

    public int nextPage() {
        return pageNumber + perPageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPerPageSize() {
        return perPageSize;
    }

    public void setPerPageSize(int perPageSize) {
        this.perPageSize = perPageSize;
    }

    public int getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(int firstNumber) {
        this.firstNumber = firstNumber;
    }
}
