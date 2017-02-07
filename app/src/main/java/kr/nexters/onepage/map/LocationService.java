package kr.nexters.onepage.map;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kr.nexters.onepage.R;
import kr.nexters.onepage.common.model.Loc;
import kr.nexters.onepage.common.model.LocationInfo;
import kr.nexters.onepage.common.model.LocationList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kr.nexters.onepage.map.MapActivity.mGoogleMap;

/**
 * Created by hoody on 2017-02-06.
 */

public class LocationService {

    private static final String TAG = "MapActivity";

    private int totalPageSize;
    private int periodPageSize;
    private int resultCount;
    private static LocationList locations;
    private LocationInfo info;

    private LocationAPI locationAPI = LocationAPI.Factory.create();

    //db에서 받아온 랜드마크 리스트 google map 에 마커 찍기
    public void showLocationList() {
        locationAPI.getLocationList().enqueue(new Callback<LocationList>() {
            @Override
            public void onResponse(Call<LocationList> call, Response<LocationList> response) {
                if (response.isSuccessful()) {
                    locations = response.body();

                    MarkerOptions options = new MarkerOptions();
                    options.flat(false);
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.other_landmark));

                    for(Loc loc : locations.getLocations()) {
                        options.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                        loc.setMarker(mGoogleMap.addMarker(options));
                    }
                    Log.i(TAG, "location list : " + locations.toString());
                }
            }

            @Override
            public void onFailure(Call<LocationList> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    //선택된 마커에 대한 정보 구하기
    public LocationInfo getLocationInfo(Marker marker) {
        locationAPI.getLocationList().enqueue(new Callback<LocationList>() {
            @Override
            public void onResponse(Call<LocationList> call, Response<LocationList> response) {
                info = new LocationInfo();
                String today =
                    new SimpleDateFormat("yyyy-mm-dd").format(new Date());

                for(Loc loc : locations.getLocations()) {
                    if(loc.getMarker().toString().equals(marker.toString())) { //string으로 비교할때만 된다!
                        //선택된 마커의 정보 가져와서 LocationInfo형태로 저장
                        info.setName(loc.getName());
                        info.setTotalPageSize(getTotalPageSize(loc.getLocationId()));
                        info.setTotalPageSize(getPageSizeByPeriod(loc.getLocationId(), today, today));

                        break;
                    }
                }
                Log.i(TAG, "Selected location info : " + info);
            }

            @Override
            public void onFailure(Call<LocationList> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return info;
    }

    //총 페이지 수 구하기
    public int getTotalPageSize(Long locationId) {
        locationAPI.getTotalPageSize(locationId).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                totalPageSize = (Integer)response.body();
                Log.i(TAG, "total page size : " + totalPageSize);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }

        });
        return totalPageSize;
    }

    //기간별 페이지 수 구하기
    public int getPageSizeByPeriod(Long locationId, String startDate, String endDate) {
        locationAPI.getPageSizeByPeriod(locationId, startDate, endDate).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                periodPageSize = (Integer)response.body();
                Log.i(TAG, "today page size : " + periodPageSize);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return periodPageSize;
    }
}
