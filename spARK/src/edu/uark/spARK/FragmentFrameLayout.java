package edu.uark.spARK;

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
		// TODO Auto-generated constructor stub
	}

	public float getXFraction() {
        return getX() / getWidth(); // TODO: guard divide-by-zero
    }

    public void setXFraction(float xFraction) {
        // TODO: cache width
        final int width = getWidth();
        setX((width > 0) ? (xFraction * width) : -9999);
    }

}
