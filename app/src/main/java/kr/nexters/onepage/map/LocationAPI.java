package kr.nexters.onepage.map;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import kr.nexters.onepage.R;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.model.Loc;
import kr.nexters.onepage.common.model.LocationList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static kr.nexters.onepage.map.MapActivity.mGoogleMap;

/**
 * Created by hoody on 2017-02-01.
 */

public interface LocationAPI {

    @GET("location/all")
    Call<LocationList> getLocationList();

    @GET("page/count/total")
    Call<Integer> getTotalPageSize(
            @Query("locationId") Long locationId
    );

    @GET("page/count")
    Call<Integer> getPageSizeByPeriod(
            @Query("locationId") Long locationId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    class Factory {
        public static LocationAPI create() {
            return new Retrofit.Builder()
                    .baseUrl(NetworkManager.SERVER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(LocationAPI.class);
        }
    }

}
