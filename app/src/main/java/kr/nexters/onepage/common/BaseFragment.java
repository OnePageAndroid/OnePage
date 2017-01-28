package kr.nexters.onepage.common;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by ohjaehwan on 2017. 1. 28..
 */

public class BaseFragment extends Fragment {
    protected void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
