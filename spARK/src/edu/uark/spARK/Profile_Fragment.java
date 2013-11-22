package edu.uark.spARK;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import android.content.SharedPreferences;


public class Profile_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public Profile_Fragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
		TextView textview = (TextView)profileView.findViewById(R.id.userName);
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", getActivity().MODE_PRIVATE);

        textview.setText(preferences.getString("currentUsername","User"));


    	return profileView;
    }

	@Override
	public void processFinish(JSONObject result) {
		// TODO Auto-generated method stub
		
	}
}
