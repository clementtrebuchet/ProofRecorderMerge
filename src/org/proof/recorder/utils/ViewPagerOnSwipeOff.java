package org.proof.recorder.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerOnSwipeOff extends ViewPager {
	
	private boolean disabled;

	public ViewPagerOnSwipeOff(Context context) {
		super(context);
		this.disabled = false;
	}	

    public ViewPagerOnSwipeOff(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.disabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !this.disabled && super.onTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !this.disabled && super.onInterceptTouchEvent(event);

    }
 
    public void disablePaging(boolean enabled) {
        this.disabled = enabled;
    }
}
