package kr.nexters.onepage.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.model.Loc;

public class MapActivity extends BaseActivity {

    private static final String TAG = "MapActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 100;
    public final static int ZOOM_LEVEL = 13;

    private LocationManager locationManager;
    public static GoogleMap mGoogleMap;
    private MapFragment mapFragment;
    private Marker currentMarker;
    private MarkerOptions currentOptions;

    private MapInfoFragment mapInfoFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private LatLng currentLatLng;
    private LatLng lastLatLng;
    private Loc loc;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //MainActivity로 가는 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //location manager 준비
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //google map 준비
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

        currentOptions = new MarkerOptions();

        fragmentManager = getFragmentManager();

        mapInfoFragment = (MapInfoFragment)getFragmentManager().findFragmentById(R.id.mapInfoFragment);

        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.hide(mapInfoFragment);

        //LocationAPI test
        locationService = new LocationService();
        //Setting marker. Get location from db
        locationService.showLocationList();
        locationService.getTotalPageSize(Long.parseLong("5"));
        locationService.getPageSizeByPeriod(Long.parseLong("5"), "2017-02-07", "2017-02-07");

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
                    //Log.i(TAG, "location name : " + loc.getLocationName(marker));
                    //Log.i(TAG, "location info : " + loc.getLocationInfo(marker));

                    //List<Loc> ll = new ArrayList<Loc>();
                    //ll = locationService.getLocationList();
//                    locationService.getLocationList();
                    //show location info
//                    List<Loc> locl = locationService.getLocationList();
//                    Log.i(TAG, "loc : " + locl.get(0).toString());

//                    Log.i(TAG, "ll" + locationService.getLocationList().getLocations().get(1));
                    //fragmentTransaction.show(mapInfoFragment);
                    Log.i(TAG, "click marker : " + marker.toString());
                    locationService.getLocationInfo(marker);
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

    private MarkerOptions createMarkerOptions(LatLng latLng, String title, String content, int resource) {

        MarkerOptions newOptions = new MarkerOptions();

        newOptions.position(latLng);
        newOptions.title(title);
        newOptions.snippet(content);
        newOptions.icon(BitmapDescriptorFactory.fromResource(resource));

        return newOptions;
    }

    //Permission Check
    private void checkPermission() {
        //If permission is denied, request
        if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED ) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
    }

    //Action button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_current_loc :
                if(currentLatLng == null) {
                    Toast.makeText(MapActivity.this, getString(R.string.toast_gps_error), Toast.LENGTH_LONG).show();
                }
                else {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));
                }
                return true;
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

}
