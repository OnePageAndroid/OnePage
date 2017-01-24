package kr.nexters.onepage.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.InfiniteViewPager;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.PageAdapter;
import kr.nexters.onepage.common.WeatherAPI;
import kr.nexters.onepage.common.model.PageResponse;
import kr.nexters.onepage.common.model.WeatherRepo;
import kr.nexters.onepage.main.model.LocationSearchResponse;
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

    Location lastLocation;

    LastLocationManager lastLocationManager;

    long lastLocationId;
    int totalSize;
    int curPage = 0;
    int PAGE_SIZE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initPager();
        initLocationManager();
        initWeather();
    }

    private void initPager() {
        mainAdapter = new PageAdapter(getSupportFragmentManager());
        mainPager.setAdapter(mainAdapter);
        mainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position >= mainAdapter.getCount() - 2 && curPage * PAGE_SIZE < totalSize) {
                    getPages(lastLocationId, curPage + 1, PAGE_SIZE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initLocationManager() {
        //적절한 위치제공자 선택
        lastLocationManager = new LastLocationManager(this, newLocation -> {
            this.lastLocation = newLocation;
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (lastLocation == null) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            NetworkManager.getInstance().getApi().getLocationsFromCoordniates(
                    1.0,
                    1.0
            ).enqueue(new Callback<LocationSearchResponse>() {
                @Override
                public void onResponse(Call<LocationSearchResponse> call, Response<LocationSearchResponse> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getLocations() != null && response.body().getLocations().get(0) != null) {

                        long locationId = response.body().getLocations().get(0).getLocationId();

                        if (lastLocationId != locationId) {
                            lastLocationId = locationId;
                            mainAdapter.clear();
                            getPages(locationId, 0, PAGE_SIZE);
                        }

                    }
                }

                @Override
                public void onFailure(Call<LocationSearchResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void getPages(long locationId, int pageNumber, int perPageSize) {
        NetworkManager.getInstance().getApi()
                .getPagesFromLocation(locationId, pageNumber, perPageSize)
                .enqueue(new Callback<PageResponse>() {
                    @Override
                    public void onResponse(Call<PageResponse> call, Response<PageResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getPages() != null) {
                            ivEmpty.setVisibility(View.GONE);
                            PageResponse pageResponse = response.body();

                            totalSize = pageResponse.getTotalSize();
                            curPage = pageResponse.getPageNumber();

                            mainAdapter.add(pageResponse.getPages());
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse> call, Throwable t) {

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
        //TODO 여기다 지도 액티비티로 가면됨
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivityForResult(intent, REQUEST_MAP);
    }

    @OnClick(R.id.btn_write)
    public void navigasteToWrite() {
        //TODO 여기다 글쓰기 액티비티로 가면됨
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        startActivity(intent);
    }

}
