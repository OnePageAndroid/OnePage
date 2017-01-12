package kr.nexters.onepage.common;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

/**
 * Created by OhJaeHwan on 2017-01-12.
 */

public class OnePageApplication extends MultiDexApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
