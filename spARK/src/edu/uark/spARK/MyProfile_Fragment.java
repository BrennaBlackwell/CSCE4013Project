package edu.uark.spARK;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;

public class MyProfile_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public MyProfile_Fragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

	    //update the actionbar to show the up caret
	    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	    
    	//this should be false by default so there's no caret when a user selects their profile in NavDrawer
    	setHasOptionsMenu(false);
   
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View profileView = inflater.inflate(R.layout.fragment_myprofile, container, false);
		TextView userName = (TextView)profileView.findViewById(R.id.userName);
		TextView userFullName = (TextView)profileView.findViewById(R.id.userFullName);
		TextView aboutMeField = (TextView)profileView.findViewById(R.id.aboutMeField);
		userName.setText(MainActivity.myUsername);
		userFullName.setText(MainActivity.myFullName);
		aboutMeField.setText(MainActivity.myDesc);
		
		Button editButton = (Button) profileView.findViewById(R.id.EditAccount);
    	editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), EditAccountActivity.class);
			//	i.putExtra(name, value);
				startActivityForResult(i, 1);
			}
		});
		
    	return profileView;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

	    // get selected item
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		getActivity().onBackPressed();
	    		break;
    		default:
    			break;
	    }
	    return true;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (resultCode == 0) {
    		getFragmentManager().beginTransaction().replace(R.id.fragment_frame, new MyProfile_Fragment()).commit();
        } else {
        	// Handle cancel
        }
    }
    
	@Override
	public void processFinish(JSONObject result) {
		// TODO Auto-generated method stub
		
	}
}
