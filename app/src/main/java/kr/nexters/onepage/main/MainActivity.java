package kr.nexters.onepage.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.intro.MainFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pager_main)
    ViewPager mainPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //툴바 설정
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainAdapter = new MainAdapter(getSupportFragmentManager());

        //어댑터에 프래그먼트들을 추가
        mainAdapter.add(MainFragment.newInstance(R.mipmap.ic_launcher));
        mainAdapter.add(MainFragment.newInstance(android.R.drawable.ic_dialog_alert));
        mainAdapter.add(MainFragment.newInstance(android.R.drawable.ic_delete));
        mainAdapter.add(MainFragment.newInstance(android.R.drawable.ic_input_add));
        mainAdapter.add(MainFragment.newInstance(android.R.drawable.ic_dialog_dialer));
        mainAdapter.add(MainFragment.newInstance(android.R.drawable.ic_dialog_email));

        mainPager.setAdapter(mainAdapter);
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
