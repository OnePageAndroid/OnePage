package kr.nexters.onepage.mypage.bookmark;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseFragment;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.mypage.MyPageService;
import kr.nexters.onepage.mypage.user.UserPageAdapter;

public class BookMarkPagerFragment extends BaseFragment {
    private final static int PAGE_SIZE = 5;

    @BindView(R.id.pager_main)
    RecyclerViewPager mainPager;

    private MyPageService myPageService = new MyPageService();

    private UserPageAdapter mainAdapter;
    private Unbinder unbinder;

    private boolean loading = false;
    private OnLongClickPageListener onLongClickPageListener;

    interface OnLongClickPageListener {
        void onLongClick();
    }

    public void setOnLongClickPageListener(OnLongClickPageListener onLongClickPageListener) {
        this.onLongClickPageListener = onLongClickPageListener;
    }

    public static BookMarkPagerFragment newInstance() {
        BookMarkPagerFragment fragment = new BookMarkPagerFragment();
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
        getFirstPages(PAGE_SIZE);
        return view;
    }

    private void initPager(List<Page> pages) {
        mainAdapter = new UserPageAdapter(pages.size());
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
        mainAdapter.add(pages);
    }

    private void getPages(int perPageSize, boolean isReverse) {
        loading = true;
        myPageService.findPageByHeart(PropertyManager.getKeyId(), mainAdapter.getLoadPageNum(isReverse), perPageSize, (pages) -> {
            loading = false;
            if (isReverse) {
                mainAdapter.add(0, pages);
                return;
            }
            mainAdapter.add(pages);
        });
    }

    private void getFirstPages(int perPageSize) {
        //첫번째 페이지가 중앙에 와야되서 첫 페이지를 -2로 가져옴
        myPageService.findPageByHeart(PropertyManager.getKeyId(), -2, perPageSize, (pages) -> {
            initPager(pages);
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
