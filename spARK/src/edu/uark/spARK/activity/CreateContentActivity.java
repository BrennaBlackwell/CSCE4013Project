package edu.uark.spARK.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.uark.spARK.R;
import edu.uark.spARK.data.PredictionSample;
import edu.uark.spARK.dialog.CustomDialogBuilder;
import edu.uark.spARK.entity.Bulletin;
import edu.uark.spARK.entity.Discussion;
import edu.uark.spARK.entity.Group;
import edu.uark.spARK.entity.User;
import edu.uark.spARK.fragment.DatePickerFragment;
import edu.uark.spARK.fragment.NewContentFragment;
import edu.uark.spARK.fragment.TimePickerFragment;
import edu.uark.spARK.location.MyLocation;

@SuppressLint("ValidFragment")
public class CreateContentActivity extends FragmentActivity implements OnNavigationListener {

	private String[] options = new String[] { "BULLETIN", "DISCUSSION", "GROUP", "EVENT" };
	private NewContentFragment curNewFragment;
	// SectionsPagerAdapter mSectionsPagerAdapter;
	// ViewPager mViewPager;

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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(), R.layout.simple_spinner_item, android.R.id.text1, options);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(adapter, this);

		/*
		 * mSectionsPagerAdapter = new
		 * SectionsPagerAdapter(getSupportFragmentManager());
		 * 
		 * // Set up the ViewPager with the sections adapter. mViewPager =
		 * (ViewPager) findViewById(R.id.create_content_pager);
		 * mViewPager.setAdapter(mSectionsPagerAdapter);
		 */
		String contentType = getIntent().getStringExtra("contentType");
		if (contentType != null) {
			for (int i = 0; i < options.length; i++) {
				if (contentType.equalsIgnoreCase(options[i] + "S")) {
					actionBar.setSelectedNavigationItem(i);
					break;
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_content, menu);
    	final MenuItem createItem = menu.findItem(R.id.create);
		final Button btnCreate = (Button) createItem.getActionView();
    	btnCreate.setText("Create");
    	btnCreate.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    	btnCreate.setTextColor(Color.WHITE);
    	btnCreate.setGravity(Gravity.CENTER);
    	btnCreate.setAllCaps(true);
    	btnCreate.setTypeface(btnCreate.getTypeface(), Typeface.BOLD);
    	float scale = getResources().getDisplayMetrics().density;
    	int dpAsPixels = (int) (10*scale + 0.5f);
    	btnCreate.setPadding(dpAsPixels, 0, dpAsPixels, 0);
    	btnCreate.setBackgroundResource(R.drawable.ab_selector);
    	btnCreate.setShadowLayer(1, 1, 1, Color.BLACK);
    	btnCreate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menu.performIdentifierAction(createItem.getItemId(), Menu.CATEGORY_CONTAINER);
			}
    		
    	});
    	
    	return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.create:
			Toast toast = Toast.makeText(getApplicationContext(), "MATT MAGIC HAPPENS HERE!", 2);
			curNewFragment.create();
			toast.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * public class SectionsPagerAdapter extends FragmentPagerAdapter {
	 * 
	 * public SectionsPagerAdapter(FragmentManager fm) { super(fm); }
	 * 
	 * @Override public Fragment getItem(int position) { Fragment fragment = new
	 * Fragment(); if(position == 0) { fragment = new NewBulletinFragment(); }
	 * else if (position == 1) { fragment = new NewDiscussionFragment(); } else
	 * if (position == 2) { fragment = new NewGroupFragment(); } return
	 * fragment; }
	 * 
	 * @Override public int getCount() { return options.length; }
	 * 
	 * @Override public CharSequence getPageTitle(int position) { if(position >=
	 * 0 && position < options.length) { return options[position]; } else {
	 * return null; } } }
	 */

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		if (position == 0) {
			curNewFragment = new NewBulletinFragment();
		} else if (position == 1) {
			curNewFragment = new NewDiscussionFragment();
		} else if (position == 2) {
			curNewFragment = new NewGroupFragment();
		} else if (position == 3) {
			curNewFragment = new NewEventFragment();
		}
		getFragmentManager().beginTransaction().replace(R.id.create_content_frame, curNewFragment).commit();
		return false;
	}

	private final class NewEventFragment extends NewContentFragment implements OnClickListener {
		private Button btnStartDate, btnEndDate, btnStartTime, btnEndTime, btnLocation;
		private EditText editTextLocation;
		String Location = null;

		public void create() {
			//creating events logic goes here
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_new_event, container, false);
			btnStartDate = (Button) v.findViewById(R.id.new_event_date_start);
			btnEndDate = (Button) v.findViewById(R.id.new_event_date_end);
			btnStartTime = (Button) v.findViewById(R.id.new_event_time_start);
			btnEndTime = (Button) v.findViewById(R.id.new_event_time_end);
			btnLocation = (Button) v.findViewById(R.id.new_event_location_btn);
			editTextLocation = (EditText) v.findViewById(R.id.new_event_location);

			final Spinner groups = (Spinner) v.findViewById(R.id.bulletin_group_selection);
			List<String> list = new ArrayList<String>();
			String listItem = "Public";
			list.add(listItem);
			for (int i = 0; i < MainActivity.myGroups.size(); i++) {
				listItem = MainActivity.myGroups.get(i).getTitle();
				list.add(listItem);
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
			groups.setAdapter(dataAdapter);

			SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
			btnStartDate.setText(sdf.format(new Date()));
			btnStartDate.setOnClickListener(this);
			btnEndDate.setOnClickListener(this);
			btnStartTime.setOnClickListener(this);
			btnEndTime.setOnClickListener(this);
			btnLocation.setOnClickListener(this);
			return v;
		}

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.new_event_date_start:
				DatePickerFragment startDateFragment = new DatePickerFragment() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						Button btnDate = (Button) v;
						btnDate.setText(new SimpleDateFormat("MMMM d, yyyy").format(new Date(year - 1900, month, day)));
					}
				};
				startDateFragment.show(getFragmentManager(), "datePicker");
				break;
			case R.id.new_event_date_end:
				DialogFragment endDateFragment = new DatePickerFragment() {
					@Override
					public Dialog onCreateDialog(Bundle savedInstanceState) {
						final DatePickerDialog d = (DatePickerDialog) super.onCreateDialog(savedInstanceState);
						if (!((Button) v).getText().toString().isEmpty()) {
							((DatePickerDialog) d).setButton(DialogInterface.BUTTON_NEGATIVE, "Clear", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if (which == DialogInterface.BUTTON_NEGATIVE) {
										((Button) v).setText(null);
										((Button) findViewById(R.id.new_event_time_end)).setText(null);
									}
								}
							});
						} else {
							((DatePickerDialog) d).setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if (which == DialogInterface.BUTTON_NEGATIVE) {
										d.dismiss();
									}
								}
							});
						}
						return d;
					}

					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						Button btnDate = (Button) v;
						btnDate.setText(new SimpleDateFormat("MMMM d, yyyy").format(new Date(year - 1900, month, day)));
					}
				};
				endDateFragment.show(getFragmentManager(), "datePicker");
				break;
			// these cases are both the same for now
			case R.id.new_event_time_start:
			case R.id.new_event_time_end:
				TimePickerFragment timeFragment = new TimePickerFragment() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						Button btnTime = (Button) v;
				        Calendar c = Calendar.getInstance();
				        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
				        c.set(Calendar.MINUTE, minute);
				        Toast t = Toast.makeText(CreateContentActivity.this, DateFormat.format("h:mm a", c), 2);
				        t.show();
						btnTime.setText(DateFormat.format("h:mm a", c));
					}
				};
				timeFragment.show(getFragmentManager(), "timePicker");
				break;
			case R.id.new_event_location_btn:
				final CharSequence[] items = { "Use current location", "Other", "None" };
				final CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());
				ArrayAdapter<CharSequence> mAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_list_item_1, items);

				builder.setTitle("Pick a location").setTitleColor(getResources().getColor(android.R.color.black)).setDividerColor(getResources().getColor(R.color.red))
						.setCustomView(R.layout.dialog_location_picker, getActivity()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// set location and be amazed
								editTextLocation.setText(Location);

							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});
				((TextView) builder.getView().findViewById(R.id.message)).setTextSize(12);
				ListView mList = (ListView) builder.getCustomView().findViewById(R.id.listView1);
				mList.setAdapter(mAdapter);
				mList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						switch (position) {
						// checkins might come in handy here, as we can use it
						// to see nearby places
						case 0:
							final MyLocation location = new MyLocation();

							final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Finding your location", "Please wait...", true);

							final MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
								@Override
								public void gotLocation(Location location) {
									String _Location = "N/A";
									// get closest place for naming purposes
									Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
									try {
										List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
										if (null != listAddresses && listAddresses.size() > 0) {
											_Location = listAddresses.get(0).getAddressLine(0);
										}
									} catch (IOException e) {
										e.printStackTrace();
									}

									String curlat = String.valueOf(location.getLatitude());
									String curlong = String.valueOf(location.getLongitude());
									progressDialog.dismiss();
									builder.setMessage(curlat + ", " + curlong + "\nAt " + _Location + "?");
									Location = _Location + " (" + curlat + "," + curlong + ")";
								}
							};

							Runnable run = new Runnable() {
								@Override
								public void run() {
									location.getLocation(getActivity().getApplicationContext(), locationResult);
								}

							};
							run.run();
							break;
						case 1:

							break;
						case 2:
							builder.setMessage("NO LOCATION");
							Location = null;
							break;
						}
					}

				});
				builder.create().show();
				break;
			}

		}
	}

	public class NewBulletinFragment extends NewContentFragment {
		
		private Spinner groups;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_bulletin, container, false);
			groups = (Spinner) view.findViewById(R.id.bulletin_group_selection);
			List<String> list = new ArrayList<String>();
			String listItem = "Public";
			list.add(listItem);
			for (int i = 0; i < MainActivity.myGroups.size(); i++) {
				listItem = MainActivity.myGroups.get(i).getTitle();
				list.add(listItem);
			}
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			groups.setAdapter(dataAdapter);
			return view;
		}
		
		public void create() {
			Intent intent = getIntent();
			String title = ((TextView) findViewById(R.id.new_bulletin_title)).getText().toString();
			if (title == null || title.trim().isEmpty()) {
				return; // do not add blank input to list
			}

			String desc = ((TextView) findViewById(R.id.new_bulletin_text)).getText().toString();
			if (desc == null || desc.trim().isEmpty()) {
				return; // do not add blank input to list
			}
			User user = new User(MainActivity.myUserID, MainActivity.myUsername, null, MainActivity.myFullName, MainActivity.myDesc, 0, MainActivity.myProfilePicture);
			int position = groups.getSelectedItemPosition();

			int itemSelected = 0;
			if (position != 0) {
				itemSelected = MainActivity.myGroups.get(position - 1).getId();
			}

			String lat = intent.getStringExtra("latitude");
			String lng = intent.getStringExtra("longitude");
			intent.putExtra("bulletin", new Bulletin(0, title, desc, user, lat, lng));
			intent.putExtra("groupSelected", itemSelected);
			setResult(RESULT_OK, intent);
			finish();
		}	
	}

	public class NewDiscussionFragment extends NewContentFragment {
		private Spinner groups;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_discussion, container, false);

			groups = (Spinner) view.findViewById(R.id.discussion_group_selection);
			List<String> list = new ArrayList<String>();
			String listItem = "Public";

			list.add(listItem);
			for (int i = 0; i < MainActivity.myGroups.size(); i++) {
				listItem = MainActivity.myGroups.get(i).getTitle();
				list.add(listItem);
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			groups.setAdapter(dataAdapter);

			view.findViewById(R.id.new_discussion_cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});

			return view;
		}
		
		public void create() {
			final Intent intent = getIntent();
			String title = ((TextView) findViewById(R.id.new_discussion_title)).getText().toString();
			if (title == null || title.trim().isEmpty()) {
				return; // do not add blank input to list
			}

			String desc = ((TextView) findViewById(R.id.new_discussion_description)).getText().toString();
			if (desc == null || desc.trim().isEmpty()) {
				return; // do not add blank input to list
			}
			User user = new User(MainActivity.myUserID, MainActivity.myUsername, null, MainActivity.myFullName, MainActivity.myDesc, 0, MainActivity.myProfilePicture);
			int position = groups.getSelectedItemPosition();

			int itemSelected = 0;
			if (position != 0) {
				itemSelected = MainActivity.myGroups.get(position - 1).getId();
			}

			String lat = intent.getStringExtra("latitude");
			String lng = intent.getStringExtra("longitude");

			intent.putExtra("discussion", new Discussion(0, title, desc, user, lat, lng));
			intent.putExtra("groupSelected", itemSelected);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	public class NewGroupFragment extends NewContentFragment {
		private Spinner groups;
		
		public void create() {
			final Intent intent = getIntent();
			String title = ((TextView) findViewById(R.id.new_group_title)).getText().toString();
			if (title == null || title.trim().isEmpty()) {
				return; // do not add blank input to list
			}

			String desc = ((TextView) findViewById(R.id.new_group_description)).getText().toString();
			if (desc == null || desc.trim().isEmpty()) {
				return; // do not add blank input to list
			}

			RadioButton privacy = (RadioButton) findViewById(R.id.radio_open);
			RadioButton visibility = (RadioButton) findViewById(R.id.radio_visible);

			User user = new User(MainActivity.myUserID, MainActivity.myUsername, null, MainActivity.myFullName, MainActivity.myDesc, 0, MainActivity.myProfilePicture);

			String lat = intent.getStringExtra("latitude");
			String lng = intent.getStringExtra("longitude");

			Group g = new Group(0, title, desc, user, lat, lng, privacy.isChecked(), visibility.isChecked());
			intent.putExtra("group", g);
			setResult(RESULT_OK, intent);

			finish();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_new_group, container, false);
			return view;
		}

	}
}
