package edu.uark.spARK;

import java.util.ArrayList;
import java.util.List;

import org.json.*;

import android.app.*;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.entities.*;

public class MainActivity extends Activity implements AsyncResponse{
	
	public static int UserID;
	public static String Username;
	public static List<Group> myGroups = new ArrayList();
	
    private static final int PROFILE_FRAGMENT = 0;
    private static final int NEWSFEED_FRAGMENT = 1;
    private static final int CHECKIN_FRAGMENT = 2;
    private static final int CREATE_CONTENT_ACTIVITY = 3;

    private int page = -1;


    private DrawerLayout mDrawerLayout;
    private NavListArrayAdapter mNavListArrayAdapter;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mListTitles;	//option titles
    private Menu mMenu;

    private JSONArray MyContents = null;
    
	static MapView_Fragment mMapViewFragment;
	
	public MyAdapter mAdapter;
	public static SelectiveViewPager mPager;
	
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);
        
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
        Username = preferences.getString("currentUsername", "");

        JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_GET_MY_CONTENT, Username);
        
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
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            //selectItem(0);
        }

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mMapViewFragment = new MapView_Fragment();
        getFragmentManager().beginTransaction().add(R.id.map_frame, mMapViewFragment).commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_frame, new Fragment());
        
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
				// TODO Auto-generated method stub
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
				mMapViewFragment.updateMarkers(tab.getText().toString());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
        	
        };
        
      final ActionBar.Tab discussionTab = bar.newTab().setText("Discussions");
      ActionBar.Tab bulletinTab = bar.newTab().setText("Bulletins");
      discussionTab.setTabListener(tabListener);
      bulletinTab.setTabListener(tabListener);
      bar.addTab(discussionTab);
      bar.addTab(bulletinTab);
    }
	
    public static class MyAdapter extends FragmentPagerAdapter {
       
    	
    	public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
        	return 2;
        }

        @Override
        public NewsFeed_Fragment getItem(int position) {
        	return NewsFeed_Fragment.newInstance(position);
        }

    }  	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
        	intent.putExtra("contentType", getActionBar().getSelectedTab().getText());
        	intent.putExtra("user", getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE).getString("currentUsername", ""));
        	intent.putExtra("rank", "Total N00B!"); // TODO: Get user info from database on login and store in global User variable
        	
        	Location loc = mMapViewFragment.getLocationClient().getLastLocation();
        	intent.putExtra("latitude", String.valueOf(loc.getLatitude()));
        	intent.putExtra("longitude", String.valueOf(loc.getLongitude()));
        	startActivityForResult(intent, CREATE_CONTENT_ACTIVITY);
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	selectItem(position);
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
            view.setSelected(true);
        }
    }

    private void selectItem(int position) {
    	
        FragmentManager fragmentManager = getFragmentManager();
    	
        switch(position) {
    	
    	case 0:  		
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new MyProfile_Fragment()).commit();
            mPager.setVisibility(View.GONE);
            break;
    	case 1:
            fragmentManager.beginTransaction().attach(mMapViewFragment)
            .replace(R.id.fragment_frame, new Fragment()).commit();
            mPager.setVisibility(View.VISIBLE);
            break;
        case 2:
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new CheckIn_Fragment()).commit(); 
            mPager.setVisibility(View.GONE);
            break;
            
        case 3:
        	//groups
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new Fragment()).commit();
            mPager.setVisibility(View.GONE);
            break;
        case 4:
        	//bookmarks
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new Fragment()).commit();
            mPager.setVisibility(View.GONE);
            break;
        case 5:
        	//settings we could make another activity/fragment/whatever
        	break;
        case 6:
        	//dialog
        	break;
        case 7:
            SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            //editor.remove("currentUsername");
            editor.remove("currentPassword");
            editor.remove("autoLogin");
            editor.commit();
            Intent backToLogin = new Intent(this, LogInActivity.class).putExtra("Logout", true);
            startActivity(backToLogin);
            finish();        	   
        default:
        	//nothing should happen here
        	break;
        }
        	
    }
    
//    private void switchFragment(Fragment fragmentName) {
//        Bundle args = new Bundle();
//	    args.putInt(ContentFragment.ARG_FRAGMENT_TYPE, fragmentName);
//        fragment.setArguments(args);
//    
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.fragment_frame, switchFragment())
//        .remove(mMapViewFragment)
//        .commit();    	
//    }

//    private void switchFragment(int fragmentName){
//        // update the main content by replacing fragments
//    	
//    	//if the desired fragment isn't the same as the already loaded page
//    	if (fragmentName != page){
//	    	Fragment fragment;
//	    	//int fragmentName is statically declared above
//	    	switch (fragmentName){
//	    		case PROFILE_FRAGMENT: //0
//	    			fragment = new MyProfile_Fragment();
//	    			break;
//		    	case NEWSFEED_FRAGMENT://1
//		    		fragment = new HybridFragment();
//		    		break;
//		    	case CLUSTERVIEW_FRAGMENT: //2
//	    			fragment = new ClusterView_Fragment();
//		    		break;
//		    	case CHECKIN_FRAGMENT: //2
//	    			fragment = new CheckIn_Fragment();
//		    		break;
//		    	case MAPVIEW_FRAGMENT: //0
//		    		fragment = new MapView_Fragment();
//		    		break;
////		    	case NEWSFEED_FRAGMENT: //5
////		    		fragment = new ListFeed_Fragment();
////		    		break;
//		    	default:
//		    		//(currently blank)
//		    		fragment = new ContentFragment();
//		    		break;
//	    	}
//	
//	        Bundle args = new Bundle();
//		    args.putInt(ContentFragment.ARG_FRAGMENT_TYPE, fragmentName);
//	        fragment.setArguments(args);
//        
//            FragmentManager fragmentManager = getFragmentManager();
//            
//            return fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment)
//
//            mDrawerList.setItemChecked(fragmentName, true);
//            //setTitle(mListTitles[position]);
//
//            page = fragmentName;
//
//        }
//        mDrawerLayout.closeDrawer(mDrawerList);
//    }

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
        		//mListBulletinFragment.arrayListContent.add(0, bulletin);
        		mAdapter.getItem(1).arrayListContent.add(0, bulletin);
        		if(bulletin.hasLocation()) {
        			mMapViewFragment.addContent(bulletin, getActionBar().getSelectedTab().getPosition() == 1);
        		}
        		
        		JSONQuery jquery = new JSONQuery(this);
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Bulletin", Integer.toString(bulletin.getCreator().getId()), bulletin.getTitle(), bulletin.getText(), Integer.toString(groupSelected));
        	} else if (intent.hasExtra("discussion")) {
        		Discussion discussion = (Discussion) intent.getSerializableExtra("discussion");
        		int groupSelected = intent.getIntExtra("groupSelected", 0);
        		//mListDiscussionFragment.arrayListContent.add(0, discussion);
        		mAdapter.getItem(0).arrayListContent.add(0, discussion);
        		if(discussion.hasLocation()) {
        			mMapViewFragment.addContent(discussion, getActionBar().getSelectedTab().getPosition() == 0);
        		}
        		
        		JSONQuery jquery = new JSONQuery(this);
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Discussion", Integer.toString(discussion.getCreator().getId()), discussion.getTitle(), discussion.getText(), Integer.toString(groupSelected));
        	} else if (intent.hasExtra("group")) {
        		Group group = (Group) intent.getSerializableExtra("group");
				JSONQuery jquery = new JSONQuery(this);
				if(group.hasLocation()) {
					mMapViewFragment.addContent(group, false);
				}
				
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Group", Integer.toString(group.getCreator().getId()), group.getTitle(), group.getDescription(), (group.isOpen() ? "Open" : "Closed"), (group.isVisible() ? "Visible" : "Hidden"));		
        	}
        }
    	JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_GET_MY_CONTENT, Username);
    }

    @Override
	public void processFinish(JSONObject result) {
		try { 
			myGroups = new ArrayList<Group>();
			int success = result.getInt(ServerUtil.TAG_SUCCESS);
			if (success == 1) {
				MyContents = result.getJSONArray(ServerUtil.TAG_GROUPS);
				UserID = result.getInt(ServerUtil.TAG_USER_ID);
				
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
		//make sure there are no fragments in backstack
		if(getFragmentManager().getBackStackEntryCount() == 0) {
			finish();
	    }
		else if (getFragmentManager().getBackStackEntryAt(0).getName().compareTo("Map") == 0) {
			mMapViewFragment.zoomOutMap();
			super.onBackPressed();
		}
		else {
			super.onBackPressed();
		}
	}
}
