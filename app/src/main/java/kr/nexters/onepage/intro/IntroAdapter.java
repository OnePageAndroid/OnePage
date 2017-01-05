package kr.nexters.onepage.intro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ohjaehwan on 2017. 1. 5..
 */

class IntroAdapter extends FragmentPagerAdapter {

    //프래그먼트들을 저장할 리스트
    private List<Fragment> fragmentList = new ArrayList<>();

    IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    //프래그 먼트를 리스트에 추가
    public void add(Fragment fragment) {
        fragmentList.add(fragment);
    }


    //포지션에 해당하는 페이져의 프래그먼트가 사용된다.
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
