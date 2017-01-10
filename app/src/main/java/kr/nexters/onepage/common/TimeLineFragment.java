package kr.nexters.onepage.common;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.model.TimeLine;


public class TimeLineFragment extends Fragment {

    private static String IMAGE_RESOURCE = "image_resource";

    //버터나이프 사용
    @BindView(R.id.iv_image)
    ImageView ivImage;

    @BindView(R.id.tv_text)
    TextView tvText;

    Unbinder unbinder;

    private TimeLine timeLine;

    //프래그먼트 생성 팩토리 메서드
    public static TimeLineFragment newInstance(TimeLine timeLine) {
        TimeLineFragment fragment = new TimeLineFragment();
        fragment.setTimeLine(timeLine);
        return fragment;
    }

    public void setTimeLine(TimeLine timeLine) {
        this.timeLine = timeLine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트 사용을 위한 뷰 인플레이트
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //프래그먼트에서 버터나이프를 사용하려면 onCreateView에서 이렇게 사용해야함
        unbinder = ButterKnife.bind(this, view);

        tvText.setText(timeLine.getText());

        //이미지로딩 라이브러리
        Glide.with(this)
                .load(timeLine.getImgRes())
                .into(ivImage);


        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
