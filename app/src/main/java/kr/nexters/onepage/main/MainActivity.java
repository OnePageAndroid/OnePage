package kr.nexters.onepage.main;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.model.PageRepo;
import kr.nexters.onepage.common.model.WeatherRepo;
import kr.nexters.onepage.main.adapter.MainAdapter;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.region.RegionActivity;
import kr.nexters.onepage.util.AppbarAnimUtil;
import kr.nexters.onepage.write.WriteActivity_ucrop;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_MAP = 1000;

    @BindView(R.id.appbar)
    AppBarLayout appbarLayout;

    @BindView(R.id.layout_content_collapse)
    ViewGroup layoutCollapse;
    @BindView(R.id.layout_content_expand)
    LinearLayout layoutExpand;

    @BindView(R.id.iv_empty)
    ImageView ivEmpty;

    @BindView(R.id.pager_main)
    RecyclerViewPager mainPager;

    MainAdapter mainAdapter;

    LastLocationManager lastLocationManager;
    Location lastLocation;
    long lastLocationId;

    int PAGE_SIZE = 5;
    boolean loading = false;

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


    private void initAppbar() {
        appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            AppbarAnimUtil.getInstance().handleAppbar(layoutCollapse, layoutExpand, percentage);
        });
        AppbarAnimUtil.getInstance().startAlphaAnimation(layoutCollapse, 0, View.INVISIBLE);
    }

    private void initPager() {

        mainAdapter = new MainAdapter();
        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mainPager.setLayoutManager(linearLayout);
        mainPager.setAdapter(mainAdapter);

        mainPager.addOnPageChangedListener((prePosotion, curPosition) -> {
            if (!loading && curPosition >= mainAdapter.getItemCount() - 2) {
                getPages(lastLocationId, PAGE_SIZE, false);
            } else if (!loading && curPosition <= 1) {
                getPages(lastLocationId, PAGE_SIZE, true);
            }
        });
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
        if (lastLocation == null) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            disposables.add(LocationSearchRepo
                    .getLocationId(1.0, 1.0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            //첫번째 페이지가 중앙에 와야되서 첫 페이지를 -2로 가져옴
                            newLocationId -> {
                                if (lastLocationId != newLocationId) {
                                    lastLocationId = newLocationId;
                                    mainAdapter.clear();
                                    getFirstPages(lastLocationId, PAGE_SIZE);
                                }
                            },
                            throwable -> toast(throwable.getLocalizedMessage())
                    )

            );
        }
    }

    private void getPages(long locationId, int perPageSize, boolean isReverse) {
        loading = true;

        disposables.add(PageRepo
                .findPageRepoById(locationId, mainAdapter.getLoadPageNum(isReverse), perPageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pageRepo -> {
                            Log.d("PageRepo", pageRepo.getPages().toString());
                            if (isReverse) {
                                mainAdapter.add(0, pageRepo.getPages());
                            } else {
                                mainAdapter.add(pageRepo.getPages());
                            }
                            loading = false;
                        },
                        throwable -> toast(throwable.getLocalizedMessage())
                ));
    }

    private void getFirstPages(long locationId, int perPageSize) {
        disposables.add(PageRepo
                .findPageRepoById(locationId, -2, perPageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pageRepo -> {
                            mainAdapter.add(pageRepo.getPages());
                            Log.d("PageRepo", pageRepo.getPages().toString());
                        },
                        throwable -> toast(throwable.getLocalizedMessage()),
                        () -> {
                            ivEmpty.setVisibility(View.GONE);

                            //뷰페이저의 가운데가 첫번쨰 페이지로 오게 세팅
                            if (mainAdapter.getItemCount() > 0) {
                                mainPager.scrollToPosition(mainAdapter.getFirstPagePostion());
                            }
                        }
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

    @OnClick(R.id.btn_region)
    public void navigateToRegion() {
        startActivity(new Intent(MainActivity.this, RegionActivity.class));
    }

    @OnClick(R.id.btn_write)
    public void navigasteToWrite() {
        Intent intent = new Intent(MainActivity.this, WriteActivity_ucrop.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
