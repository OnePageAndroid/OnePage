package kr.nexters.onepage.main;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.InfiniteViewPager;
import kr.nexters.onepage.common.PageAdapter;
import kr.nexters.onepage.common.model.PageRepo;
import kr.nexters.onepage.common.model.WeatherRepo;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.write.WriteActivity;

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

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initAppbar();
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
        if (lastLocation == null) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            disposables.add(LocationSearchRepo
                    .getLocationId(1.0, 1.0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            newLocationId -> {
                                if (lastLocationId != newLocationId) {
                                    lastLocationId = newLocationId;
                                    mainAdapter.clear();
                                    getPages(lastLocationId, 0, PAGE_SIZE);
                                }
                            },
                            throwable -> toast(throwable.getLocalizedMessage())
                    )
            );
        }
    }

    private void getPages(long locationId, int pageNumber, int perPageSize) {
        disposables.add(PageRepo
                .findPageRepoById(locationId, pageNumber, perPageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pageRepo -> {
                            totalSize = pageRepo.getTotalSize();
                            curPage = pageRepo.getPageNumber();

                            mainAdapter.add(pageRepo.getPages());
                        },
                        throwable -> toast(throwable.getLocalizedMessage()),
                        () -> ivEmpty.setVisibility(View.GONE)
                ));
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

        Intent intent = new Intent(MainActivity.this, WriteActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }


    /**
     * Appbar Animiation
     **/

    private static final float PERCENTAGE_TO_SHOW_CONTENT_AT_TOOLBAR = 0.5f;
    private static final int ALPHA_ANIMATIONS_DURATION = 500;

    private boolean isCollapseLayoutVisible = false;

    @BindView(R.id.appbar)
    AppBarLayout appbarLayout;

    @BindView(R.id.layout_content_collapse)
    ViewGroup layoutCollapse;
    @BindView(R.id.layout_content_expand)
    LinearLayout layoutExpand;

    private void initAppbar() {
        appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            handleToolbarVisibility(percentage);
        });
        startAlphaAnimation(layoutCollapse, 0, View.INVISIBLE);
    }


    private void handleToolbarVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_CONTENT_AT_TOOLBAR) {
            if (!isCollapseLayoutVisible) {
                startAlphaAnimation(layoutCollapse, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(layoutExpand, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isCollapseLayoutVisible = true;
            }

        } else {
            if (isCollapseLayoutVisible) {
                startAlphaAnimation(layoutCollapse, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startAlphaAnimation(layoutExpand, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isCollapseLayoutVisible = false;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
