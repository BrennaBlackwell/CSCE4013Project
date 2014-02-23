package edu.uark.spARK.fragment;

import org.json.JSONObject;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.uark.spARK.R;
import edu.uark.spARK.activity.EditProfileActivity;
import edu.uark.spARK.activity.MainActivity;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;

public class MyProfileFragment extends Fragment implements AsyncResponse {
	private static final int EDIT_PROFILE = 1;
	public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public MyProfileFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
	    //update the actionbar to show the up caret
	    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	    
    	setHasOptionsMenu(true);   
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View profileView = inflater.inflate(R.layout.fragment_myprofile, container, false);
		ImageView imageView = (ImageView) profileView.findViewById(R.id.profileImageView);
    	TextView userName = (TextView)profileView.findViewById(R.id.userName);
		TextView userFullName = (TextView)profileView.findViewById(R.id.userFullName);
		TextView aboutMeField = (TextView)profileView.findViewById(R.id.aboutMeField);
		
		imageView.setImageBitmap(MainActivity.myProfilePicture);
		userName.setText(MainActivity.myUsername);
		userFullName.setText(MainActivity.myFullName);
		aboutMeField.setText(MainActivity.myDesc);
		
		Button editButton = (Button) profileView.findViewById(R.id.EditProfile);
    	editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), EditProfileActivity.class);
				startActivityForResult(intent, EDIT_PROFILE);
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
    	super.onActivityResult(requestCode, resultCode, intent);
    	switch(requestCode) { 
        case EDIT_PROFILE:
	    	if (resultCode == 0) {
	    		getFragmentManager().beginTransaction().replace(R.id.fragment_frame, new MyProfileFragment()).commit();
	        } else {
	        	// Handle cancel
	        }
	    	break;
    	}
    }
    
	@Override
	public void processFinish(JSONObject result) {
		// TODO Auto-generated method stub
		
	}
}
