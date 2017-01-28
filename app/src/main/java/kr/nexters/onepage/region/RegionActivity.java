package kr.nexters.onepage.region;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.main.PagerFragment;

import static kr.nexters.onepage.main.PagerFragment.KEY_LAST_LOCATION;

public class RegionActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        long locationId = getIntent().getLongExtra(KEY_LAST_LOCATION, -1L);
        if(locationId == -1L) {
            toast("잘못된 접근");
            finish();
        }

        replaceFragment(R.id.fragment_region, PagerFragment.newInstance(locationId));
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
