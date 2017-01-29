package kr.nexters.onepage.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.model.Page;

/**
 * Created by ohjaehwan on 2017. 1. 28..
 */

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

    List<Page> items = new ArrayList<>();

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
        holder.bind(items.get(position));
    }

    public void add(List<Page> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void add(int position, List<Page> items) {
        this.items.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public Page getPage(int position) {
        return items.get(position);
    }

    public int getFirstPagePostion() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getPageNum() == 0) {
                return i;
            }
        }
        return 0;
    }

    private int getFirstPageNum() {
        if (items.size() <= 0) {
            return 0;
        }
        return items.get(0).getPageNum();
    }

    private int getLastPageNum() {
        if (items.size() <= 0) {
            return 0;
        }
        return items.get(items.size() - 1).getPageNum();
    }

    public int getLoadPageNum(boolean isReverse) {
        if (isReverse) {
            return getFirstPageNum() - 5;
        }
        return getLastPageNum() + 1;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_text)
        TextView tvText;
        @BindView(R.id.tv_page)
        TextView tvPage;
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
            tvPage.setText(item.getPageNum() + "page");
            Glide.with(itemView.getContext())
                    .load(item.getFirstImageUrl())
                    .into(ivImg);
        }
    }
}

