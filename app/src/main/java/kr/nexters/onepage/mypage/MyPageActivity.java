package kr.nexters.onepage.mypage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.adapter.PageAdapter;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.util.Pageable;

public class MyPageActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private final static int MY_PAGE = 1;
    private final static int BOOK_MARK = 2;

    @BindView(R.id.tab_recycle_view_pager)
    RecyclerViewPager viewPager;

    @BindView(R.id.tab_mypage)
    TabLayout myPageTabLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    List<TabLayout.Tab> tabs = Lists.newArrayList();
    List<Page> items = Lists.newArrayList();
    PageAdapter pageAdapter = new PageAdapter();
    Pageable pageable = Pageable.of();

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
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(layout);

        MyPageAPI.Factory.findPageByUser(PropertyManager.getKeyId(), pageable.getPageNumber(), pageable.getPerPageSize(), pages -> items.addAll(pages));
        for(int resId : resIds) {
            items.add(Page.of(resId, "" + resId));
        }
        pageAdapter.add(items);
        viewPager.setAdapter(pageAdapter);
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
                pageable.initPage();
//                pageAdapter.add(MyPageAPI.Factory.findPageByUser(PropertyManager.getKeyId(),
//                        pageable.getPageNumber(), pageable.getPerPageSize()));
                break;
            case BOOK_MARK :
                pageable.initPage();
//                pageAdapter.add(MyPageAPI.Factory.findPageByHeart(PropertyManager.getKeyId(),
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
