package edu.uark.spARK.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SelectiveViewPager extends ViewPager {
	private boolean isPaging = true;

	public SelectiveViewPager(Context context) {
	    super(context);
	}

	public SelectiveViewPager(Context context, AttributeSet attributeSet){
	    super(context, attributeSet);
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isPaging) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPaging) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
    
	public void setPaging(boolean p) { 
		isPaging = p; 
	}
}
