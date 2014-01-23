package edu.uark.spARK.activity;

import java.sql.Timestamp;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import edu.uark.spARK.R;
import edu.uark.spARK.arrayadapter.NavListArrayAdapter;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.dialog.CustomDialogBuilder;
import edu.uark.spARK.entity.*;
import edu.uark.spARK.fragment.CheckInFragment;
import edu.uark.spARK.fragment.GroupFragment;
import edu.uark.spARK.fragment.MapViewFragment;
import edu.uark.spARK.fragment.MyProfileFragment;
import edu.uark.spARK.fragment.NewsFeedFragment;
import edu.uark.spARK.view.SelectiveViewPager;

public class MainActivity extends Activity implements AsyncResponse {
	
	// TODO: Need to create a Current/myUser Class for these variables
	public static int myUserID;
	public static String myUsername;
	public static List<Group> myGroups = new ArrayList();
	public static String myFullName;
	public static String myDesc;
	
    private static final int PROFILE_FRAGMENT = 0;
    private static final int NEWSFEED_FRAGMENT = 1;
    private static final int CHECKIN_FRAGMENT = 2;
    private static final int CREATE_CONTENT_ACTIVITY = 3;

    private int page = -1;


    private DrawerLayout mDrawerLayout;
    private Handler mHandler = new Handler();	//using this for smoother drawer animation
    private NavListArrayAdapter mNavListArrayAdapter;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mListTitles;	//option titles
    private Menu mMenu;

    private JSONArray MyContents = null;
    
	public static MapViewFragment mMapViewFragment;
	public static NewsFeedFragment mDiscussionFragment;
	public static NewsFeedFragment mBulletinFragment;
	private static Fragment f;
	
	public MyAdapter mAdapter;

	public static int mOldDrawerPosition;	//default value
	public static int mNewDrawerPosition;	//default value
	public static SelectiveViewPager mPager;
	
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);
        
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
        myUsername = preferences.getString("currentUsername", "");
        
        JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_GET_MY_CONTENT, myUsername);
        
        mTitle = mDrawerTitle = getTitle();
        mListTitles = getResources().getStringArray(R.array.nav_drawer_title_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        // set a new custom arrayadapter
        mNavListArrayAdapter = new NavListArrayAdapter(getApplicationContext(), R.layout.drawer_list_item);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(mNavListArrayAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    	mOldDrawerPosition=1;	//default value
    	mNewDrawerPosition=1;	//default value
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(true);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(new MainDrawerListener());
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

    	if (savedInstanceState == null) {
            mMapViewFragment= new MapViewFragment() {
        		@Override
        		public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        			final View v = super.onCreateView(inflater, container, savedInstanceState);
        			final float scale = this.getResources().getDisplayMetrics().density;
        		    final int pixels = (int) (100 * scale + 0.5f);
        		    final ViewTreeObserver observer = v.getViewTreeObserver();
        		    observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        		        @Override
        		        public void onGlobalLayout() {
        		            v.setY((int) (-v.getHeight()/2 + pixels/2));
        		            v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        		            //this will be called as the layout is finished, prior to displaying.
        		        }
        		    });
        		    return v;
        		}
        		
        		@Override
        		public void onConfigurationChanged(Configuration newConfig) {
        		    super.onConfigurationChanged(newConfig);
        		    resetView();
        		  }
        	};
    		getFragmentManager().beginTransaction().add(R.id.map_frame, mMapViewFragment).commit();
    	}

        mPager = (SelectiveViewPager) findViewById(R.id.pager);

        mAdapter = new MyAdapter(getFragmentManager());
        mPager.setAdapter(mAdapter);
        
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

  			@Override
  			public void onPageScrollStateChanged(int state) { }

  			@Override
  			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

  			@Override
  			public void onPageSelected(int position) {
  				getActionBar().getTabAt(position).select();
  			}
        	
        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            
            @Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
				mMapViewFragment.updateMarkers(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			
			}
        	
        };
        ActionBar.Tab discussionTab = bar.newTab().setText("Discussions");
        ActionBar.Tab bulletinTab = bar.newTab().setText("Bulletins");
        discussionTab.setTabListener(tabListener);
        bulletinTab.setTabListener(tabListener);
        bar.addTab(discussionTab);
        bar.addTab(bulletinTab);     
	}
    public static class MyAdapter extends FragmentPagerAdapter {
        	
    	private FragmentManager mFragmentManager;
		public MyAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public int getCount() {
        	return 2;
        }
        
        @Override
        public NewsFeedFragment getItem(int position) {
        	if (position == 0) {
        		mDiscussionFragment = NewsFeedFragment.newInstance(position);
        		return mDiscussionFragment;
        	} else if (position == 1) {
        		mBulletinFragment = NewsFeedFragment.newInstance(position);
        		return mBulletinFragment;
        	}
			return null;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    	SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    	searchView.setQueryHint("Search for discussions and bulletins...");
    	searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //configure search info, add listeners
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.post:
        	Intent intent = new Intent(getApplicationContext(), CreateContentActivity.class);
        	try {
        		intent.putExtra("contentType", getActionBar().getSelectedTab().getText());
        	} catch (Exception e) {
        		intent.putExtra("contentType", "Groups");
        	}
        	try {
	        	Location loc = mMapViewFragment.getLocationClient().getLastLocation();
	        	if (loc != null) {
	     	    	intent.putExtra("latitude", String.valueOf(loc.getLatitude()));
	            	intent.putExtra("longitude", String.valueOf(loc.getLongitude()));
	     	    }
        	} catch (Exception e) { }
        	
        	startActivityForResult(intent, CREATE_CONTENT_ACTIVITY);
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
    	
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mNewDrawerPosition = position;
            mDrawerList.setItemChecked(position, true);
            view.setSelected(true);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        
    }
    
    private class MainDrawerListener implements DrawerLayout.DrawerListener {
    	private int mDrawerState;
//    	public MainDrawerListener() {
//    		mDrawerState = DrawerLayout.STATE_IDLE;
//    	}
		@Override
		public void onDrawerClosed(View drawerView) {
			mDrawerToggle.onDrawerClosed(drawerView);
			if (mNewDrawerPosition != -1) {
				selectItem(mNewDrawerPosition);
			}
		}

		@Override
		public void onDrawerOpened(View drawerView) {
			mDrawerToggle.onDrawerOpened(drawerView);
			mNewDrawerPosition = -1;
		}

		@Override
		public void onDrawerSlide(View drawerView, float offset) {
			mDrawerToggle.onDrawerSlide(drawerView, offset);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			mDrawerState = newState;
			mDrawerToggle.onDrawerStateChanged(mDrawerState);
		}

    }

    private void selectItem(final int position) {  	
    		
	        FragmentManager fragmentManager = getFragmentManager(); 
	        switch(position) {
	    	
	    	case 0:  	
	            fragmentManager.beginTransaction().detach(mMapViewFragment)
	            .replace(R.id.fragment_frame, new MyProfileFragment(), "My Profile")
	            .addToBackStack("Profile").commit();
	            mPager.setVisibility(View.GONE);
	            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	            mDrawerToggle.setDrawerIndicatorEnabled(false);
	            break;
	    	case 1:
	    		if (mNewDrawerPosition != mOldDrawerPosition) {
		            fragmentManager.beginTransaction().attach(mMapViewFragment)
	    				.detach(f).commit();
		            mPager.setVisibility(View.VISIBLE);
		            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		            mOldDrawerPosition = mNewDrawerPosition;
	    		}
	            break;
	        case 2:
	        	if (mNewDrawerPosition != mOldDrawerPosition) {
		        	f = new CheckInFragment();
		            fragmentManager.beginTransaction().detach(mMapViewFragment)
		            .replace(R.id.fragment_frame, f, "Check In").commit(); 
		            mPager.setVisibility(View.GONE);
		            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		            mOldDrawerPosition = mNewDrawerPosition;
	        	}
	            break;
	            
	        case 3:
	        	//groups
	        	if (mNewDrawerPosition != mOldDrawerPosition) {
		        	f = new GroupFragment();
		            fragmentManager.beginTransaction().detach(mMapViewFragment)
		            .replace(R.id.fragment_frame, f, "Groups").commit();
		            mPager.setVisibility(View.GONE);
		            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		            mOldDrawerPosition = mNewDrawerPosition;
	        	}
	            break;
	        case 4:
	        	//bookmarks
	        	if (mNewDrawerPosition != mOldDrawerPosition) {
		        	f = new Fragment();
		            fragmentManager.beginTransaction().detach(mMapViewFragment)
		            .replace(R.id.fragment_frame, f, "Bookmarks").commit();
		            mPager.setVisibility(View.GONE);
		            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		            mOldDrawerPosition = mNewDrawerPosition;
	        	}
	            break;
	        case 5:
	        	//settings we could make another activity/fragment/whatever
	        	if (mNewDrawerPosition != mOldDrawerPosition) {
		        	f = new Fragment();
		            fragmentManager.beginTransaction().detach(mMapViewFragment)
		            .replace(R.id.fragment_frame, f, "Settings").commit();
		            mPager.setVisibility(View.GONE);
		            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		            mOldDrawerPosition = mNewDrawerPosition;
	        	}
	        	break;
	        case 6:
	        	//dialog
	            new DialogFragment() {
	            	@Override
	            	public Dialog onCreateDialog(Bundle savedInstanceState) {
						
	            		return new CustomDialogBuilder(MainActivity.this)
	                    .setIcon(R.drawable.ic_menu_about)
	                    .setTitle("About spark")
	                    .setTitleColor(getResources().getColor(R.color.red))
	                    .setDividerColor(getResources().getColor(R.color.red))
	                    .setMessage("Created by: \n\nAlex Adamec \nBrenna Blackwell \nMatt McClelland \nJD Pack \nChandramohan Sol")
	                    .setPositiveButton("Send Feedback", new DialogInterface.OnClickListener() {
	                       @Override
	                       public void onClick(DialogInterface dialog, int id) {

	                       }
	                    })
	                    .setNegativeButton("Help", new DialogInterface.OnClickListener() {
	                       @Override
	                       public void onClick(DialogInterface dialog, int id) {

	                       }
	                    })
	                   .create();
	            	}
	            }.show(getFragmentManager(), "About");
	        	break;
	        case 8:
	            SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
	            SharedPreferences.Editor editor = preferences.edit();
	            //editor.remove("currentUsername");
	            editor.remove("currentPassword");
	            editor.remove("autoLogin");
	            editor.commit();
	            Intent backToLogin = new Intent(MainActivity.this, LogInActivity.class).putExtra("Logout", true);
	            startActivity(backToLogin);
	            finish();        	   
	        default:
	        	//nothing should happen here
	        	break;
	        }
    }
    

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanButton:
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                TextView txtScanResult = (TextView)findViewById(R.id.txtScanResult);
                txtScanResult.setText(contents);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        } else if (requestCode == CREATE_CONTENT_ACTIVITY && resultCode == RESULT_OK) {
        	if(intent.hasExtra("bulletin")) {
        		Bulletin bulletin = (Bulletin) intent.getSerializableExtra("bulletin");
        		int groupSelected = intent.getIntExtra("groupSelected", 0);
        		mBulletinFragment.arrayListContent.add(0, bulletin);
        		if(bulletin.hasLocation()) {
        			mMapViewFragment.addContent(bulletin, getActionBar().getSelectedTab().getPosition() == 1);
        		}
        		
        		JSONQuery jquery = new JSONQuery(this);
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Bulletin", 
						Integer.toString(bulletin.getCreator().getId()), 
						bulletin.getTitle(), bulletin.getText(), 
						Integer.toString(groupSelected),
						bulletin.getLatitude(), bulletin.getLongitude());
				mBulletinFragment.getListAdapter().notifyDataSetChanged();
        	} else if (intent.hasExtra("discussion")) {
        		Discussion discussion = (Discussion) intent.getSerializableExtra("discussion");
        		int groupSelected = intent.getIntExtra("groupSelected", 0);
        		mDiscussionFragment.arrayListContent.add(0, discussion);
        		if(discussion.hasLocation()) {
        			mMapViewFragment.addContent(discussion, getActionBar().getSelectedTab().getPosition() == 0);
        		}
        		
        		JSONQuery jquery = new JSONQuery(this);
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Discussion", 
						Integer.toString(discussion.getCreator().getId()), 
						discussion.getTitle(), discussion.getText(), 
						Integer.toString(groupSelected), 
						discussion.getLatitude(), discussion.getLongitude());
				mDiscussionFragment.getListAdapter().notifyDataSetChanged();
        	} else if (intent.hasExtra("group")) {
        		Group group = (Group) intent.getSerializableExtra("group");
				JSONQuery jquery = new JSONQuery(this);
				if(group.hasLocation()) {
					mMapViewFragment.addContent(group, false);
				}
				
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Group", 
						Integer.toString(group.getCreator().getId()), 
						group.getTitle(), group.getDescription(), 
						(group.isOpen() ? "Open" : "Closed"), 
						(group.isVisible() ? "Visible" : "Hidden"),
						group.getLatitude(), group.getLongitude());
        	}
        }
    	JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_GET_MY_CONTENT, myUsername);
		
    }

    @Override
	public void processFinish(JSONObject result) {
		try { 
			myGroups = new ArrayList<Group>();
			int success = result.getInt(ServerUtil.TAG_SUCCESS);
			if (success == 1) {
				MyContents = result.getJSONArray(ServerUtil.TAG_GROUPS);
				myUserID = result.getInt(ServerUtil.TAG_USER_ID);
				myFullName = result.getString(ServerUtil.TAG_USER_FULL_NAME);
				myDesc = result.getString(ServerUtil.TAG_USER_DESC);
				
				for (int i = 0; i < MyContents.length(); i++) {
					JSONObject content = MyContents.getJSONObject(i);

					int groupID = Integer.parseInt(content.getString(ServerUtil.TAG_ID));
					String groupName = content.getString(ServerUtil.TAG_TITLE).trim();
					String groupDesc = content.getString(ServerUtil.TAG_BODY).trim();
					String groupPrivacy = content.getString(ServerUtil.TAG_PRIVACY).trim();
					String groupVisibility = content.getString(ServerUtil.TAG_VISIBILITY).trim();
					boolean open = true;
					boolean visible = true; 
					if (groupPrivacy.contains("Closed")) {
						open = false;
					}
					if (groupVisibility.contains("Hidden")) {
						visible = false;
					}
					
					Group g = new Group(groupID, groupName, groupDesc, open, visible);
					myGroups.add(g);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		FragmentManager fm = getFragmentManager();
		if (fm.getBackStackEntryCount() != 0) {
			getDrawerToggle().setDrawerIndicatorEnabled(true);
			FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1);
			if (backEntry.getName() == "Map") {
				mMapViewFragment.zoomOutMap();
				mPager.setPaging(true);
			}
			else if (backEntry.getName() == "Profile") {
    			getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    			mPager.setVisibility(View.VISIBLE);
    			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

			}
			super.onBackPressed();
		}
		else
			finish();
	}
	
	public ActionBarDrawerToggle getDrawerToggle() {
		return this.mDrawerToggle;
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}
	
	public void updateMapMarkers() {
		mMapViewFragment.updateMarkers(getActionBar().getSelectedTab().getPosition());
	}

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query)
    {
    	JSONQuery jquery = new JSONQuery(MainActivity.this);
        jquery.execute(ServerUtil.URL_SEARCH, Integer.toString(MainActivity.myUserID), query);
    }
	
}
