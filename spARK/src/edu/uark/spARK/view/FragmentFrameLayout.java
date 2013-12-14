package edu.uark.spARK.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FragmentFrameLayout extends FrameLayout {

	public FragmentFrameLayout(Context context) {
		super(context);
	}
	
	public FragmentFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
    public FragmentFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public float getXFraction() {
        return getX() / getWidth();
    }

    public void setXFraction(float xFraction) {
        final int width = getWidth();
        setX((width > 0) ? (xFraction * width) : Integer.MIN_VALUE);
    }
    
	public float getYFraction() {
        return getY() / getHeight();
    }

    public void setYFraction(float yFraction) {
        final int height = getHeight();
        setY((height > 0) ? (yFraction * height) : Integer.MIN_VALUE);
    }

}
