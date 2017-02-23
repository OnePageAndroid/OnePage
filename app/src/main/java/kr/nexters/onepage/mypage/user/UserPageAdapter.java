package kr.nexters.onepage.mypage.user;

import android.content.Context;
import android.support.v7.app.AlertDialog;
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
import kr.nexters.onepage.common.BusProvider;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.main.model.PageRefreshEvent;
import kr.nexters.onepage.util.ConvertUtil;


public class UserPageAdapter extends RecyclerView.Adapter<UserPageAdapter.PageViewHolder> {
    private List<Page> pages = Lists.newArrayList();

    private int totalPageSize;

    public UserPageAdapter(int totalPageSize) {
        this.totalPageSize = totalPageSize;
    }

    interface OnDeleteClickListener {
        void clickDelete();
    }

    private OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_page_mypage, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        holder.bind(pages.get(position));
    }

    public int getTotalPageSize() {
        return totalPageSize;
    }

    public void add(List<Page> items) {
        this.pages.addAll(items);
        notifyDataSetChanged();
    }

    public void add(int position, List<Page> items) {
        this.pages.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public void clear() {
        pages.clear();
        notifyDataSetChanged();
    }

    public Page getPage(int position) {
        return pages.get(position);
    }

    public int getFirstPagePostion() {
        for (int i = 0; i < pages.size(); i++) {
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
        @BindView(R.id.tv_page_current)
        TextView tvPageCurrent;
        @BindView(R.id.tv_page_total)
        TextView tvPageTotal;
        @BindView(R.id.iv_image)
        ImageView ivImg;
        @BindView(R.id.iv_delete)
        ImageView ivMark;
        @BindView(R.id.layout_text)
        FrameLayout layoutText;
        @BindView(R.id.tv_location)
        TextView tvLocation;

        Page page;

        public PageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Page page) {
            this.page = page;
            tvText.setText(page.getContent());
            tvLocation.setText(page.getLocationName());
            tvPageCurrent.setText(ConvertUtil.integerToCommaString(page.getPageNum()));
            tvPageTotal.setText(ConvertUtil.integerToCommaString(totalPageSize));

            if (!page.getFirstImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(page.getFirstImageUrl())
                        .placeholder(R.drawable.loading_card_img)
                        .into(ivImg);
                ivImg.setVisibility(View.VISIBLE);
                layoutText.setPadding(0, ConvertUtil.dipToPixels(itemView.getContext(), 10), 0, 0);
            } else {
                ivImg.setVisibility(View.GONE);
                layoutText.setPadding(0, ConvertUtil.dipToPixels(itemView.getContext(), 15), 0, 0);
            }
        }

        @OnClick(R.id.iv_delete)
        public void onClickDelete() {

            Context context = itemView.getContext();

            new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.mypage_delete))
                    .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
                        NetworkManager.getInstance().getApi().deletePageById(page.getPageId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        serverResponse -> {
                                            if (serverResponse.isSuccess()) {
                                                if(onDeleteClickListener != null) {
                                                    onDeleteClickListener.clickDelete();
                                                    BusProvider.post(new PageRefreshEvent());
                                                }
                                            }
                                        }, throwable -> Log.e("deletePage", throwable.getLocalizedMessage())
                                );
                    })
                    .setNegativeButton(context.getString(R.string.cancel), null)
                    .show();
        }
    }
}

