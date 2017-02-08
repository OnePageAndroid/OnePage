package kr.nexters.onepage.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.model.Loc;
import kr.nexters.onepage.common.model.LocationList;

import kr.nexters.onepage.main.MainActivity;
import kr.nexters.onepage.write.WriteActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends BaseActivity {

    private static final String TAG = "MapActivity";
    public static final String CLICKED_LOC = "clicked_loc";
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 100;
    public final static int ZOOM_LEVEL = 13;

    private LocationManager locationManager;
    public GoogleMap mGoogleMap;
    private MapFragment mapFragment;
    private Marker currentMarker;
    private MarkerOptions currentOptions;

    private LatLng currentLatLng;
    private LatLng lastLatLng;

    private LocationList locations;
    private Loc clickedLoc;

    //landmark info box 추가
    @BindView(R.id.tvLocationName) TextView tvLocationName;
    @BindView(R.id.tvTodayPageSize) TextView tvTodayPageSize;
    @BindView(R.id.tvTotalPageSize) TextView tvTotalPageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        //back button
//        setSupportActionBar((Toolbar) findViewById(R.id.mapToolbar));
//        final ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

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
        showLocationList();
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //현재 위치 button 활성화
            mGoogleMap.setMyLocationEnabled(true);

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

    //Action button
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_map, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch(id) {
//            case R.id.action_current_loc :
//                if(currentLatLng == null) {
//                    Toast.makeText(MapActivity.this, getString(R.string.toast_gps_error), Toast.LENGTH_LONG).show();
//                }
//                else {
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));
//                }
//                return true;
//            case android.R.id.home :
//                finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Log.i(TAG, "today : " + today);

        for(Loc loc : locations.getLocations()) {

            //선택된 마커의 정보 가져와서 LocationInfo형태로 저장
            if(loc.getMarker().toString().equals(marker.toString())) { //Marker string으로 비교할때만 된당
                tvLocationName.setText(loc.getName());
                Log.i(TAG, "locationId : " + loc.getLocationId());
                Log.i(TAG, "locationName : " + loc.getName());

                clickedLoc = loc; //랜드마크 정보 박스 클릭했을때 넘겨주기 위한 dto 저장
                Log.i(TAG, "clicked loc : " + clickedLoc.toString());

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

    //landmark info box 클릭 시 이벤트 처리
    //dto 넣어서 넘겨주는건 어떨까요 값은 clickedLoc에 값은 들어있어요
    @OnClick(R.id.ivInfoBox)
    public void onInfoBoxClick() {
//        Intent intent = new Intent(MapActivity.this, );
//        intent.putExtra(CLICKED_LOC, clickedLoc);
//        startActivity(intent);
        Log.i(TAG, "box click");
    }

}
