package edu.uark.spARK;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.entities.User;


public class Profile_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public Profile_Fragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
    	final ActionBar actionBar = getActivity().getActionBar();
	    actionBar.setDisplayShowTitleEnabled(false);
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    	
    	final User u = (User) getArguments().getSerializable("ContentCreator");
    	getActivity().getActionBar().setNavigationMode(ActionBar.DISPLAY_HOME_AS_UP);
    	
		View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
		TextView userName = (TextView)profileView.findViewById(R.id.userName);
		TextView userFullName = (TextView)profileView.findViewById(R.id.userFullName);
		TextView aboutMeField = (TextView)profileView.findViewById(R.id.aboutMeField);
		userName.setText(u.getName());
		userFullName.setText(u.getFullname());
		aboutMeField.setText(u.getDesc());
        
        Button blockButton = (Button) profileView.findViewById(R.id.Block);
    	blockButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Block " + u.getName())
		        .setMessage("Are you sure you want to block " + u.getName() + "?")
		        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	JSONQuery jquery = new JSONQuery(Profile_Fragment.this);
                    jquery.execute(ServerUtil.URL_BLOCK_CONTENT, "Block", Integer.toString(MainActivity.myUserID), Integer.toString(u.getId()), "User");
		        }

		    })
		    .setNegativeButton("No", null)
		    .show();
			}
			
		});
    	
    	Button reportButton = (Button) profileView.findViewById(R.id.Report);
    	reportButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Report " + u.getName())
		        .setMessage("Are you sure you want to report " + u.getName() + "?")
		        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	// TODO: Report functionality later
		        }

		    })
		    	.setNegativeButton("No", null)
		    	.show();
			}	
		});
    	
        
    	return profileView;
    }
    
	@Override
	public void processFinish(JSONObject result) {
		// TODO Auto-generated method stub
		
	}
}
