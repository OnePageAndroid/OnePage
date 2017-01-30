package kr.nexters.onepage.mypage;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseFragment;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.adapter.PageAdapter;

public class UserPagerFragment extends BaseFragment {
    @BindView(R.id.pager_main)
    RecyclerViewPager mainPager;

    PageAdapter mainAdapter;
    Unbinder unbinder;

    int PAGE_SIZE = 5;
    boolean loading = false;

    OnLongClickPageListener onLongClickPageListener;

    interface OnLongClickPageListener {
        void onLongClick();
    }

    public void setOnLongClickPageListener(OnLongClickPageListener onLongClickPageListener) {
        this.onLongClickPageListener = onLongClickPageListener;
    }

    public static UserPagerFragment newInstance() {
        UserPagerFragment fragment = new UserPagerFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        unbinder = ButterKnife.bind(this, view);
        initPager();
        return view;
    }

    private void initPager() {
        mainAdapter = new PageAdapter();
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mainPager.setLayoutManager(linearLayout);
        mainPager.setAdapter(mainAdapter);

        mainPager.addOnPageChangedListener((prePosotion, curPosition) -> {
            if (!loading && curPosition >= mainAdapter.getItemCount() - 2) {
                getPages(PAGE_SIZE, false);
            } else if (!loading && curPosition <= 1) {
                getPages(PAGE_SIZE, true);
            }
        });

        if(onLongClickPageListener != null) {
            mainAdapter.setOnLongClickPageViewHolderListener(() -> onLongClickPageListener.onLongClick());
        }

        getFirstPages(PAGE_SIZE);
    }

    private void getPages(int perPageSize, boolean isReverse) {
        loading = true;
        MyPageAPI.Factory.findPageByUser(PropertyManager.getKeyId(), mainAdapter.getLoadPageNum(isReverse), perPageSize, (pages) -> {
            if (isReverse) {
                mainAdapter.add(0, pages);
                return;
            }
            mainAdapter.add(pages);
        });
    }

    private void getFirstPages(int perPageSize) {
        //첫번째 페이지가 중앙에 와야되서 첫 페이지를 -2로 가져옴
        MyPageAPI.Factory.findPageByUser(PropertyManager.getKeyId(), -2, perPageSize, (pages) -> {
            mainAdapter.add(pages);
            Log.d("PageRepo", pages.toString());
            if (mainAdapter.getItemCount() > 0) {
                mainPager.scrollToPosition(mainAdapter.getFirstPagePostion());
            }
        });
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
