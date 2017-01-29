package kr.nexters.onepage.main;


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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseFragment;
import kr.nexters.onepage.common.model.PageRepo;
import kr.nexters.onepage.main.adapter.PageAdapter;

public class PagerFragment extends BaseFragment {

    @BindView(R.id.pager_main)
    RecyclerViewPager mainPager;

    PageAdapter mainAdapter;

    Unbinder unbinder;

    long lastLocationId;

    int PAGE_SIZE = 5;
    boolean loading = false;

    public static final String KEY_LAST_LOCATION = "key_last_location";

    private final CompositeDisposable disposables = new CompositeDisposable();

    OnLongClickPageListener onLongClickPageListener;

    interface OnLongClickPageListener {
        void onLongClick();
    }

    public void setOnLongClickPageListener(OnLongClickPageListener onLongClickPageListener) {
        this.onLongClickPageListener = onLongClickPageListener;
    }

    public static PagerFragment newInstance(long lastLocationId) {
        PagerFragment fragment = new PagerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_LAST_LOCATION, lastLocationId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        unbinder = ButterKnife.bind(this, view);
        lastLocationId = getArguments().getLong(KEY_LAST_LOCATION, -1L);
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
                getPages(lastLocationId, PAGE_SIZE, false);
            } else if (!loading && curPosition <= 1) {
                getPages(lastLocationId, PAGE_SIZE, true);
            }
        });

        if(onLongClickPageListener != null) {
            mainAdapter.setOnLongClickPageViewHolderListener(() -> onLongClickPageListener.onLongClick());
        }

        getFirstPages(lastLocationId, PAGE_SIZE);
    }

    private void getPages(long locationId, int perPageSize, boolean isReverse) {
        loading = true;
        disposables.add(PageRepo
                .findPageRepoById(locationId, mainAdapter.getLoadPageNum(isReverse), perPageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pageRepo -> {
                            Log.d("PageRepo", pageRepo.getPages().toString());
                            if (isReverse) {
                                mainAdapter.add(0, pageRepo.getPages());
                            } else {
                                mainAdapter.add(pageRepo.getPages());
                            }
                            loading = false;
                        },
                        throwable -> toast(throwable.getLocalizedMessage())
                ));
    }

    private void getFirstPages(long locationId, int perPageSize) {
        //첫번째 페이지가 중앙에 와야되서 첫 페이지를 -2로 가져옴
        disposables.add(PageRepo
                .findPageRepoById(locationId, -2, perPageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pageRepo -> {
                            mainAdapter.add(pageRepo.getPages());
                            Log.d("PageRepo", pageRepo.getPages().toString());
                        },
                        throwable -> toast(throwable.getLocalizedMessage()),
                        () -> {                            //뷰페이저의 가운데가 첫번쨰 페이지로 오게 세팅
                            if (mainAdapter.getItemCount() > 0) {
                                mainPager.scrollToPosition(mainAdapter.getFirstPagePostion());
                            }
                        }
                ));
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        disposables.clear();
        super.onDestroyView();
    }
}
