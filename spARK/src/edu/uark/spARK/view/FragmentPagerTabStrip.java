package edu.uark.spARK.view;

import java.text.AttributedCharacterIterator.Attribute;

import android.content.Context;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

public class FragmentPagerTabStrip extends PagerTabStrip {

	public FragmentPagerTabStrip(Context context) {
		super(context);
	}

	public FragmentPagerTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public float getXFraction() {
        return getX() / getWidth();
    }

    public void setXFraction(float xFraction) {
        final int width = getWidth();
        setX((width > 0) ? (xFraction * width) : Integer.MIN_VALUE);
    }
    
	public float getYFraction() {
        return -(getY() / getHeight());
    }

    public void setYFraction(float yFraction) {
        final int height = getHeight();
        setY((height > 0) ? (-yFraction * height) : Integer.MIN_VALUE);
    }
}
