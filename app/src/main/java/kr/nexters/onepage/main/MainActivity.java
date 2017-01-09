package kr.nexters.onepage.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.intro.IntroImageFragment;
import kr.nexters.onepage.intro.MyFragmentAdapter;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MyFragmentAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainAdapter = new MyFragmentAdapter(getSupportFragmentManager());

        //어댑터에 프래그먼트들을 추가
        mainAdapter.add(IntroImageFragment.newInstance(R.mipmap.ic_launcher));
        mainAdapter.add("title1");
        mainAdapter.add(MainFragment.newInstance());
        mainAdapter.add("title2");
        viewPager.setAdapter(mainAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
