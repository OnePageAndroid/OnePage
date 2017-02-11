package kr.nexters.onepage.intro;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import me.relex.circleindicator.CircleIndicator;

public class IntroActivity extends BaseActivity {

    //버터나이프 사용
    @BindView(R.id.pager_intro)
    ViewPager introPager;

    //뷰페이저 어댑터
    IntroPagerAdapter introAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        //액티비티에서 버터나이프를 사용하려면 onCreate에서 이렇게 사용해야함
        ButterKnife.bind(this);

        introAdapter = new IntroPagerAdapter(getSupportFragmentManager());

        //어댑터에 프래그먼트들을 추가
//        introAdapter.add(IntroImageFragment.newInstance(R.mipmap.ic_launcher));
//        introAdapter.add(IntroImageFragment.newInstance(android.R.drawable.ic_delete));
//        introAdapter.add(IntroImageFragment.newInstance(android.R.drawable.ic_dialog_alert));
        introAdapter.add(IntroLastFragment.newInstance());

        introPager.setAdapter(introAdapter);
    }
}
