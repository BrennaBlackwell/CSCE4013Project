package edu.uark.spARK.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Input.InputInput;
import com.google.api.services.prediction.model.Output;
import com.google.api.services.prediction.model.Output.OutputMulti;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.uark.spARK.R;
import edu.uark.spARK.data.PredictionUtil;
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
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
	GoogleCredential credential = new GoogleCredential();
	private static Prediction client;

	
	
	private String[] options = new String[] { "BULLETIN", "DISCUSSION", "GROUP", "EVENT" };
	private NewContentFragment curNewFragment;
	private int selectedNavItem;
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

	private class NewEventFragment extends NewContentFragment implements OnClickListener {
		private Button btnStartDate, btnEndDate, btnStartTime, btnEndTime, btnLocation;
		private ImageButton btnAddTopic;
		private EditText editTextLocation, editTextDescription;
		private LinearLayout topicLinearLayout;
		private List<String> outputList;
		private List<String> selectedOutput;
		String Location = null;		

		public void create() {
			
		}
		
		public void hideSoftKeyboard(Activity activity) {
		    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_new_event, container, false);
			v.findViewById(R.id.locationLinearLayout).setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent ev) {
				    hideSoftKeyboard(getActivity());
				    return false;
				}
			});
			btnStartDate = (Button) v.findViewById(R.id.new_event_date_start);
			btnEndDate = (Button) v.findViewById(R.id.new_event_date_end);
			btnStartTime = (Button) v.findViewById(R.id.new_event_time_start);
			btnEndTime = (Button) v.findViewById(R.id.new_event_time_end);
			btnLocation = (Button) v.findViewById(R.id.new_event_location_btn);
			btnAddTopic = (ImageButton) v.findViewById(R.id.topic_add_button);
			btnAddTopic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogFragment d = new DialogFragment() {
						@Override
						public Dialog onCreateDialog(Bundle savedInstanceState) {
							final ArrayList mSelectedItems = new ArrayList(); // Where we track the selected items
							AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
							// // Set the dialog title
							builder.setTitle("Suggested");
							// // Specify the list array, the items to be selected by default (null for none),
							// // and the listener through which to receive callbacks when items are selected
							boolean[] checked = new boolean[outputList.size()];
							for (int i = 0; i < outputList.size(); i++)
								if (selectedOutput.contains(outputList.get(i)))
									checked[i] = true;
							// ((String) selectedOutput.keySet().toArray()[i])
							builder.setMultiChoiceItems(outputList.toArray(new String[outputList.size()]), checked, new DialogInterface.OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which, boolean isChecked) {
									if (isChecked) {
										// If the user checked the item, add it to the selected items
										// mSelectedItems.add(which);
										selectedOutput.add(outputList.get(which));
									} else if (selectedOutput.contains(outputList.get(which))) {
										// Else, if the item is already in the array, remove it
										// mSelectedItems.remove(Integer.valueOf(which));
										selectedOutput.remove(outputList.get(which));
									}
								}
							});
							// // Set the action buttons
							builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									// User clicked OK, so save the mSelectedItems results somewhere
									// or return them to the component that opened the dialog
									topicLinearLayout.removeAllViews();
									for (int i = 0; i < selectedOutput.size(); i++) {
										final View newTopic = LayoutInflater.from(CreateContentActivity.this).inflate(R.layout.topic_grid_item, null);
										((TextView) newTopic.findViewById(R.id.topic_textview)).setText(selectedOutput.get(i));
										topicLinearLayout.addView(newTopic);
										newTopic.findViewById(R.id.topic_delete_btn).setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												topicLinearLayout.removeView(newTopic);
												selectedOutput.remove(((TextView) newTopic.findViewById(R.id.topic_textview)).getText());
											}

										});
									}
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {

								}
							});
							//
							return builder.create();
						}
					};
					d.show(getFragmentManager(), "TAG");
				}
			});
			editTextLocation = (EditText) v.findViewById(R.id.new_event_location);
			topicLinearLayout = (LinearLayout) v.findViewById(R.id.topicLinearLayout);
			editTextDescription = (EditText) v.findViewById(R.id.new_event_description);
			editTextDescription.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					PredictionTask predict = null;
					if (!hasFocus) {
						predict = new PredictionTask() {

							@Override
							protected void onPreExecute() {
								//textViewTopic.setText(null);
								topicLinearLayout.removeAllViews();
							}

							@Override
							protected Output doInBackground(Void... param) {
								Set<String> scopes = new HashSet<String>();
								scopes.add(PredictionScopes.DEVSTORAGE_FULL_CONTROL);
								scopes.add(PredictionScopes.DEVSTORAGE_READ_ONLY);
								scopes.add(PredictionScopes.DEVSTORAGE_READ_WRITE);
								scopes.add(PredictionScopes.PREDICTION);
								GoogleAccountCredential accountCredential = GoogleAccountCredential.usingOAuth2(CreateContentActivity.this, scopes);
								SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
								accountCredential.setSelectedAccountName(preferences.getString(MainActivity.PREF_ACCOUNT_NAME, null));
								client = new com.google.api.services.prediction.Prediction.Builder(httpTransport, JSON_FACTORY, MainActivity.credential).setApplicationName(
										MainActivity.APPLICATION_NAME).build();
								// test
								final Input input = new Input();
								InputInput inputInput = new InputInput();
								List<Object> params = new ArrayList<Object>();
								params.add(editTextDescription.getText().toString().trim());
								inputInput.setCsvInstance(params);
								input.setInput(inputInput);
								Output output = null;
								try {
									output = client.trainedmodels().predict(PredictionUtil.PROJECT_ID, PredictionUtil.TOPIC_MODEL_ID, input).execute();
								} catch (IOException e) {
									e.printStackTrace();
								}
								return output;
							}

							@Override
							protected void onPostExecute(Output result) {
								try {
									// sort in reverse sorted order
									Collections.sort(result.getOutputMulti(), new Comparator<OutputMulti>() {
										@Override
										public int compare(OutputMulti o1, OutputMulti o2) {
											if (Double.valueOf(o1.getScore()) < Double.valueOf(o2.getScore()))
												return 1;
											if (Double.valueOf(o1.getScore()) > Double.valueOf(o2.getScore()))
												return -1;
											return 0;
										}

									});
									Log.d("PREDICTION", result.toPrettyString());
									final View newTopic = LayoutInflater.from(CreateContentActivity.this).inflate(R.layout.topic_grid_item, null);
									outputList = new ArrayList<String>();
									selectedOutput = new ArrayList<String>();
									// dirty output for now
									for (int i = 0; i < result.getOutputMulti().size(); i++)
										outputList.add(result.getOutputMulti().get(i).getLabel());
									selectedOutput.add(result.getOutputLabel());
									((TextView) newTopic.findViewById(R.id.topic_textview)).setText(result.getOutputLabel());
									topicLinearLayout.addView(newTopic);
									newTopic.findViewById(R.id.topic_delete_btn).setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											topicLinearLayout.removeView(newTopic);
											selectedOutput.remove(((TextView) newTopic.findViewById(R.id.topic_textview)).getText());
										}

									});

								} catch (IOException e) {
									e.printStackTrace();
								} catch (NullPointerException e) {
									e.printStackTrace();
								}
							}
						};
						predict.execute();
					}
				}

			});
			
			
			
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
				Intent i = new Intent(CreateContentActivity.this, AddLocationActivity.class);
				this.startActivityForResult(i, 400);
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
	
	public class PredictionTask extends AsyncTask<Void, Void, Output> {

		public PredictionTask() {
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Output doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}		
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//	  super.onSaveInstanceState(savedInstanceState);
//	  savedInstanceState.putInt("currentContent", getActionBar().getSelectedNavigationIndex());
//	}
//
//	@Override
//	public void onRestoreInstanceState(Bundle savedInstanceState) {
//	  super.onRestoreInstanceState(savedInstanceState);
//	  selectedNavItem = savedInstanceState.getInt("currentContent");
//	  // where mMyCurrentPosition should be a public value in your activity.
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//		if (selectedNavItem != 0)
//			getActionBar().setSelectedNavigationItem(selectedNavItem);
//	}
}
