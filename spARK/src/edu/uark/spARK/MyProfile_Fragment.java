package edu.uark.spARK;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;

public class MyProfile_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public MyProfile_Fragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	SharedPreferences preferences = this.getActivity().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
		String currentUser = preferences.getString("currentUser", "");
		
		View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
		TextView textview = (TextView)profileView.findViewById(R.id.userID);
		textview.setText(currentUser);
		
    	return profileView;
    }

	@Override
	public void processFinish(String output) {
		// TODO Auto-generated method stub
		
	}
}