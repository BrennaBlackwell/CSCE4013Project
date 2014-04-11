package edu.uark.spARK.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.prediction.PredictionScopes;

import edu.uark.spARK.R;
import edu.uark.spARK.arrayadapter.NavListArrayAdapter;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.entity.Bulletin;
import edu.uark.spARK.entity.Discussion;
import edu.uark.spARK.entity.Event;
import edu.uark.spARK.entity.Group;
import edu.uark.spARK.fragment.ContentFragment;
import edu.uark.spARK.fragment.GroupFragment;
import edu.uark.spARK.fragment.HomeFragment;
import edu.uark.spARK.fragment.MapViewFragment;
import edu.uark.spARK.fragment.MyProfileFragment;

public class MainActivity extends FragmentActivity implements AsyncResponse {
	//auth2.0 Prediction API authentication variables
	static final String APPLICATION_NAME = "csce.uark.edu-spark/1.0";
	private static final String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/prediction";
	static final String PREF_AUTH_TOKEN = "authToken";
	static final String PREF_ACCOUNT_NAME = "accountName";	
	static GoogleCredential credential = new GoogleCredential();
	String accountName;
	SharedPreferences preferences;
	GoogleAccountManager accountManager;
	private static final int REQUEST_AUTHENTICATE = 11;

	
	// TODO: Need to create a Current/myUser Class for these variables
	public static int myUserID;
	public static String myUsername;
	public static List<Group> myGroups = new ArrayList();
	public static String myFullName;
	public static String myDesc;
	public static Bitmap myProfilePicture;
    private JSONArray MyContents = null;

    //Navigation menus
    private DrawerLayout mNavDrawerLayout;
    private MainDrawerListener mNavDrawerListener;
    private NavListArrayAdapter mNavListArrayAdapter;
    private ListView mNavDrawerList;
    private ListView mNotificationList;
    private ActionBarDrawerToggle mNavDrawerToggle;
    private CharSequence mTitle;
    private String[] mNavDrawerTitles;	//option titles
    private Menu mMenu;

	private final int CREATE_CONTENT_ACTIVITY = 3;   
	private MapViewFragment mMapViewFragment;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
		myUsername = preferences.getString("currentUsername", "");
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_GET_MY_CONTENT, myUsername);
		
		//initialize Google account
		//UNCOMMENT next two lines if you want to select different Google account for prediction
		//You won't be able to see the consent screen again if you've already hit accept before, it's stored locally in a db on the phone
		//preferences.edit().remove(PREF_ACCOUNT_NAME).commit();
		//preferences.edit().remove(PREF_AUTH_TOKEN).commit();
		accountName = preferences.getString(PREF_ACCOUNT_NAME, null);
	    //Logger.getLogger("com.google.api.client").setLevel(Level.OFF);
	    accountManager = new GoogleAccountManager(this);
	    //try account token
	    gotAccount(savedInstanceState);

		mTitle = getTitle();
		mNavDrawerTitles = getResources().getStringArray(R.array.nav_drawer_title_array);
		mNavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavDrawerList = (ListView) findViewById(R.id.left_drawer);
		mNotificationList = (ListView) findViewById(R.id.right_drawer);
		TextView tvTest = new TextView(this);
		tvTest.setText("No new notifications");
		tvTest.setTextColor(Color.BLACK);
		mNotificationList.addHeaderView(tvTest);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		// set a new custom arrayadapter
		mNavListArrayAdapter = new NavListArrayAdapter(getApplicationContext(), R.layout.drawer_list_item);
		// set up the drawer's list view with items and click listener
		mNavDrawerList.setAdapter(mNavListArrayAdapter);
		mNavDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mNotificationList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] {"Test", "Test 2"}));
		// enable ActionBar app icon to behave as action to toggle nav drawer
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		bar.setDisplayShowTitleEnabled(true);
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mNavDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mNavDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
										// onPrepareOptionsMenu()
				mNavDrawerToggle.syncState();
			}

			public void onDrawerOpened(View view) {
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
				mNavDrawerToggle.syncState();
			}
		};
		
		mNavDrawerListener = new MainDrawerListener();
		mNavDrawerLayout.setDrawerListener(mNavDrawerListener);
		
		mNavDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		//initialize content to home
		selectItem(1);
		
			// TODO: Place the map initialization code somewhere after things have been loaded from the server, or just find a way to hide it until data appears
			//this won't really be a problem now since you can't see the map behind the home screen
			mMapViewFragment = new MapViewFragment() {
				@Override
				public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
					final View v = super.onCreateView(inflater, container, savedInstanceState);
					final float scale = this.getResources().getDisplayMetrics().density;
					final int pixels = (int) (100 * scale + 0.5f);
					final ViewTreeObserver observer = v.getViewTreeObserver();
					observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							//v.setY((int) (-v.getHeight() / 2 + pixels / 2));
							//v.animate().y((-v.getHeight() / 2 + pixels / 2)).setDuration(250);
							//v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
							// this will be called as the layout is finished,
							// prior to displaying.
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
			getSupportFragmentManager().beginTransaction().add(R.id.map_frame, mMapViewFragment).commit();   
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    	SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    	searchView.setBackgroundResource(R.drawable.searchview_background);
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        searchView.findViewById(searchPlateId).setBackgroundColor(Color.TRANSPARENT);
    	searchView.setBaselineAligned(true);
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
        if (mNavDrawerToggle.onOptionsItemSelected(item)) {
        	if (mNavDrawerLayout.isDrawerOpen(mNotificationList))
        		mNavDrawerLayout.closeDrawer(mNotificationList);
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
        	
        	startActivityForResult(intent, CREATE_CONTENT_ACTIVITY );
        	break;
        case R.id.notification:
        	if (mNavDrawerLayout.isDrawerOpen((mNotificationList)))
        		mNavDrawerLayout.closeDrawer(mNotificationList);
        	else {
        		mNavDrawerLayout.openDrawer(mNotificationList);
            	if (mNavDrawerLayout.isDrawerOpen(mNavDrawerList))
            		mNavDrawerLayout.closeDrawer(mNavDrawerList);
        	}
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
    	
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mNavDrawerListener.newPos = position;
            mNavDrawerList.setItemChecked(position, true);
            view.setSelected(true);
            mNavDrawerListener.wasItemSelected = true;
            mNavDrawerLayout.closeDrawer(mNavDrawerList);
        }
        
    }
    
    private class MainDrawerListener implements DrawerLayout.DrawerListener {
    	private int mDrawerState;
    	public boolean wasItemSelected;
    	public int oldPos, newPos;
    	public MainDrawerListener() {
    		//initialize start position to home
    		oldPos = 1;
    	}
		@Override
		public void onDrawerClosed(View drawerView) {
			mNavDrawerToggle.onDrawerClosed(drawerView);
        	if (wasItemSelected && (oldPos != newPos)) {
        		//case 0 exists because we're choosing the profile to be added to back stack
        		//case 6 is the settings activity
        		if (newPos != 0 && newPos != 6) {
        			oldPos = newPos;
        		}
        		selectItem(newPos);
        	}

		}

		@Override
		public void onDrawerOpened(View drawerView) {
			mNavDrawerToggle.onDrawerOpened(drawerView);
			wasItemSelected = false;
		}

		@Override
		public void onDrawerSlide(View drawerView, float offset) {
			mNavDrawerToggle.onDrawerSlide(drawerView, offset);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			mDrawerState = newState;
			mNavDrawerToggle.onDrawerStateChanged(mDrawerState);
		}

    }

    private void selectItem(final int position) {  	
	        FragmentManager fm = getSupportFragmentManager();
	        switch(position) {    	
	    	case 0:  	
	    		//Profile fragment
	            fm.beginTransaction().detach(mMapViewFragment)
	            .replace(R.id.fragment_frame, new MyProfileFragment(), "My Profile")
	            .addToBackStack("Profile").commit();
	            mNavDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	            mNavDrawerToggle.setDrawerIndicatorEnabled(false);
	    		setTitle(mNavDrawerTitles[position]);
	            break;
	    	case 1:
	    		//Home fragment
		            fm.beginTransaction()
		            .replace(R.id.fragment_frame, HomeFragment.newInstance(position), "Home").commit();
		    		setTitle(mNavDrawerTitles[position]);
	            break;
	        case 2:
	        	//Headlines fragment
		            fm.beginTransaction()
		            .replace(R.id.fragment_frame, ContentFragment.newInstance(position), "Headlines").commit(); 
		    		setTitle(mNavDrawerTitles[position]);
	            break;
	        case 3:
	        	//Events fragment
		            fm.beginTransaction()
		            .replace(R.id.fragment_frame, ContentFragment.newInstance(position), "Events").commit();
		    		setTitle(mNavDrawerTitles[position]);
	            break;
	        case 4:
	        	//Discussions fragment
		            fm.beginTransaction()
		            .replace(R.id.fragment_frame, ContentFragment.newInstance(position), "Discussions").commit();
		    		setTitle(mNavDrawerTitles[position]);
	            break;
	        case 5:
		    	//Groups fragment
		            fm.beginTransaction()
		            .replace(R.id.fragment_frame, new GroupFragment(), "Groups").commit();
		    		setTitle(mNavDrawerTitles[position]);
	        	break;
	        case 6:
	        	//Settings fragment
	        	//since we're using compatibility library, we have to use activity instead of fragment...grrr
	        	Intent intent = new Intent(this, SettingsActivity.class);
	        	startActivity(intent);
//	            new DialogFragment() {
//	            	@Override
//	            	public Dialog onCreateDialog(Bundle savedInstanceState) {
//						
//	            		return new CustomDialogBuilder(MainActivity.this)
//	                    .setIcon(R.drawable.ic_menu_about)
//	                    .setTitle("About spark")
//	                    .setTitleColor(getResources().getColor(R.color.red))
//	                    .setDividerColor(getResources().getColor(R.color.red))
//	                    .setMessage("Created by: \n\nAlex Adamec \nMatt McClelland \nJD Pack \nJaCarri Tolette")
//	                    .setPositiveButton("Send Feedback", new DialogInterface.OnClickListener() {
//	                       @Override
//	                       public void onClick(DialogInterface dialog, int id) {
//
//	                       }
//	                    })
//	                    .setNegativeButton("Help", new DialogInterface.OnClickListener() {
//	                       @Override
//	                       public void onClick(DialogInterface dialog, int id) {
//
//	                       }
//	                    })
//	                   .create();
//	            	}
//	            }.show(getFragmentManager(), "About");
	        	break;
	        case 7:
	            SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
	            SharedPreferences.Editor editor = preferences.edit();
	            //editor.remove("currentUsername");
	            editor.remove("currentPassword");
	            editor.remove("autoLogin");
	            editor.commit();
	            myProfilePicture = null;
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
        mNavDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mNavDrawerToggle.onConfigurationChanged(newConfig);
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

    //TODO: clean up return code, especially since bulletins don't exist anymore
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				TextView txtScanResult = (TextView) findViewById(R.id.txtScanResult);
				txtScanResult.setText(contents);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		} 
		else if (requestCode == REQUEST_AUTHENTICATE) {
	        if (resultCode == RESULT_OK) {
	            gotAccount(intent.getExtras());
	          } else {
	            chooseAccount();
	          }
		} 
		else if (requestCode == CREATE_CONTENT_ACTIVITY && resultCode == RESULT_OK) {
        	if(intent.hasExtra("bulletin")) {
        		Bulletin bulletin = (Bulletin) intent.getSerializableExtra("bulletin");
        		int groupSelected = intent.getIntExtra("groupSelected", 0);
        		//TODO: add new content locally
        		//mBulletinFragment.arrayListContent.add(0, bulletin);
        		if(bulletin.hasLocation()) {
        			mMapViewFragment.addContent(bulletin, getActionBar().getSelectedTab().getPosition() == 1);
        		}
        		
        		JSONQuery jquery = new JSONQuery(this);
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Bulletin", 
						Integer.toString(bulletin.getCreator().getId()), 
						bulletin.getTitle(), bulletin.getText(), 
						Integer.toString(groupSelected),
						bulletin.getLatitude(), bulletin.getLongitude());
				//mBulletinFragment.getListAdapter().notifyDataSetChanged();
        	} else if (intent.hasExtra("discussion")) {
        		Discussion discussion = (Discussion) intent.getSerializableExtra("discussion");
        		int groupSelected = intent.getIntExtra("groupSelected", 0);
        		//TODO: add new content locally
        		//mDiscussionFragment.arrayListContent.add(0, discussion);
        		if(discussion.hasLocation()) {
        			//mMapViewFragment.addContent(discussion, getActionBar().getSelectedTab().getPosition() == 0);
        		}
        		
        		JSONQuery jquery = new JSONQuery(this);
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Discussion", 
						Integer.toString(discussion.getCreator().getId()), 
						discussion.getTitle(), discussion.getText(), 
						Integer.toString(groupSelected), 
						discussion.getLatitude(), discussion.getLongitude());
				//mDiscussionFragment.getListAdapter().notifyDataSetChanged();
        	} else if (intent.hasExtra("group")) {
        		Group group = (Group) intent.getSerializableExtra("group");
				JSONQuery jquery = new JSONQuery(this);
				if(group.hasLocation()) {
					//mMapViewFragment.addContent(group, false);
				}
				
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Group", 
						Integer.toString(group.getCreator().getId()), 
						group.getTitle(), group.getDescription(), 
						(group.isOpen() ? "Open" : "Closed"), 
						(group.isVisible() ? "Visible" : "Hidden"),
						group.getLatitude(), group.getLongitude());
        	} else if (intent.hasExtra("event")) {
        		Event event = (Event) intent.getSerializableExtra("event");
        		int groupSelected = intent.getIntExtra("groupSelected", 0);
        		//TODO: add new content locally
        		//mDiscussionFragment.arrayListContent.add(0, event);
        		if(event.hasLocation()) {
        			//mMapViewFragment.addContent(event, getActionBar().getSelectedTab().getPosition() == 0);
        		}
        		
        		JSONQuery jquery = new JSONQuery(this);
				jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Event", 
						Integer.toString(event.getCreator().getId()), 
						event.getTitle(), event.getText(), 
						Integer.toString(groupSelected),
						event.getLatitude(), event.getLongitude(), event.getLocation(),
						event.getStartDate(), event.getStartTime(),
						event.getEndDate(), event.getEndTime());
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
				myFullName = result.getString(ServerUtil.TAG_USER_FULL_NAME).trim();
				myDesc = result.getString(ServerUtil.TAG_USER_DESC).trim();
				String base64Image = result.getString(ServerUtil.TAG_USER_PIC).trim();
				if (!base64Image.isEmpty()) {
					byte[] rawImage = Base64.decode(base64Image, Base64.DEFAULT);
					myProfilePicture = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
				} else {
					myProfilePicture = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.drawer_profile);
				}
				mNavListArrayAdapter.notifyDataSetChanged();
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
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() != 0) {
			getDrawerToggle().setDrawerIndicatorEnabled(true);
			FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1);
			if (backEntry.getName() == "Map") {
				mMapViewFragment.zoomOutMap();
			}
			else if (backEntry.getName() == "Profile") {
    			mNavDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    			setTitle(mNavDrawerTitles[mNavDrawerListener.oldPos]);
			}
			super.onBackPressed();
		}
		else
			finish();
	}
	
	public ActionBarDrawerToggle getDrawerToggle() {
		return this.mNavDrawerToggle;
	}

	public DrawerLayout getDrawerLayout() {
		return mNavDrawerLayout;
	}
	
	//TODO: grab markers based on content being viewed
	public void updateMapMarkers() {
		//mMapViewFragment.updateMarkers(getActionBar().getSelectedTab().getPosition());
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
	
	void setAuthToken(String authToken) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_AUTH_TOKEN, authToken);
		editor.commit();
		credential.setAccessToken(authToken);
	}

	void setAccountName(String accountName) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		this.accountName = accountName;
	}

	void onAuthToken() {
		Set<String> scopes = new HashSet<String>();
		scopes.add(PredictionScopes.DEVSTORAGE_FULL_CONTROL);
		scopes.add(PredictionScopes.DEVSTORAGE_READ_ONLY);
		scopes.add(PredictionScopes.DEVSTORAGE_READ_WRITE);
		scopes.add(PredictionScopes.PREDICTION);
		GoogleAccountCredential accountCredential = GoogleAccountCredential.usingOAuth2(this, scopes);
		accountCredential.setSelectedAccountName(preferences.getString(PREF_ACCOUNT_NAME, null));
	}

	private void chooseAccount() {
		accountManager.getAccountManager().getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null, MainActivity.this, null, null, new AccountManagerCallback<Bundle>() {

			public void run(AccountManagerFuture<Bundle> future) {
				Bundle bundle;
				try {
					bundle = future.getResult();
					setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
					setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
					onAuthToken();
				} catch (OperationCanceledException e) {
					// user canceled
				} catch (AuthenticatorException e) {
					Log.e("Oauth", e.getMessage(), e);
				} catch (IOException e) {
					Log.e("Oauth", e.getMessage(), e);
				}
			}
		}, null);
		// TODO: Make layout look neat!
		// CustomDialogBuilder builder = new CustomDialogBuilder(this);
		// builder.setTitle("Choose an Account");
		// final Account[] accounts = accountManager.getAccountManager().getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		// final int size = accountManager.getAccounts().length;
		// String[] names = new String[size];
		// for (int i = 0; i < size; i++)
		// names[i] = accounts[i].name;
		// builder.setItems(names, new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// setAccountName(accounts[which].name);
		// //setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
		// //setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
		// onAuthToken();
		// }
		// }).show();
	}

	void gotAccount(Bundle b) {
		Account account = accountManager.getAccountByName(accountName);
		if (account == null) {
			chooseAccount();
			return;
		}
		if (credential.getAccessToken() != null) {
			onAuthToken();
			return;
		}
		accountManager.getAccountManager().getAuthToken(account, AUTH_TOKEN_TYPE, b, true, new AccountManagerCallback<Bundle>() {
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle bundle = future.getResult();
					if (bundle.containsKey(AccountManager.KEY_INTENT)) {
						Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
						intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivityForResult(intent, REQUEST_AUTHENTICATE);
					} else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
						setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
						onAuthToken();
					}
				} catch (Exception e) {
					Log.e("Oauth", e.getMessage(), e);
				}
			}
		}, null);
	}
}
