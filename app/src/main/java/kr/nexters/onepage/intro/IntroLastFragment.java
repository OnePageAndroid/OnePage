package kr.nexters.onepage.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.nexters.onepage.R;

/**
 * Created by ohjaehwan on 2017. 1. 5..
 */

public class IntroLastFragment extends Fragment {
    private static String IMAGE_RESOURCE = "image_resource";

    //버터나이프 사용
    @BindView(R.id.ivIntro)
    ImageView ivIntro;

    @BindView(R.id.btnMain)
    Button btnMain;

    //프래그먼트 생성 팩토리 메서드
    public static IntroLastFragment newInstance() {
        //프래그먼트를 생성하고 번들에 이미지 리소스를 담아서 아규먼트를 세팅한다.
        IntroLastFragment fragment = new IntroLastFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트 사용을 위한 뷰 인플레이트
        View view = inflater.inflate(R.layout.fragment_intro_last, container, false);

        //프래그먼트에서 버터나이프를 사용하려면 onCreateView에서 이렇게 사용해야함
        ButterKnife.bind(this, view);

        Glide.with(this)
                .load(android.R.drawable.ic_dialog_info)
                .into(ivIntro);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }
}