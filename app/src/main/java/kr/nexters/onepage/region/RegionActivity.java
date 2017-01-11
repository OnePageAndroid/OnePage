package kr.nexters.onepage.region;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.InfinitePagerAdapter;
import kr.nexters.onepage.common.InfiniteViewPager;
import kr.nexters.onepage.common.TimeLineAdapter;
import kr.nexters.onepage.common.model.TimeLine;

public class RegionActivity extends AppCompatActivity {

    @BindView(R.id.pager_mypage)
    InfiniteViewPager regionPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TimeLineAdapter regionAdapter;

    InfinitePagerAdapter wrappedAdapter;

    int resIds[] = {R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_delete, android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_dialer, android.R.drawable.ic_dialog_email};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        regionAdapter = new TimeLineAdapter(getSupportFragmentManager());

        List<TimeLine> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //어댑터에 프래그먼트들을 추가
            items.add(new TimeLine(resIds[i % resIds.length], "" + i));
        }

        regionAdapter.add(items);

        wrappedAdapter = new InfinitePagerAdapter(regionAdapter);
        regionPager.setAdapter(wrappedAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
