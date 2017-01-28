package kr.nexters.onepage.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

/**
 * Created by ohjaehwan on 2017. 1. 28..
 */

public class AppbarAnimUtil {
    private static final float PERCENTAGE_TO_SHOW_CONTENT_AT_TOOLBAR = 0.5f;
    private static final int ALPHA_ANIMATIONS_DURATION = 500;

    private boolean isCollapseLayoutVisible = false;


    private AppbarAnimUtil() {
    }

    private static class InstanceHolder {
        private static final AppbarAnimUtil INSTANCE = new AppbarAnimUtil();
    }

    public static AppbarAnimUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void handleAppbar(ViewGroup layoutCollapse, ViewGroup layoutExpand, float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_CONTENT_AT_TOOLBAR) {
            if (!isCollapseLayoutVisible) {
                startAlphaAnimation(layoutCollapse, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(layoutExpand, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isCollapseLayoutVisible = true;
            }

        } else {
            if (isCollapseLayoutVisible) {
                startAlphaAnimation(layoutCollapse, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startAlphaAnimation(layoutExpand, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isCollapseLayoutVisible = false;
            }
        }
    }

    public void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
