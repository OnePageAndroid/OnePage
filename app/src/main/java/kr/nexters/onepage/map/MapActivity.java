package kr.nexters.onepage.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.model.Loc;
import kr.nexters.onepage.common.model.LocationList;
import kr.nexters.onepage.landmark.LandmarkActivity;
import kr.nexters.onepage.util.ConvertUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kr.nexters.onepage.main.MainActivity.KEY_LAST_LOCATION;

public class MapActivity extends BaseActivity {

    private static final String TAG = "MapActivity";
    public final static int ZOOM_LEVEL = 13;

    private LocationManager locationManager;
    public GoogleMap mGoogleMap;
    private MapFragment mapFragment;
    private Marker currentMarker;
    private MarkerOptions currentOptions;

    private LatLng currentLatLng;
    private LatLng lastLatLng;

    private LocationList locations;

    private String today;

    //landmark info box 추가
    @BindView(R.id.tvLocationName) TextView tvLocationName;
    @BindView(R.id.tvTodayPageSize) TextView tvTodayPageSize;
    @BindView(R.id.tvTotalPageSize) TextView tvTotalPageSize;
    @BindView(R.id.ivInfoBox) ImageView ivInfoBox;
    @BindView(R.id.ivMyPlace) ImageView ivMyPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        //MainActivity로 가는 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //location manager 준비
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //google map 준비
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

        currentOptions = new MarkerOptions();

        //LocationAPI
        //Setting marker. Get location from db
        today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Log.i(TAG, "today : " + today);
        showLocationList();
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //현재 위치 button 활성화
            mGoogleMap.setMyLocationEnabled(true);

            //구글맵 패딩 설정
            mGoogleMap.setPadding(0, ConvertUtil.dipToPixels(getBaseContext(), 74), 0 ,0);

            //Check location permission
            checkPermission();

            //적절한 위치제공자 선택
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setAltitudeRequired(false);

            String bestProvider = locationManager.getBestProvider(criteria, true);
            Log.i("bestProvider", bestProvider);

            //5초 간격, 3미터 이상 이동시 update
            locationManager.requestLocationUpdates(bestProvider, 10000, 10, locationListener);
            //locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 10, locationListener);

            //TODO 메인인텐트에서 받아온 위치 정보로 현재위치 표시하기

            //last location
            Location lastLocation = locationManager.getLastKnownLocation(bestProvider);
            //Location lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(lastLocation == null) {
                lastLatLng = new LatLng(37.5759879,126.97692289999998); //마지막 위치가 없을 경우 광화문으로
            } else {
                lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            }
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, ZOOM_LEVEL));

            currentOptions.position(lastLatLng);
            currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_landmark));

            currentMarker = googleMap.addMarker(currentOptions);
            currentOptions.visible(false); //hide last location marker

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //마커로 해당 LocationInfo 불러오기
                    Log.i(TAG, marker.toString());
                    getLocationInfo(marker);
                    return true;
                }
            });
        }
    };

    //위치정보 수신, 현재 위치 파악
    LocationListener locationListener = new LocationListener() {
        int flg = 0; //for check first received location

        @Override
        public void onLocationChanged(Location location) {

            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            if(flg == 0) {
                Log.i(TAG, "flag : " + flg);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));
                flg = 1;
            }

            //marker
            currentMarker.setPosition(currentLatLng);

            Log.i("Current Loc", String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //Permission Check
    private void checkPermission() {
        //If permission is denied, request
        if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED ) {
//            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //callBack 메소드와 연결, 준비가 다 끝난 후 map을 불러옴
        mapFragment.getMapAsync(mapReadyCallBack);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Check location permission
        checkPermission();
        //Quit location listener
        locationManager.removeUpdates(locationListener);
    }



    //db에서 받아온 랜드마크 리스트 google map 에 마커 찍기
    public void showLocationList() {
        LocationAPI.Factory.create().getLocationList().enqueue(new Callback<LocationList>() {
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
    public void getLocationInfo(Marker marker) {
        ivMyPlace.setVisibility(View.INVISIBLE);

        for(Loc loc : locations.getLocations()) {

            //선택된 마커의 정보 가져와서 LocationInfo형태로 저장
            if(loc.getMarker().toString().equals(marker.toString())) { //Marker string으로 비교할때만 된당
                tvLocationName.setText(loc.getName());
                Log.i(TAG, "locationId : " + loc.getLocationId());
                Log.i(TAG, "locationName : " + loc.getName());

                ivInfoBox.setOnClickListener(v -> {
                    navigateToLandmark(loc.getLocationId());
                });

                LocationAPI.Factory.create().getTotalPageSize(loc.getLocationId()).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        int total = response.body();

                        tvTotalPageSize.setText(getString(R.string.total_page_size) + String.format("%,d", total));
                        Log.i(TAG, "total : " + total);
                    }
                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });

                LocationAPI.Factory.create().getPageSizeByPeriod(loc.getLocationId(), today, today).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        int today = response.body();

                        tvTodayPageSize.setText(getString(R.string.new_page_size) + String.format("%,d", today));
                        Log.i(TAG, "today : " + today);
                    }
                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });
                break;
            }
        }
    }

    private void navigateToLandmark(long locationId) {
        Intent intent = new Intent(this, LandmarkActivity.class);
        intent.putExtra(KEY_LAST_LOCATION, locationId);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
