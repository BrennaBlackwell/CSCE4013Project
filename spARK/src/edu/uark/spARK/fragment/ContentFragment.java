package edu.uark.spARK.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import edu.uark.spARK.R;
import edu.uark.spARK.activity.MainActivity;
import edu.uark.spARK.view.SelectiveViewPager;

@SuppressLint("NewApi")
public class ContentFragment extends Fragment {
	private SelectiveViewPager mPager;
	private MyAdapter mAdapter;
	
    public static ContentFragment newInstance(int num) {
    	ContentFragment f = new ContentFragment();

        // Supply num input as an argument (0 for discussion fragment, 1 for bulletin).
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.fragment_content, container, false);
        mPager = (SelectiveViewPager) v.findViewById(R.id.pager);
        PagerTabStrip mPagerTabStrip = (PagerTabStrip) v.findViewById(R.id.pager_tab_strip);
        mPagerTabStrip.setDrawFullUnderline(true);
        mPagerTabStrip.setTabIndicatorColorResource(R.color.tabstrip);
        mAdapter = new MyAdapter(getChildFragmentManager());   
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(1);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

  			@Override
  			public void onPageScrollStateChanged(int state) { }

  			@Override
  			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

  			@Override
  			public void onPageSelected(int position) {

  			}
        	
        });  

        return v;
    }
    
    public static class MyAdapter extends FragmentPagerAdapter {
    	private static final String[] titles = { "Recommended", "Recent", "Nearby", "Popular", "Favorites"};
    	private FragmentManager mFragmentManager;
    	public MyAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public int getCount() {
        	return 5;
        }
        
        @Override
        public NewsFeedFragment getItem(int position) {
        	return NewsFeedFragment.newInstance(position);
        }     
        
        @Override
        public CharSequence getPageTitle(int position) {
        	return titles[position];
        }
        
        public NewsFeedFragment getActiveFragment(ViewPager container, int position) {
        	String name = makeFragmentName(container.getId(), position);
        		return  (NewsFeedFragment) mFragmentManager.findFragmentByTag(name);
        	}

        	private static String makeFragmentName(int viewId, int index) {
        	    return "android:switcher:" + viewId + ":" + index;
        	}
    }  	
    
    @Override
    public void onResume() {
    	super.onResume();

    }
    
    public MyAdapter getAdapter() {
    	return mAdapter;
    }
}
