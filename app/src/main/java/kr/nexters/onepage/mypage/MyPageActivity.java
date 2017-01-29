package kr.nexters.onepage.mypage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.InfinitePagerAdapter;
import kr.nexters.onepage.common.InfiniteViewPager;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.PageAdapter;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.util.Pageable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private final static int MY_PAGE = 1;
    private final static int BOOK_MARK = 2;

    @BindView(R.id.pager_mypage)
    InfiniteViewPager myPagePager;

    @BindView(R.id.tab_mypage)
    TabLayout myPageTabLayout;

    List<TabLayout.Tab> tabs;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    List<Page> items = Lists.newArrayList();
    PageAdapter myPageAdapter;
    InfinitePagerAdapter wrappedAdapter;

    Pageable pageable;

    int resIds[] = {R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_delete, android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_dialer, android.R.drawable.ic_dialog_email};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        ButterKnife.bind(this);

        initActionBar();
        initTab();
        initPager();
    }

    private void initPager() {
        pageable = Pageable.of();
        myPageAdapter = new PageAdapter(getSupportFragmentManager());

        MyPageAPI.Factory.findPageByUser(PropertyManager.getKeyId(),
                pageable.getPageNumber(), pageable.getPerPageSize()).enqueue(new Callback<List<Page>>() {
            @Override
            public void onResponse(Call<List<Page>> call, Response<List<Page>> response) {
                items.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<Page>> call, Throwable t) {
                Log.e("initPager : " + t.getMessage(), t.getMessage());
            }
        });
        myPageAdapter.add(items);
        wrappedAdapter = new InfinitePagerAdapter(myPageAdapter);
        myPagePager.setAdapter(wrappedAdapter);
    }

    private void initTab() {
        tabs = ImmutableList.of(
                myPageTabLayout.newTab().setTag(MY_PAGE).setText("마이페이지"),
                myPageTabLayout.newTab().setTag(BOOK_MARK).setText("북마크"));
        for(TabLayout.Tab tab : tabs) {
            myPageTabLayout.addTab(tab);
        }
        myPageTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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
                pageable.initPage();
                myPageAdapter.clear();
//                myPageAdapter.add(MyPageAPI.Factory.findPageByUser(PropertyManager.getKeyId(),
//                        pageable.getPageNumber(), pageable.getPerPageSize()));
                break;
            case BOOK_MARK :
                pageable.initPage();
                myPageAdapter.clear();
//                myPageAdapter.add(MyPageAPI.Factory.findPageByHeart(PropertyManager.getKeyId(),
//                        pageable.getPageNumber(), pageable.getPerPageSize()));
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
