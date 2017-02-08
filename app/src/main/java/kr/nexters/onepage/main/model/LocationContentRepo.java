package kr.nexters.onepage.main.model;

import io.reactivex.Flowable;
import kr.nexters.onepage.common.NetworkManager;
import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 1. 31..
 */

@Data
public class LocationContentRepo {
    private long locationId;
    private String name;
    private String url;

    public static Flowable<LocationContentRepo> findLocationContentById(long locationId, String dayType) {
        return NetworkManager.getInstance().getApi()
                .getFlowableLocationImageFromId(locationId, dayType);
    }
}
