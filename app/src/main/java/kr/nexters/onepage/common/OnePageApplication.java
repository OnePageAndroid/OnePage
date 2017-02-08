package kr.nexters.onepage.common;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import kr.nexters.onepage.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by OhJaeHwan on 2017-01-12.
 */

public class OnePageApplication extends MultiDexApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static Context getContext() {
        return mContext;
    }
}
