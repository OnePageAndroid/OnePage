package kr.nexters.onepage.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.ImageUtil;
import kr.nexters.onepage.main.model.LocationContentRepo;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.util.AppbarAnimUtil;
import kr.nexters.onepage.util.ConvertUtil;
import kr.nexters.onepage.write.WriteActivity;

public class MainActivity extends BaseActivity {

    public static final String KEY_LAST_LOCATION = "key_last_location";
    public static final int REQUEST_WRITE = 1000;

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

    @BindView(R.id.layout_empty)
    ViewGroup layoutEmpty;

    LastLocationManager lastLocationManager;
    Location lastLocation;
    long lastLocationId = -1L;

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
                    .getLocationId(lastLocation.getLatitude(), lastLocation.getLongitude())
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
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initFragment(long locationId) {
        layoutEmpty.setVisibility(View.GONE);
        PagerFragment pagerFragment = PagerFragment.newInstance(locationId);

        pagerFragment.setOnLongClickPageListener(() -> appbarLayout.setExpanded(false, true));
        pagerFragment.setCallBackToolbar(new PagerFragment.CallBackToolbar() {
            @Override
            public void initToolbarPageNumber(int pageSize) {
                tvToolbarTotalPage.setText(ConvertUtil.integerToCommaString(pageSize));
                if(pageSize == 0) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void initWeatherImage(String weatherCode) {
                Glide.with(getApplicationContext())
                        .load(ConvertUtil.WeatherCodeToResouceId(weatherCode))
                        .asGif()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(ivWeather);
            }

            @Override
            public void initToolbarLocationContent(LocationContentRepo locationContentRepo) {

                Glide.with(getApplicationContext())
                        .load(locationContentRepo.getUrl())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                Bitmap texture = BitmapFactory.decodeResource(getResources(), R.drawable.page_texture);
                                ivLocation.setImageBitmap(ImageUtil.multiplyBitmap(resource, texture));
                            }
                        });


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
        startActivity(intent);
    }

    @OnClick(R.id.btn_write)
    public void navigasteToWrite() {
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        intent.putExtra(KEY_LAST_LOCATION, lastLocationId);
        startActivityForResult(intent, REQUEST_WRITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_WRITE && resultCode == RESULT_OK) {
            initFragment(lastLocationId);
        }
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        unbinder.unbind();
        super.onDestroy();
    }
}
