package kr.nexters.onepage.main;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.model.WeatherRepo;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.region.RegionActivity;
import kr.nexters.onepage.util.AppbarAnimUtil;
import kr.nexters.onepage.write.WriteActivity;

import static kr.nexters.onepage.main.PagerFragment.KEY_LAST_LOCATION;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_MAP = 1000;

    @BindView(R.id.appbar)
    AppBarLayout appbarLayout;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;

    @BindView(R.id.layout_content_collapse)
    ViewGroup layoutCollapse;
    @BindView(R.id.layout_content_expand)
    LinearLayout layoutExpand;

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
        initWeather();
    }


    private void initAppbar() {
        appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            AppbarAnimUtil.getInstance().handleAppbar(layoutCollapse, layoutExpand, percentage);
        });
        AppbarAnimUtil.getInstance().startAlphaAnimation(layoutCollapse, 0, View.INVISIBLE);
    }

    private void initLocationManager() {
        lastLocationManager = new LastLocationManager(this, newLocation -> this.lastLocation = newLocation);
    }

    private void initWeather() {
        disposables.add(WeatherRepo
                .getSky()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sky -> toast(sky.getName()),
                        throwable -> toast(throwable.getLocalizedMessage())
                )
        );
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
                                    initFragment();
                                }
                            },
                            throwable -> toast(throwable.getLocalizedMessage())
                    )

            );
        } else {
            ivEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initFragment() {
        ivEmpty.setVisibility(View.GONE);
        PagerFragment pagerFragment = PagerFragment.newInstance(lastLocationId);
        pagerFragment.setOnLongClickPageListener(() -> appbarLayout.setExpanded(false, true));
        replaceFragment(R.id.fragment_main, pagerFragment);
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

    @OnClick(R.id.btn_region)
    public void navigateToRegion() {
        Intent intent = new Intent(this, RegionActivity.class);
        intent.putExtra(KEY_LAST_LOCATION, lastLocationId);
        startActivity(intent);
    }

    @OnClick(R.id.btn_write)
    public void navigasteToWrite() {
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        unbinder.unbind();
        super.onDestroy();
    }
}
