package kr.nexters.onepage.mypage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.InfinitePagerAdapter;
import kr.nexters.onepage.common.InfiniteViewPager;
import kr.nexters.onepage.common.TimeLineAdapter;
import kr.nexters.onepage.common.model.TimeLine;

public class MyPageActivity extends BaseActivity {

    @BindView(R.id.pager_mypage)
    InfiniteViewPager myPagePager;

    @BindView(R.id.tab_mypage)
    TabLayout myPageTab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TimeLineAdapter myPageAdapter;

    InfinitePagerAdapter wrappedAdapter;

    int resIds[] = {R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_delete, android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_dialer, android.R.drawable.ic_dialog_email};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        myPageAdapter = new TimeLineAdapter(getSupportFragmentManager());

        List<TimeLine> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //어댑터에 프래그먼트들을 추가
            items.add(new TimeLine(resIds[i % resIds.length], "" + i));
        }

        myPageAdapter.add(items);

        wrappedAdapter = new InfinitePagerAdapter(myPageAdapter);
        myPagePager.setAdapter(wrappedAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
