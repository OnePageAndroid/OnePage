package kr.nexters.onepage.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.TimeLineAdapter;
import kr.nexters.onepage.common.model.TimeLine;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pager_main)
    ViewPager mainPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TimeLineAdapter mainAdapter;

    int resIds[] = {R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_delete, android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_dialer, android.R.drawable.ic_dialog_email};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //툴바 설정
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainAdapter = new TimeLineAdapter(getSupportFragmentManager());


        List<TimeLine> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //어댑터에 프래그먼트들을 추가
            items.add(new TimeLine(resIds[i % resIds.length], "" + i));
        }
        mainAdapter.add(items);

        int COUNT = items.size();

        mainPager.setAdapter(mainAdapter);
        mainPager.setCurrentItem(COUNT);
        mainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position < COUNT)        //1번째 아이템에서 마지막 아이템으로 이동하면
                    mainPager.setCurrentItem(position + COUNT, false);
                else if (position >= COUNT * 2)     //마지막 아이템에서 1번째 아이템으로 이동하면
                    mainPager.setCurrentItem(position - COUNT, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick(R.id.btn_map)
    public void navigateToMap() {
        //TODO 여기다 지도 액티비티로 가면됨
        Log.d("ojh102", "map");
    }

    @OnClick(R.id.btn_write)
    public void navigasteToWrite() {
        //TODO 여기다 글쓰기 액티비티로 가면됨
        Log.d("ojh102", "write");
    }
}
