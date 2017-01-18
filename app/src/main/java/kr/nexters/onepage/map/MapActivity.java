package kr.nexters.onepage.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Map;

import kr.nexters.onepage.R;
import kr.nexters.onepage.common.model.Poi;

public class MapActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 100;
    public final static int ZOOM_LEVEL = 13;

    private LocationManager locationManager;
    private GoogleMap mGoogleMap;
    private MapFragment mapFragment;
    private MarkerOptions currentOptions;
    private MarkerOptions poiOptions;
    private Marker currentMarker;

    private LatLng currentLatLng;
    private LatLng lastLatLng;

    private ArrayList<Poi> poiList;
    private Poi poi;

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

        //marker 준비
        currentOptions = new MarkerOptions();
        poiOptions = new MarkerOptions();
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //Check location permission
            checkPermission();

            //적절한 위치제공자 선택
//            Criteria criteria = new Criteria();
//            criteria.setAccuracy(Criteria.ACCURACY_FINE);
//            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
//            criteria.setAltitudeRequired(false);
//
//            String bestProvider = locationManager.getBestProvider(criteria, true);
//            Log.i("bestProvider", bestProvider);
//            locationManager.requestLocationUpdates(bestProvider, 10000, 3, locationListener);

            //5초 간격, 3미터 이상 이동시 update
            //use fake gps for testing
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 10, locationListener);

            //last location
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, ZOOM_LEVEL));

            //setting marker
            currentOptions.position(lastLatLng);
            currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker));
            currentOptions.title("current loc");
            currentOptions.snippet("fake");

            currentMarker = googleMap.addMarker(currentOptions);
            currentMarker.showInfoWindow();

            currentOptions.visible(false); //hide last location marker

            //디비에서 받아올 위도, 경도로 Marker 표시 해주기

            //LatLng loc = new LatLng(37.5545168, 126.9706483);
            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM_LEVEL));


            //set marker from location list
            //sample data
            getLocation();
            for (Poi poi : poiList) {
                //change db location
                poiOptions.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
                poiOptions.title(poi.getName());
                poiOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker));

                poi.setMarker(googleMap.addMarker(poiOptions));
                poi.getMarker().showInfoWindow();

                Log.i("MapActivityLog", poi.toString());
            }

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //click event
                    finish();
                    return true;
                }
            });
        }
    };

    //위치정보 수신, 현재 위치 파악
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));

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
                Toast.makeText(MapActivity.this, "현재위치로", Toast.LENGTH_LONG).show();

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));

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

    //Permission Check
    private void checkPermission() {
        //If permission is denied, request
        if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED ) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
    }

    private void getLocation() {
        poiList = new ArrayList();

        //sample data
        poiList.add(new Poi(null, 37.5759879,126.97692289999998, "광화문", null));
        poiList.add(new Poi(null, 37.5545168,126.9706483, "서울역", null));
        poiList.add(new Poi(null, 37.60193, 127.04153, "월곡역", null));

    }

}
