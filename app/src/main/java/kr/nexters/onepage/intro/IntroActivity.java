package kr.nexters.onepage.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

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
    RecyclerViewPager introPager;

    DummyPageAdapter dummyPageAdapter;

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
    }

    @OnClick(R.id.btn_main)
    public void navigateToMain() {
        PropertyManager.getInstance().setBoolean(KEY_IS_NOT_FIRST, true);
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
