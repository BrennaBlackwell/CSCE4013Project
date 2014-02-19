package edu.uark.spARK.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import edu.uark.spARK.R;
import edu.uark.spARK.entity.Bulletin;
import edu.uark.spARK.entity.Discussion;
import edu.uark.spARK.entity.Group;
import edu.uark.spARK.entity.User;

@SuppressLint("ValidFragment")
public class CreateContentActivity extends FragmentActivity implements OnNavigationListener {
	
	private String[] options = new String[]{ "BULLETIN", "DISCUSSION", "GROUP", "EVENT" };
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
	    actionBar.setDisplayHomeAsUpEnabled(true);
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
	    if(contentType != null) {
		    for(int i=0; i < options.length; i++) {
		    	if(contentType.equalsIgnoreCase(options[i] + "S")) {
		    		actionBar.setSelectedNavigationItem(i);
		    		break;
		    	}
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
		} else if (position == 3) {
			fragment = new NewEventFragment();
		}
		getFragmentManager().beginTransaction().replace(R.id.create_content_frame, fragment).commit();
		return false;
	}
	
	
	private final class NewEventFragment extends Fragment implements OnClickListener {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_new_event, container, false); 
			Button btnStartDate = (Button) v.findViewById(R.id.new_event_date_start);
			Button btnEndDate = (Button) v.findViewById(R.id.new_event_date_end);
			//Spinner etStartTime = (Spinner) v.findViewById(R.id.new_event_time_start);
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy"); 
			btnStartDate.setText( sdf.format(new Date()));
			//etStartTime.setText("12:00 AM");
			//etStartTime.set		
			btnStartDate.setOnClickListener(this);
			btnEndDate.setOnClickListener(this);
			return v;
		}

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.new_event_date_start: case R.id.new_event_date_end:
				DialogFragment dateFragment = new DatePickerFragment() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						Button btnDate = (Button) v; 
						btnDate.setText(new SimpleDateFormat("MMMM d, yyyy").format(new Date(year-1900, month, day)));
					}
				};
				dateFragment.show(getFragmentManager(), "datePicker");
				break;
			}
			
		}
	}

	public static class DatePickerFragment extends DialogFragment
    	implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
	
			return new DatePickerDialog(getActivity(), this, year, month, day) {
				
			};
			// Create a new instance of DatePickerDialog and return it
		}
	
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
		}
		
	}

	public class NewBulletinFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_bulletin, container, false);
			
			final Spinner groups = (Spinner) view.findViewById(R.id.bulletin_group_selection);
			List<String> list = new ArrayList<String>();
			String listItem = "Public";
			list.add(listItem);
			for (int i=0; i<MainActivity.myGroups.size(); i++) {
				listItem = MainActivity.myGroups.get(i).getTitle();
				list.add(listItem);
			}
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			groups.setAdapter(dataAdapter);
			groups.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
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
					User user = new User(MainActivity.myUserID, MainActivity.myUsername, null, MainActivity.myFullName, MainActivity.myDesc, 0, MainActivity.myProfilePicture);
					int position = groups.getSelectedItemPosition();
					
					int itemSelected = 0;
					if (position != 0) {
						itemSelected = MainActivity.myGroups.get(position-1).getId();
					}
					
					String lat = intent.getStringExtra("latitude");
					String lng = intent.getStringExtra("longitude");
					
					intent.putExtra("bulletin", new Bulletin(0, title, desc, user, lat, lng));
					intent.putExtra("groupSelected", itemSelected);
					setResult(RESULT_OK, intent);
					
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class NewDiscussionFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_discussion, container, false);
			
			final Spinner groups = (Spinner) view.findViewById(R.id.discussion_group_selection);
			List<String> list = new ArrayList<String>();
			String listItem = "Public";
			
			list.add(listItem);
			for (int i=0; i<MainActivity.myGroups.size(); i++) {
				listItem = MainActivity.myGroups.get(i).getTitle();
				list.add(listItem);
			}
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			groups.setAdapter(dataAdapter);
			groups.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
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
					User user = new User(MainActivity.myUserID, MainActivity.myUsername, null, MainActivity.myFullName, MainActivity.myDesc, 0, MainActivity.myProfilePicture);
					int position = groups.getSelectedItemPosition();

					int itemSelected = 0;
					if (position != 0) {
						itemSelected = MainActivity.myGroups.get(position-1).getId();
					}
					
					String lat = intent.getStringExtra("latitude");
					String lng = intent.getStringExtra("longitude");

					intent.putExtra("discussion", new Discussion(0, title, desc, user, lat, lng));
					intent.putExtra("groupSelected", itemSelected);
					setResult(RESULT_OK, intent);
					
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
					
					RadioButton privacy = (RadioButton)findViewById(R.id.radio_open);
					RadioButton visibility = (RadioButton)findViewById(R.id.radio_visible);
							
					User user = new User(MainActivity.myUserID, MainActivity.myUsername, null, MainActivity.myFullName, MainActivity.myDesc, 0, MainActivity.myProfilePicture);
					
					String lat = intent.getStringExtra("latitude");
					String lng = intent.getStringExtra("longitude");

					Group g = new Group(0, title, desc, user, lat, lng, privacy.isChecked(), visibility.isChecked());
					intent.putExtra("group", g);
					setResult(RESULT_OK, intent);
					
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
}
