package kr.nexters.onepage.intro;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

/**
 * Created by hoody on 2017-02-14.
 */

public class DisabledRecyclerViewPager extends RecyclerViewPager {

    private boolean isPagingEnabled = true;

    public DisabledRecyclerViewPager(Context context) {
        super(context);
    }

    public DisabledRecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisabledRecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}
