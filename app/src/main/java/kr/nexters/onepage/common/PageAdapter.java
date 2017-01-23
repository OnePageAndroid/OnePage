package kr.nexters.onepage.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import kr.nexters.onepage.common.model.Page;


public class PageAdapter extends FragmentPagerAdapter {

    private List<Page> items = new ArrayList<>();

    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(List<Page> pages) {
        items.addAll(pages);
    }

    //포지션에 해당하는 페이져의 프래그먼트가 사용된다.
    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(items.get(position % items.size()));
    }

    @Override
    public float getPageWidth(int position) {
        return 0.93f;
    }

    @Override
    public int getCount() {
        return items.size() * 3;
    }
}

