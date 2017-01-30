package kr.nexters.onepage.mypage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;

public class MyPageActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private final static int MY_PAGE = 1;
    private final static int BOOK_MARK = 2;

    @BindView(R.id.tab_mypage)
    TabLayout myPageTabLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    List<TabLayout.Tab> tabs = Lists.newArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        ButterKnife.bind(this);

        initActionBar();
        initTab();
        navigateMyPage();
    }

    private void navigateMyPage() {
        UserPagerFragment pagerFragment = UserPagerFragment.newInstance();
//        pagerFragment.setOnLongClickPageListener(() -> appbarLayout.setExpanded(false, true));
        replaceFragment(R.id.fragment_main, pagerFragment);
    }

    private void navigateMark() {
        MarkPagerFragment pagerFragment = MarkPagerFragment.newInstance();
//        pagerFragment.setOnLongClickPageListener(() -> appbarLayout.setExpanded(false, true));
        replaceFragment(R.id.fragment_main, pagerFragment);
    }

    private void initTab() {
        tabs = ImmutableList.of(
                myPageTabLayout.newTab().setTag(MY_PAGE).setText("마이페이지"),
                myPageTabLayout.newTab().setTag(BOOK_MARK).setText("북마크"));
        for(TabLayout.Tab tab : tabs) {
            myPageTabLayout.addTab(tab);
        }
        myPageTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        myPageTabLayout.addOnTabSelectedListener(this);
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tabIdx = (int) tab.getTag();
        switch (tabIdx) {
            case MY_PAGE :
                navigateMyPage();
                break;
            case BOOK_MARK :
                navigateMark();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
