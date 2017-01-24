package kr.nexters.onepage.main;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.InfinitePagerAdapter;
import kr.nexters.onepage.common.InfiniteViewPager;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.PageAdapter;
import kr.nexters.onepage.common.WeatherAPI;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.common.model.WeatherRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.write.WriteActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_MAP = 1000;

    @BindView(R.id.pager_main)
    InfiniteViewPager mainPager;

    @BindView(R.id.iv_empty)
    ImageView ivEmpty;

    PageAdapter mainAdapter;

    InfinitePagerAdapter wrappedAdapter;

    LocationManager locationManager;
    Location lastLocation;
    Criteria criteria;

    int resIds[] = {R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_delete, android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_dialer, android.R.drawable.ic_dialog_email};

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initLocationManager();
        initWeather();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(lastLocation != null) {
            ivEmpty.setVisibility(View.GONE);
            mainAdapter = new PageAdapter(getSupportFragmentManager());

            List<Page> items = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                //어댑터에 프래그먼트들을 추가
                items.add(new Page(resIds[i % resIds.length], "" + i));
            }
            mainAdapter.add(items);
            wrappedAdapter = new InfinitePagerAdapter(mainAdapter);
            mainPager.setAdapter(wrappedAdapter);
        } else {
            ivEmpty.setVisibility(View.VISIBLE);
        }


    }

    private void initLocationManager() {
        //적절한 위치제공자 선택
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAltitudeRequired(false);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || !locationManager.isProviderEnabled(bestProvider)) {
            return;
        }

        locationManager.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);
        lastLocation = locationManager.getLastKnownLocation(bestProvider);
    }

    private void initWeather() {
        WeatherAPI weatherApi = NetworkManager.getInstance().getWeatherApi();
        weatherApi.getWeather(1, "37.5714000000", "126.9658000000").enqueue(new Callback<WeatherRepo>() {
            @Override
            public void onResponse(Call<WeatherRepo> call, Response<WeatherRepo> response) {
                if (response.isSuccessful()) {
                    WeatherRepo.weather.hourly.Sky sky = response.body().getWeather().getHourly().get(0).getSky();
                    Toast.makeText(getApplicationContext(), sky.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherRepo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.btn_my)
    public void navigateToMyPage() {
        Intent intent = new Intent(this, MyPageActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_map)
    public void navigateToMap() {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivityForResult(intent, REQUEST_MAP);
    }

    @OnClick(R.id.btn_write)
    public void navigasteToWrite() {
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        startActivity(intent);
    }

}
