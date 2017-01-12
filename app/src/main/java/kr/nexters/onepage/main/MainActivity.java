package kr.nexters.onepage.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.InfinitePagerAdapter;
import kr.nexters.onepage.common.InfiniteViewPager;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.TimeLineAdapter;
import kr.nexters.onepage.common.WeatherAPI;
import kr.nexters.onepage.common.model.TimeLine;
import kr.nexters.onepage.common.model.WeatherRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.write.WriteActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MAP = 1000;

    @BindView(R.id.pager_main)
    InfiniteViewPager mainPager;

    TimeLineAdapter mainAdapter;

    InfinitePagerAdapter wrappedAdapter;

    int resIds[] = {R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_delete, android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_dialer, android.R.drawable.ic_dialog_email};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initWeather();

        mainAdapter = new TimeLineAdapter(getSupportFragmentManager());

        List<TimeLine> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //어댑터에 프래그먼트들을 추가
            items.add(new TimeLine(resIds[i % resIds.length], "" + i));
        }

        mainAdapter.add(items);

        wrappedAdapter = new InfinitePagerAdapter(mainAdapter);
        mainPager.setAdapter(wrappedAdapter);

    }

    private void initWeather() {
        WeatherAPI weatherApi = NetworkManager.getInstance().getApiFromClass(WeatherAPI.class);
        weatherApi.getWeather(1, "37.5714000000", "126.9658000000").enqueue(new Callback<WeatherRepo>() {
            @Override
            public void onResponse(Call<WeatherRepo> call, Response<WeatherRepo> response) {
                if(response.isSuccessful()) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

        }

    }

}
