package kr.nexters.onepage.common;

import android.app.Application;
import android.content.Context;

/**
 * Created by OhJaeHwan on 2017-01-12.
 */

public class OnePageApplication extends Application {

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
