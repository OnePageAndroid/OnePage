package kr.nexters.onepage.intro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ohjaehwan on 2017. 1. 5..
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {

    //프래그먼트들을 저장할 리스트
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> fragmentTitleList = new ArrayList<>();

    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    //프래그 먼트를 리스트에 추가
    public void add(Fragment fragment) {
        fragmentList.add(fragment);
    }
    public void add(String title) { fragmentTitleList.add(title); }

    //포지션에 해당하는 페이져의 프래그먼트가 사용된다.
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

}

