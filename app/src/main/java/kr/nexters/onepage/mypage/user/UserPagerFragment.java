package kr.nexters.onepage.mypage.user;


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
import kr.nexters.onepage.common.BusProvider;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.common.model.PageRepo;
import kr.nexters.onepage.main.model.PageRefreshEvent;
import kr.nexters.onepage.mypage.MyPageService;

public class UserPagerFragment extends BaseFragment {
    private final static int PAGE_SIZE = 5;

    @BindView(R.id.pager_main)
    RecyclerViewPager mainPager;

    private MyPageService myPageService = new MyPageService();

    private UserPageAdapter mainAdapter;
    private Unbinder unbinder;

    private boolean loading = false;

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
        getFirstPages(PAGE_SIZE);
        return view;
    }

    private void initPager(PageRepo pageRepo) {
        mainAdapter = new UserPageAdapter(pageRepo.getTotalSize());
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mainPager.setLayoutManager(linearLayout);
        mainPager.setAdapter(mainAdapter);

        mainPager.addOnPageChangedListener((prePosotion, curPosition) -> {
            if (mainAdapter.getTotalPageSize() > 3) {
                if (!loading && curPosition >= mainAdapter.getItemCount() - 2) {
                    getPages(PAGE_SIZE, false);
                } else if (!loading && curPosition <= 1) {
                    getPages(PAGE_SIZE, true);
                }
            }
        });

        mainAdapter.setOnDeleteClickListener(() -> {
            getFirstPages(PAGE_SIZE);
            BusProvider.post(new PageRefreshEvent());
        });

        mainAdapter.add(pageRepo.getPages());
    }

    private void getPages(int perPageSize, boolean isReverse) {
        loading = true;
        myPageService.findPageByUser(PropertyManager.getKeyId(), mainAdapter.getLoadPageNum(isReverse), perPageSize, (pageRepo) -> {
            List<Page> pages = pageRepo.getPages();
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
        myPageService.findPageByUser(PropertyManager.getKeyId(), -2, perPageSize, (pageRepo) -> {
            initPager(pageRepo);
            Log.d("PageRepo", pageRepo.toString());
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
