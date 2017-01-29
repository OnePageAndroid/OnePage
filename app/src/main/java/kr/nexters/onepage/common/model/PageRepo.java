package kr.nexters.onepage.common.model;

import java.util.List;

import io.reactivex.Flowable;
import kr.nexters.onepage.common.NetworkManager;
import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 1. 24..
 */

@Data
public class PageRepo {
    private int pageNumber;
    private List<Page> pages;
    private int perPageSize;
    private int resultCount;
    private int totalSize;


    public static Flowable<PageRepo> findPageRepoById(long locationId, int page, int pageSize) {
        return NetworkManager.getInstance().getApi()
                .getFlowablePagesFromLocation(locationId, page, pageSize);
    }

    public boolean isPresent() {
        return resultCount != 0;
    }
}
