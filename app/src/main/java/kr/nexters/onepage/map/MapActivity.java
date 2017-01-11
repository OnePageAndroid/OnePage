package kr.nexters.onepage.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import kr.nexters.onepage.R;

public class MapActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 100;
    public final static int ZOOM_LEVEL = 15;

    private LocationManager locationManager;
    private GoogleMap mGoogleMap;
    private MapFragment mapFragment;
    private MarkerOptions markerOptions;
    private Marker marker;

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
        markerOptions = new MarkerOptions();
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //Android version 위치 요청 권한 확인
            checkPermission();

            //적절한 위치제공자 선택
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setAltitudeRequired(false);

            String bestProvider = locationManager.getBestProvider(criteria, true);
            Log.i("bestProvider", bestProvider);

            //10초 간격, 3미터 이상 이동시 update
            locationManager.requestLocationUpdates(bestProvider, 10000, 3, locationListener);

            //디비에서 받아올 위도, 경도로 Marker 표시 해주기

            LatLng loc = new LatLng(37.5545168, 126.9706483);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM_LEVEL));

            markerOptions.position(new LatLng(37.5545168, 126.9706483)); //test
            markerOptions.title("서울역");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker));
            marker = googleMap.addMarker(markerOptions);
            marker.showInfoWindow();
        }
    };

    //위치정보 수신, 현재 위치 파악
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, ZOOM_LEVEL));

            //현재 위치에 마커
            marker.setPosition(currentLoc);

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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MapActivity.this, "Permission was granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MapActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    //Permission Check
    private void checkPermission() {
        if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            return;
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
                Toast.makeText(MapActivity.this, "현재위치로", Toast.LENGTH_LONG).show();
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
        //Android version 위치 요청 권한 확인
        checkPermission();
        //위치 정보 수신 종료
        locationManager.removeUpdates(locationListener);
    }
}
