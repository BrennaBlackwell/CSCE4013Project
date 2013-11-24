package edu.uark.spARK;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.Group;
import edu.uark.spARK.entities.User;

@SuppressLint("ValidFragment")
public class CreateContentActivity extends FragmentActivity implements OnNavigationListener, AsyncResponse {
	
	private String[] options = new String[]{ "BULLETIN", "DISCUSSION", "GROUP" };
	private Spinner discussionGroups, bulletinGroups;
//	SectionsPagerAdapter mSectionsPagerAdapter;
//	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_content);
		
		 // Set up the action bar to show a dropdown list.
	    final ActionBar actionBar = getActionBar();
	    actionBar.setDisplayShowTitleEnabled(false);
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

	    // Specify a SpinnerAdapter to populate the dropdown list.
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
	        android.R.layout.simple_spinner_item, android.R.id.text1,
	        options);

	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	    // Set up the dropdown list navigation in the action bar.
	    actionBar.setListNavigationCallbacks(adapter, this);

	    /*
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.create_content_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		*/
	    String contentType = getIntent().getStringExtra("contentType");
	    for(int i=0; i < options.length; i++) {
	    	if(contentType.equalsIgnoreCase(options[i])) {
	    		actionBar.setSelectedNavigationItem(i);
	    		break;
	    	}
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_content, menu);
		return true;
	}

	/*
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new Fragment();
			if(position == 0) {
				fragment = new NewBulletinFragment();
			} else if (position == 1) {
				fragment = new NewDiscussionFragment();
			} else if (position == 2) {
				fragment = new NewGroupFragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return options.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if(position >= 0 && position < options.length) {
				return options[position];
			} else {
				return null;
			}
		}
	}
	*/

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Fragment fragment = new Fragment();
		if(position == 0) {
			fragment = new NewBulletinFragment();
		} else if (position == 1) {
			fragment = new NewDiscussionFragment();
		} else if (position == 2) {
			fragment = new NewGroupFragment();
		}
		getFragmentManager().beginTransaction().replace(R.id.create_content_frame, fragment).commit();
		return false;
	}
	
	
	public class NewBulletinFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_bulletin, container, false);
			
			final Intent intent = getIntent();
			view.findViewById(R.id.new_bulletin_submit).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = ((TextView)findViewById(R.id.new_bulletin_title)).getText().toString();
					if(title == null || title.trim().isEmpty()){
						return; // do not add blank input to list
					}
					
					String desc = ((TextView)findViewById(R.id.new_bulletin_text)).getText().toString();
					if(desc == null || desc.trim().isEmpty()){
						return; // do not add blank input to list
					}
					User user = new User(0, intent.getStringExtra("user"), intent.getStringExtra("rank"));
					
					// Error occurring here.. Commented out
					
					//intent.putExtra("bulletin", new Bulletin(0, title, text, user));
					//setResult(RESULT_OK, intent);
					
					JSONQuery jquery = new JSONQuery(CreateContentActivity.this);
					jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Bulletin", user.getTitle(), title, desc);
					
					finish();
				}
			});
			view.findViewById(R.id.new_bulletin_cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			
			return view;
		}

	}

	
	public class NewDiscussionFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_discussion, container, false);
			
			final Intent intent = getIntent();
			view.findViewById(R.id.new_discussion_submit).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = ((TextView)findViewById(R.id.new_discussion_title)).getText().toString();
					if(title == null || title.trim().isEmpty()){
						return; // do not add blank input to list
					}
					
					String desc = ((TextView)findViewById(R.id.new_discussion_description)).getText().toString();
					if(desc == null || desc.trim().isEmpty()){
						return; // do not add blank input to list
					}
					User user = new User(0, intent.getStringExtra("user"), intent.getStringExtra("rank"));
					
					// Error occurring here.. Commented out
					
//					intent.putExtra("discussion", new Discussion(0, title, desc, user));
//					setResult(RESULT_OK, intent);
					
					JSONQuery jquery = new JSONQuery(CreateContentActivity.this);
					jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Discussion", user.getTitle(), title, desc);
					
					finish();
				}
			});
			view.findViewById(R.id.new_discussion_cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			
			return view;
		}
	}
	
	
	public class NewGroupFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_group, container, false);
			
			final Intent intent = getIntent();
			view.findViewById(R.id.new_group_submit).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = ((TextView)findViewById(R.id.new_group_title)).getText().toString();
					if(title == null || title.trim().isEmpty()){
						return; // do not add blank input to list
					}
					
					String desc = ((TextView)findViewById(R.id.new_group_description)).getText().toString();
					if(desc == null || desc.trim().isEmpty()){
						return; // do not add blank input to list
					}
					
					User user = new User(0, intent.getStringExtra("user"), intent.getStringExtra("rank"));
					Group g = new Group(0, title, desc, user,
							findViewById(R.id.radio_open).isSelected(), findViewById(R.id.radio_visible).isSelected());
					intent.putExtra("group", g);
					setResult(RESULT_OK, intent);
					
					String privacy = "Open";
					String visibility = "Visible";
					if (!g.isOpen()){
						privacy = "Closed";
					}
					if (!g.isVisible()) {
						visibility = "Hidden";
					}
					
					JSONQuery jquery = new JSONQuery(CreateContentActivity.this);
					jquery.execute(ServerUtil.URL_CREATE_CONTENT, "Group", user.getTitle(), title, desc, privacy, visibility);		
					
					finish();
				}
			});
			view.findViewById(R.id.new_group_cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			
			return view;
		}
		
	}
	
	
	// Need to implement Group Drop Down selection boxes so Discussions and Bulletins can be included 
	// inside a Group on their creation. Create dummy values at first and I can fill them in later
	public void populateBulletinGroupDropDown() {
		 
		bulletinGroups = (Spinner) findViewById(R.id.bulletin_group_selection);
		List<String> list = new ArrayList<String>();
		
		// Eventual JQuery Call will go here to populate lists
//		SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
//		String currentUser = preferences.getString("currentUsername", "");
//
//		JSONQuery jquery = new JSONQuery(this);
//		jquery.execute(ServerUtil.SOME_URL, currentUser);
		
		list.add("list 1");
		list.add("list 2");
		list.add("list 3");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bulletinGroups.setAdapter(dataAdapter);
	  }
	
	public void populateDiscussionGroupDropDown() {
		 
		discussionGroups = (Spinner) findViewById(R.id.discussion_group_selection);
		List<String> list = new ArrayList<String>();
		
		// Eventual JQuery Call will go here to populate lists
//		SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
//		String currentUser = preferences.getString("currentUsername", "");
//
//		JSONQuery jquery = new JSONQuery(this);
//		jquery.execute(ServerUtil.SOME_URL, currentUser);
		
		
		list.add("list 1");
		list.add("list 2");
		list.add("list 3");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		discussionGroups.setAdapter(dataAdapter);
	  }

	@Override
	public void processFinish(JSONObject result) {
		try { 
			// Checking for SUCCESS TAG
			int success = result.getInt("success");
			if (success == 1) {
				// Content Successfully Created Message Here
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
