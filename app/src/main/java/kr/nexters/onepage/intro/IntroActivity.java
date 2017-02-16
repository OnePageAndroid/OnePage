package kr.nexters.onepage.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.main.MainActivity;

import static kr.nexters.onepage.common.OnePageApplication.getContext;
import static kr.nexters.onepage.common.PropertyManager.KEY_IS_NOT_FIRST;

public class IntroActivity extends BaseActivity {

    @BindView(R.id.pager_intro)
    DisabledRecyclerViewPager introPager;

    @BindView(R.id.tvCoachCenter)
    TextView tvCoachCenter;

    @BindView(R.id.tvCoachMap)
    TextView tvCoachMap;

    @BindView(R.id.tvCoachMypage)
    TextView tvCoachMypage;

    @BindView(R.id.tvCoachBookmark)
    TextView tvCoachBookmark;

    @BindView(R.id.ivCoachBookmark)
    ImageView ivCoachBookmark;

    DummyPageAdapter dummyPageAdapter;

    private static int COLOR_ORANGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ButterKnife.bind(this);

        dummyPageAdapter = new DummyPageAdapter();
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        introPager.setLayoutManager(linearLayout);
        introPager.setAdapter(dummyPageAdapter);
        introPager.scrollToPosition(1);
        introPager.setPagingEnabled(false); //disabled scroll


        //Setting Intro UI

        //Setting text Color
        COLOR_ORANGE = ContextCompat.getColor(IntroActivity.this, R.color.colorOrange);

        final SpannableStringBuilder coach_map = new SpannableStringBuilder(getString(R.string.coach_map));
        final SpannableStringBuilder coach_mypage = new SpannableStringBuilder(getString(R.string.coach_mypage));
        final SpannableStringBuilder coach_center = new SpannableStringBuilder(getString(R.string.coach_center));
        final SpannableStringBuilder coach_bookmark = new SpannableStringBuilder(getString(R.string.coach_bookmark));

        coach_mypage.setSpan(new ForegroundColorSpan(COLOR_ORANGE), 3, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        coach_mypage.setSpan(new ForegroundColorSpan(COLOR_ORANGE), 10, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        coach_map.setSpan(new ForegroundColorSpan(COLOR_ORANGE), 5, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        coach_center.setSpan(new ForegroundColorSpan(COLOR_ORANGE), 2, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        coach_center.setSpan(new ForegroundColorSpan(COLOR_ORANGE), 10, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        coach_bookmark.setSpan(new ForegroundColorSpan(COLOR_ORANGE), 7, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvCoachMap.setText(coach_map);
        tvCoachMypage.setText(coach_mypage);
        tvCoachCenter.setText(coach_center);
        tvCoachBookmark.setText(coach_bookmark);

    }

    @OnClick(R.id.btn_main)
    public void navigateToMain() {
        PropertyManager.getInstance().setBoolean(KEY_IS_NOT_FIRST, true);
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
