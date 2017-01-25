package kr.nexters.onepage.main.model;

import java.util.List;

import io.reactivex.Flowable;
import kr.nexters.onepage.common.NetworkManager;
import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 1. 24..
 */

@Data
public class LocationSearchRepo {
    private List<SearchedLocation> locations;
    private int resultCount;

    public static Flowable<Long> getLocationId(double latitude, double longitude) {
        return NetworkManager.getInstance().getApi()
                .getFlowableLocationsFromCoordniates(latitude, longitude)
                .map(repo -> repo.getLocations().get(0).getLocationId());
    }

}
