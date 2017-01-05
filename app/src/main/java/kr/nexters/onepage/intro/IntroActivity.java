package kr.nexters.onepage.intro;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import me.relex.circleindicator.CircleIndicator;

public class IntroActivity extends AppCompatActivity {

    //버터나이프 사용
    @BindView(R.id.pager)
    ViewPager pager;

    //인디케이터
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    //뷰페이저 어댑터
    IntroAdapter introAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        //액티비티에서 버터나이프를 사용하려면 onCreate에서 이렇게 사용해야함
        ButterKnife.bind(this);

        introAdapter = new IntroAdapter(getSupportFragmentManager());

        //어댑터에 프래그먼트들을 추가
        introAdapter.add(IntroImageFragment.newInstance(R.mipmap.ic_launcher));
        introAdapter.add(IntroImageFragment.newInstance(android.R.drawable.ic_delete));
        introAdapter.add(IntroImageFragment.newInstance(android.R.drawable.ic_dialog_alert));
        introAdapter.add(IntroLastFragment.newInstance());

        pager.setAdapter(introAdapter);
        indicator.setViewPager(pager);
    }
}