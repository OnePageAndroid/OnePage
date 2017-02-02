package kr.nexters.onepage.main;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.landmark.LandmarkActivity;
import kr.nexters.onepage.main.model.LocationContentRepo;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.util.AppbarAnimUtil;
import kr.nexters.onepage.util.ConvertUtil;
import kr.nexters.onepage.write.WriteActivity;

public class MainActivity extends BaseActivity {

    public static final String KEY_LAST_LOCATION = "key_last_location";
    private static final int REQUEST_MAP = 1000;

    @BindView(R.id.appbar)
    AppBarLayout appbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_content_collapse)
    ViewGroup layoutCollapse;
    @BindView(R.id.layout_content_expand)
    ViewGroup layoutExpand;

    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.iv_weather)
    ImageView ivWeather;
    @BindView(R.id.tv_location_name_kor_expand)
    TextView tvLocationNameKorExpand;
    @BindView(R.id.tv_location_name_kor_collapse)
    TextView tvLocationNameKorCollapse;

    @BindView(R.id.tv_toolbar_total_page)
    TextView tvToolbarTotalPage;

    @BindView(R.id.iv_empty)
    ImageView ivEmpty;

    LastLocationManager lastLocationManager;
    Location lastLocation;
    long lastLocationId;

    Unbinder unbinder;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initAppbar();
        initLocationManager();
    }

    private void initAppbar() {
        appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            AppbarAnimUtil.getInstance().handleAppbar(layoutCollapse, layoutExpand, percentage);
        });
        AppbarAnimUtil.getInstance().startAlphaAnimation(layoutCollapse, 0, View.INVISIBLE);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        ivWeather.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initLocationManager() {
        lastLocationManager = new LastLocationManager(this, newLocation -> this.lastLocation = newLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    private void getLocation() {
        if (lastLocation != null) {
            disposables.add(LocationSearchRepo
                    .getLocationId(1.0, 1.0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            newLocationId -> {
                                if (lastLocationId != newLocationId) {
                                    lastLocationId = newLocationId;
                                    initFragment(lastLocationId);
                                }
                            },
                            throwable -> toast(throwable.getLocalizedMessage())
                    )

            );
        } else {
            ivEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initFragment(long locationId) {
        ivEmpty.setVisibility(View.GONE);
        PagerFragment pagerFragment = PagerFragment.newInstance(locationId);

        pagerFragment.setOnLongClickPageListener(() -> appbarLayout.setExpanded(false, true));
        pagerFragment.setCallBackToolbar(new PagerFragment.CallBackToolbar() {
            @Override
            public void initToolbarPageNumber(int pageSize) {
                tvToolbarTotalPage.setText(ConvertUtil.integerToCommaString(pageSize));
            }

            @Override
            public void initWeatherImage(String weatherCode) {
                int resId = ConvertUtil.WeatherCodeToResouceId(weatherCode);
                Glide.with(getApplicationContext())
                        .load(resId != 0 ? resId : 0)
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(ivWeather);
            }

            @Override
            public void initToolbarLocationContent(LocationContentRepo locationContentRepo) {
                Glide.with(getApplicationContext())
                        .load(locationContentRepo.getUrl())
                        .into(ivLocation);
                tvLocationNameKorExpand.setText(locationContentRepo.getName());
                tvLocationNameKorCollapse.setText(locationContentRepo.getName());
            }
        });


        replaceFragment(R.id.fragment_main, pagerFragment);
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

    @OnClick(R.id.btn_landmark)
    public void navigateToLandmark() {
        Intent intent = new Intent(this, LandmarkActivity.class);
        intent.putExtra(KEY_LAST_LOCATION, lastLocationId);
        startActivity(intent);
    }

    @OnClick(R.id.btn_write)
    public void navigasteToWrite() {
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        intent.putExtra(KEY_LAST_LOCATION, lastLocationId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        unbinder.unbind();
        super.onDestroy();
    }
}
