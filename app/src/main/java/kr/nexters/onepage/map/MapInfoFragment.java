package kr.nexters.onepage.map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.nexters.onepage.R;

/**
 * Created by hoody on 2017-01-25.
 */

public class MapInfoFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_info, container, false);
    }
}