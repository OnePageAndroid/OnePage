package kr.nexters.onepage.landmark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.ImageUtil;
import kr.nexters.onepage.main.PagerFragment;
import kr.nexters.onepage.main.PagerFragment.CallBackToolbar;
import kr.nexters.onepage.main.model.LocationContentRepo;
import kr.nexters.onepage.util.AppbarAnimUtil;
import kr.nexters.onepage.util.ConvertUtil;

import static kr.nexters.onepage.main.MainActivity.KEY_LAST_LOCATION;

public class LandmarkActivity extends BaseActivity {

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
    @BindView(R.id.tv_location_name_eng_expand)
    TextView tvLocationNameEngExpand;
    @BindView(R.id.tv_location_name_kor_collapse)
    TextView tvLocationNameKorCollapse;

    @BindView(R.id.tv_toolbar_total_page)
    TextView tvToolbarTotalPageCollapse;
    @BindView(R.id.tv_toolbar_total_page_expand)
    TextView tvToolbarTotalPageExpand;

    @BindView(R.id.layout_empty)
    ViewGroup layoutEmpty;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);
        unbinder = ButterKnife.bind(this);

        long locationId = getIntent().getLongExtra(KEY_LAST_LOCATION, -1L);
        if (locationId == -1L) {
            toast("잘못된 접근");
            finish();
        }

        initAppbar();
        initFragment(locationId);
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

    private void initFragment(long locationId) {
        PagerFragment pagerFragment = PagerFragment.newInstance(locationId);

        pagerFragment.setOnLongClickPageListener(() -> appbarLayout.setExpanded(false, true));
        pagerFragment.setCallBackToolbar(new CallBackToolbar() {
            @Override
            public void initToolbarPageIndex(int pageSize) {
                tvToolbarTotalPageCollapse.setText(ConvertUtil.integerToCommaString(pageSize));
                tvToolbarTotalPageExpand.setText(ConvertUtil.integerToCommaString(pageSize));
                if (pageSize == 0) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void initWeatherImage(String weatherCode) {
                ivWeather.setAlpha(0.5f);
                int resId = ConvertUtil.findResouceIdByWeatherCode(weatherCode);
                if (resId != -1) {
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

                tvLocationNameEngExpand.setText(locationContentRepo.getEngName());
                tvLocationNameKorExpand.setText(locationContentRepo.getName());
                tvLocationNameKorCollapse.setText(locationContentRepo.getName());
            }
        });


        replaceFragment(R.id.fragment_landmark, pagerFragment);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.btn_back)
    public void onClickBack() {
        finish();
    }
}
