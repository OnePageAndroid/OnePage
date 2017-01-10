package kr.nexters.onepage.intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;


public class MainFragment extends Fragment {

    private static String IMAGE_RESOURCE = "image_resource";

    //버터나이프 사용
    @BindView(R.id.iv_image)
    ImageView ivImage;

    //프래그먼트 생성 팩토리 메서드
    public static MainFragment newInstance(int imgRes) {
        //프래그먼트를 생성하고 번들에 이미지 리소스를 담아서 아규먼트를 세팅한다.
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IMAGE_RESOURCE, imgRes);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트 사용을 위한 뷰 인플레이트
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //프래그먼트에서 버터나이프를 사용하려면 onCreateView에서 이렇게 사용해야함
        ButterKnife.bind(this, view);

        //아규먼트에 담긴 이미지 리소스를 가져온다.
        Bundle bundle = getArguments();
        int imageRes = bundle.getInt(IMAGE_RESOURCE);

        //이미지로딩 라이브러리
        Glide.with(this)
                .load(imageRes)
                .into(ivImage);


        return view;
    }

}
