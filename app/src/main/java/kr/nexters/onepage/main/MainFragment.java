package kr.nexters.onepage.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import kr.nexters.onepage.R;

/**
 * Created by ohjaehwan on 2017. 1. 9..
 */

public class MainFragment extends Fragment {


    //프래그먼트 생성 팩토리 메서드
    public static MainFragment newInstance() {
        //프래그먼트를 생성하고 번들에 이미지 리소스를 담아서 아규먼트를 세팅한다.
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //프래그먼트에서 버터나이프를 사용하려면 onCreateView에서 이렇게 사용해야함
        ButterKnife.bind(this, view);

        return view;
    }
}
