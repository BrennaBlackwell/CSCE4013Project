package edu.uark.spARK;

import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;


public class Profile_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public Profile_Fragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
		TextView textview = (TextView)profileView.findViewById(R.id.userName);
   
        textview.setText(MainActivity.Username);
    	return profileView;
    }

	public void BlockAccount(View v){
	    	
	}
	
	public void ReportAccount(View v){
		
	}
    
	@Override
	public void processFinish(JSONObject result) {
		// TODO Auto-generated method stub
		
	}
}
