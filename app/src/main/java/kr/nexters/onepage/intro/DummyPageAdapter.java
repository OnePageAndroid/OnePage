package kr.nexters.onepage.intro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;
import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 2. 11..
 */

@Data
public class DummyPageAdapter extends RecyclerView.Adapter<DummyPageAdapter.DummyPageViewHolder> {


    @Override
    public DummyPageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_page, parent, false);
        return new DummyPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DummyPageViewHolder holder, int position) {
        int resId[] = {R.drawable.img_01, R.drawable.img_01, R.drawable.img_01};
        String resStrs[] = {"꺼지지 않는 광화문의 불빛", "꺼지지 않는 광화문의 불빛", "꺼지지 않는 광화문의 불빛"};

        Glide.with(holder.itemView.getContext())
                .load(resId[position])
                .into(holder.ivImg);

        holder.tvText.setText(resStrs[position]);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class DummyPageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_text)
        TextView tvText;
        @BindView(R.id.iv_image)
        ImageView ivImg;

        public DummyPageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
