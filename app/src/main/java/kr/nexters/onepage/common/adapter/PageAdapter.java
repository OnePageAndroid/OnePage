package kr.nexters.onepage.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.collect.Lists;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.util.ConvertUtil;

/**
 * Created by ohjaehwan on 2017. 1. 28..
 */

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private List<Page> pages = Lists.newArrayList();

    private int totalPageSize;

    public PageAdapter(int totalPageSize) {
        this.totalPageSize = totalPageSize;
    }

    public interface OnLongClickPageViewHolderListener {
        void onLongClick();
    }

    private OnLongClickPageViewHolderListener onLongClickPageViewHolderListener;

    public void setOnLongClickPageViewHolderListener(OnLongClickPageViewHolderListener onLongClickPageViewHolderListener) {
        this.onLongClickPageViewHolderListener = onLongClickPageViewHolderListener;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        holder.bind(pages.get(position));
    }

    public void add(List<Page> items) {
        this.pages.addAll(items);
        notifyDataSetChanged();
    }

    public void add(int position, List<Page> items) {
        this.pages.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public int getTotalPageSize() {
        return totalPageSize;
    }

    public int getFirstPagePostion() {
        for (int i = pages.size() - 1; i >= 0; i--) {
            if (pages.get(i).getPageIndex() == 0) {
                return i;
            }
        }
        return 0;
    }

    private int getFirstPageNum() {
        if (pages.size() <= 0) {
            return 0;
        }
        return pages.get(0).getPageIndex();
    }

    private int getLastPageNum() {
        if (pages.size() <= 0) {
            return 0;
        }
        return pages.get(pages.size() - 1).getPageIndex();
    }

    public int getLoadPageNum(boolean isReverse) {
        if (isReverse) {
            return getFirstPageNum() - 5;
        }
        return getLastPageNum() + 1;
    }


    @Override
    public int getItemCount() {
        return pages.size();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_text)
        TextView tvText;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_page_current)
        TextView tvPageCurrent;
        @BindView(R.id.tv_page_total)
        TextView tvPageTotal;
        @BindView(R.id.iv_image)
        ImageView ivImg;
        @BindView(R.id.iv_mark)
        ImageView ivMark;
        @BindView(R.id.layout_text)
        FrameLayout layoutText;

        Page page;
        boolean isMarked;

        public PageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (onLongClickPageViewHolderListener != null) {
                itemView.setOnClickListener(v -> onLongClickPageViewHolderListener.onLongClick());
            }
        }

        public void bind(Page page) {
            this.page = page;
            tvText.setText(ConvertUtil.replaceSpace(page.getContent()));
            tvPageCurrent.setText(ConvertUtil.integerToCommaString(page.getPageNum()));
            tvPageTotal.setText(ConvertUtil.integerToCommaString(totalPageSize));
            tvDate.setText(page.getCreatedAt().getDateString());
            if (!page.getFirstImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(page.getFirstImageUrl())
                        .placeholder(R.drawable.loading_card_img)
                        .into(ivImg);
                ivImg.setVisibility(View.VISIBLE);
                layoutText.setPadding(0, ConvertUtil.dipToPixels(itemView.getContext(), 8), 0, 0);
            } else {
                ivImg.setVisibility(View.GONE);
                layoutText.setPadding(0, ConvertUtil.dipToPixels(itemView.getContext(), 15), 0, 0);
            }

            NetworkManager.getInstance().getApi()
                    .getBookmark(page.getPageId(), PropertyManager.getInstance().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::setBookmark,
                            throwable -> Log.e("getBookmark", throwable.getLocalizedMessage())
                    );
        }

        private void setBookmark(boolean isMarked) {
            this.isMarked = isMarked;

            Glide.with(itemView.getContext())
                    .load(isMarked ? R.drawable.bookmark_after : R.drawable.bookmark)
                    .into(ivMark);
        }

        @OnClick(R.id.iv_mark)
        public void onClickMark() {
            NetworkManager.getInstance().getApi()
                    .saveBookmark(page.getPageId(), PropertyManager.getInstance().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            serverResponse -> {
                                if (serverResponse.isSuccess()) {
                                    isMarked = !isMarked;
                                    setBookmark(isMarked);
                                }
                            }, throwable -> Log.e("saveBookmark", throwable.getLocalizedMessage())
                    );
        }
    }
}

