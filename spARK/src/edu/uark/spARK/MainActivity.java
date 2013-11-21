package edu.uark.spARK;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;

public class MainActivity extends Activity {
	
    private static final int PROFILE_FRAGMENT = 0;
    private static final int NEWSFEED_FRAGMENT = 1;
    private static final int MAPVIEW_FRAGMENT = -1;
    private static final int CLUSTERVIEW_FRAGMENT = -2;
    private static final int CHECKIN_FRAGMENT = 2;

    private int page = -1;
    
    
    private DrawerLayout mDrawerLayout;
    private NavListArrayAdapter mNavListArrayAdapter;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mListTitles;	//option titles
    private Menu mMenu;

    
    
    //in order to replace the main fragment with our other fragments (Bulletin/Discussion) this is the best way to go.
    //the mapview fragment is already in the activity_main.xml, and it's referencing our custom MapFragment
    // the new NestedFragments doesn't work < API 17, and putting our two fragments within another fragments results in bad performance
    //I also tried leaving the list as just a list and not a fragment, but drawing performance seems to not be as good
    
    //Our references to the two main fragments and map fragment
	static NewsFeed_Fragment mListDiscussionFragment;
	static NewsFeed_Fragment mListBulletinFragment;
	static MapView_Fragment mMapViewFragment;

	
	//???
//    private static ArrayList<Discussion> mDiscussionArrayList = new ArrayList<Discussion>(); 
//	private static ListFeedArrayAdapter mDiscussionAdapter;
//	private static ArrayList<Bulletin> mBulletinArrayList = new ArrayList<Bulletin>();
//	private static ListFeedArrayAdapter mBulletinAdapter;

	
	
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);   
        
        mTitle = mDrawerTitle = getTitle();
        mListTitles = getResources().getStringArray(R.array.nav_drawer_title_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

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
        
        mMapViewFragment = new MapView_Fragment();
        mListDiscussionFragment = new NewsFeed_Fragment();
        mListBulletinFragment = new NewsFeed_Fragment();
        ActionBar.Tab tabA = bar.newTab().setText("Discussions");
        ActionBar.Tab tabB = bar.newTab().setText("Bulletins");
        tabA.setTabListener(new MyTabListener(mListDiscussionFragment));
        tabB.setTabListener(new MyTabListener(mListBulletinFragment));
        bar.addTab(tabA);
        bar.addTab(tabB);

        getFragmentManager().beginTransaction().add(R.id.map_frame, mMapViewFragment).commit();
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
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
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

        }
    }

    private void selectItem(int position) {
    	
        FragmentManager fragmentManager = getFragmentManager();

        switch(position) {
    	
    	case 0:
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new MyProfile_Fragment()).commit();
            break;
    	case 1:
    		//mListDiscussion by default, I'll try to fix this to the last selected content
            fragmentManager.beginTransaction().attach(mMapViewFragment)
            .replace(R.id.fragment_frame, mListDiscussionFragment).commit();
            break;
        case 2:
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new CheckIn_Fragment()).commit(); 
            break;
        case 3:
        	//groups
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new Fragment()).commit();
            break;
        case 4:
        	//bookmarks
            fragmentManager.beginTransaction().detach(mMapViewFragment)
            .replace(R.id.fragment_frame, new Fragment()).commit();
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
            editor.remove("currentUsername");
            editor.remove("currentPassword");
            editor.commit();
            Intent backToLogin = new Intent(this, LogInActivity.class);
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
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

	@Override
	public void onResume() {
		super.onResume();
		//Load content from the server
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	//TAB SWITCHER CLASS
	class MyTabListener implements ActionBar.TabListener {
		public Fragment fragment;

		public MyTabListener(Fragment fragment) {
		    this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		    //do what you want when tab is reselected, I do nothing
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
	          Toast.makeText(getApplicationContext(),
	                  "Fragment switch!", Toast.LENGTH_SHORT)
	                  .show();
//	          switch (tab.getPosition()) {
//	          case 0:
//		        	if (fragment == null) {
//		        		fragment = new NewsFeed_Fragment();
//		        		ft.add(R.id.fragment_frame, fragment);
//		        	}
//	        	  break;
//	          case 1:
//		        	if (fragment == null) {
//		        		fragment = new NewsFeed_Fragment();
//		        		ft.add(R.id.fragment_frame, fragment);
//		        	}	
//	        	  break;
//	          }

	        }

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		    if (fragment != null) {
		        // Detach the fragment, because another one is being attached
		    	if (tab.getPosition() == 0)
		    		ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right).replace(R.id.fragment_frame, fragment);
		    	else
		    		ft.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left).replace(R.id.fragment_frame, fragment);
		    }
		}
	}
	
	@Override
	public void onBackPressed() {
		//make sure there are no fragments in backstack
		if(getFragmentManager().getBackStackEntryCount() == 0) {
	    new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Exit spark")
	        .setMessage("Are you sure you want to exit?")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            finish();    
	        }

	    })
	    .setNegativeButton("No", null)
	    .show();
	    }
		else
			super.onBackPressed();
	}
}
