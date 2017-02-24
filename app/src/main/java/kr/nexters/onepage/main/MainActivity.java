package kr.nexters.onepage.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BackPressCloseHandler;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.BusProvider;
import kr.nexters.onepage.common.ImageUtil;
import kr.nexters.onepage.main.model.LocationContentRepo;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.main.model.PageRefreshEvent;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.mypage.MyPageActivity;
import kr.nexters.onepage.util.AppbarAnimUtil;
import kr.nexters.onepage.util.ConvertUtil;
import kr.nexters.onepage.write.WriteActivity;

public class MainActivity extends BaseActivity {

    public static final String KEY_LAST_LOCATION = "key_last_location";

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
    @BindView(R.id.tv_location_name_eng_expand)
    TextView tvLocationNameEngExpand;
    @BindView(R.id.tv_location_name_kor_expand)
    TextView tvLocationNameKorExpand;
    @BindView(R.id.tv_location_name_eng_collapse)
    TextView tvLocationNameEngCollapse;
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

    BackPressCloseHandler backPressCloseHandler;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        BusProvider.register(this);

        backPressCloseHandler = new BackPressCloseHandler(this);
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
        ivWeather.setAlpha(0.1f);
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
        lastLocationManager = new LastLocationManager(this, newLocation -> {
            this.lastLocation = newLocation;
            if(lastLocationId == -1L) {
                getLocation();
            }
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
            public void initToolbarPageIndex(int pageSize) {
                tvToolbarTotalPage.setText(ConvertUtil.integerToCommaString(pageSize));
                if(pageSize == 0) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void initWeatherImage(String weatherCode) {
                int resId = ConvertUtil.findResouceIdByWeatherCode(weatherCode);
                if(resId != -1 ) {
                    Glide.with(getApplicationContext())
                            .load(resId)
                            .asGif()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(ivWeather);
                }
            }

            @Override
            public void initToolbarLocationContent(LocationContentRepo locationContentRepo) {

                Glide.with(getApplicationContext())
                        .load(locationContentRepo.getUrl())
                        .asBitmap()
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                int w = ConvertUtil.getDisplayWidthPixels(getBaseContext());
                                int h = ConvertUtil.dipToPixels(getBaseContext(), 265);
                                Bitmap texture = BitmapFactory.decodeResource(getResources(), R.drawable.page_texture);
                                Bitmap cropTexture = ImageUtil.centerCrop(texture, w, h);
                                Bitmap cropResource = ImageUtil.centerCrop(resource, w, h);
                                ivLocation.setImageBitmap(ImageUtil.multiplyBitmap(cropResource, cropTexture));
                            }
                        });
                tvLocationNameEngExpand.setText(locationContentRepo.getEnglishName());
                tvLocationNameKorExpand.setText(locationContentRepo.getName());
                tvLocationNameEngCollapse.setText(locationContentRepo.getEnglishName());
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
        BusProvider.unRegister(this);
        disposables.clear();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Subscribe
    public void pageRefresh(PageRefreshEvent event) {
        initFragment(lastLocationId);
    }
}
