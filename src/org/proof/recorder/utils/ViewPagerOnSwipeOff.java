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
        if (!this.disabled) {
            return super.onTouchEvent(event);
        }
  
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!this.disabled) {
            return super.onInterceptTouchEvent(event);
        }
 
        return false;
    }
 
    public void disablePaging(boolean enabled) {
        this.disabled = enabled;
    }
}
