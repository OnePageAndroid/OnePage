package kr.nexters.onepage.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.util.ConvertUtil;

/**
 * Created by ohjaehwan on 2017. 1. 28..
 */

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private List<Page> pages = Lists.newArrayList();

    private List<Page> items = new ArrayList<>();
    private int totalPageSize;

    public PageAdapter() {
    }

    public PageAdapter(int totalPageSize) {
        this.totalPageSize = totalPageSize;
    }

    public interface OnLongClickPageViewHolderListener {
        void onLongClick();
    }

    OnLongClickPageViewHolderListener onLongClickPageViewHolderListener;

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

    public void clear() {
        pages.clear();
        notifyDataSetChanged();
    }

    public Page getPage(int position) {
        return pages.get(position);
    }

    public int getFirstPagePostion() {
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getPageNum() == 0) {
                return i;
            }
        }
        return 0;
    }

    private int getFirstPageNum() {
        if (pages.size() <= 0) {
            return 0;
        }
        return pages.get(0).getPageNum();
    }

    private int getLastPageNum() {
        if (pages.size() <= 0) {
            return 0;
        }
        return pages.get(pages.size() - 1).getPageNum();
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

        public PageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (onLongClickPageViewHolderListener != null) {
                itemView.setOnClickListener(v -> onLongClickPageViewHolderListener.onLongClick());
//                itemView.setOnLongClickListener(v -> {
//                    onLongClickPageViewHolderListener.onLongClick();
//                    return false;
//                });
            }
        }

        public void bind(Page item) {
            tvText.setText(item.getContent());
            tvPageCurrent.setText(ConvertUtil.integerToCommaString(item.getPageNum()));
            tvPageTotal.setText(ConvertUtil.integerToCommaString(totalPageSize));

            Glide.with(itemView.getContext())
                    .load(item.getFirstImageUrl())
                    .into(ivImg);
        }
    }
}

