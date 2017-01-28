package kr.nexters.onepage.region;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.model.PageRepo;
import kr.nexters.onepage.main.adapter.MainAdapter;

public class RegionActivity extends BaseActivity {

    @BindView(R.id.pager_region)
    RecyclerViewPager recyclerViewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        mAdapter = new MainAdapter();
        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPager.setLayoutManager(linearLayout);
        recyclerViewPager.setAdapter(mAdapter);


        PageRepo
                .findPageRepoById(1, -2, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pageRepo -> {
                            mAdapter.add(pageRepo.getPages());
                        },
                        throwable -> toast(throwable.getLocalizedMessage()),
                        () -> {
                            recyclerViewPager.scrollToPosition(mAdapter.getFirstPagePostion());
                        }
                );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
