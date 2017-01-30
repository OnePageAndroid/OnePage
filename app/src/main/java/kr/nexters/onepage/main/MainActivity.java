package kr.nexters.onepage.main;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.event.Events;
import kr.nexters.onepage.common.event.RxBus;
import kr.nexters.onepage.common.model.WeatherRepo;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.region.RegionActivity;
import kr.nexters.onepage.util.AppbarAnimUtil;
import kr.nexters.onepage.util.ConvertUtil;
import kr.nexters.onepage.write.WriteActivity;

import static kr.nexters.onepage.main.PagerFragment.KEY_LAST_LOCATION;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_MAP = 1000;

    @BindView(R.id.appbar)
    AppBarLayout appbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_content_collapse)
    ViewGroup layoutCollapse;
    @BindView(R.id.layout_content_expand)
    LinearLayout layoutExpand;

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
        initWeather();
        initToolbarPageNum();
    }


    private void initAppbar() {
        appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            AppbarAnimUtil.getInstance().handleAppbar(layoutCollapse, layoutExpand, percentage);
        });
        AppbarAnimUtil.getInstance().startAlphaAnimation(layoutCollapse, 0, View.INVISIBLE);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
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

    private void initToolbarPageNum() {
        RxBus.getInstance().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    int pageNum = ((Events.ToolbarPageNumEvent) event).getTotalPageNum();
                    tvToolbarTotalPage.setText(
                            String.format(
                                    getResources().getString(R.string.main_toolbar_page),
                                    ConvertUtil.integerToCommaString(pageNum)
                            )
                    );
                });
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
