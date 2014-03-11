package edu.uark.spARK.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.MapFragment;

import edu.uark.spARK.R;
import edu.uark.spARK.location.MyLocation;

public class AddLocationActivity extends Activity {
	private String Location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);
		final EditText ed = (EditText) findViewById(R.id.location_edittext);
		ed.setTextSize(14);
		ed.setAllCaps(true);
		ed.setTypeface(Typeface.DEFAULT_BOLD);
		getFragmentManager().beginTransaction().add(R.id.map_frame, new MapFragment(), "LocationMap").commit();
		setupActionBar();
		
		final CharSequence[] items = { "Use current location", "Other", "None" };
		ArrayAdapter<CharSequence> mAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, items);

//		builder.setTitle("Pick a location").setTitleColor(getResources().getColor(android.R.color.black)).setDividerColor(getResources().getColor(R.color.red))
//				.setCustomView(R.layout.dialog_location_picker, getActivity()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int id) {
//						// set location and be amazed
//						editTextLocation.setText(Location);
//
//					}
//				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int id) {
//
//					}
//				});
//		((TextView) builder.getView().findViewById(R.id.message)).setTextSize(12);
		ListView mList = (ListView) findViewById(R.id.listView1);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				switch (position) {
				// checkins might come in handy here, as we can use it
				// to see nearby places
				case 0:
					final MyLocation location = new MyLocation();

					final ProgressDialog progressDialog = ProgressDialog.show(AddLocationActivity.this, "Finding your location", "Please wait...", true);

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
							ed.setText(curlat + ", " + curlong + "\nAt " + _Location + "?");
							Location = _Location + " (" + curlat + "," + curlong + ")";
						}
					};

					Runnable run = new Runnable() {
						@Override
						public void run() {
							location.getLocation(getApplicationContext(), locationResult);
						}

					};
					run.run();
					break;
				case 1:

					break;
				case 2:
					ed.setText("NO LOCATION");
					Location = null;
					break;
				}
			}

		});
//		builder.create().show();
//		break;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_location, menu);
    	final MenuItem acceptItem = menu.findItem(R.id.accept);
		final Button btnCreate = (Button) acceptItem.getActionView();
    	btnCreate.setText("OK");
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
				menu.performIdentifierAction(acceptItem.getItemId(), Menu.CATEGORY_CONTAINER);
			}
    		
    	});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
		return true;
	}

}
