package edu.uark.spARK;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;


public class EditAccountActivity extends Activity implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_edit_profile);
        TextView userName = (TextView)findViewById(R.id.userName);
		EditText userFullName = (EditText)findViewById(R.id.userFullNameField);
		EditText aboutMeField = (EditText)findViewById(R.id.aboutMeField);
		userName.setText(MainActivity.myUsername);
		userFullName.setText(MainActivity.myFullName);
		aboutMeField.setText(MainActivity.myDesc);
    }
    
	public void Save(View v){
		EditText userFullName = (EditText)findViewById(R.id.userFullNameField);
		String fullname = userFullName.getText().toString();
		MainActivity.myFullName = fullname;
		
		EditText aboutMeField = (EditText)findViewById(R.id.aboutMeField);
		String aboutme = aboutMeField.getText().toString();
		MainActivity.myDesc = aboutme;
		
		JSONQuery jquery = new JSONQuery(this);
        jquery.execute(ServerUtil.URL_EDIT_PROFILE, Integer.toString(MainActivity.myUserID), MainActivity.myFullName, MainActivity.myDesc);
	}
	
	public void Cancel(View v){
		finish();
	}
    
	@Override
	public void processFinish(JSONObject result) {
		try { 
			int success = result.getInt("success");
			if (success == 1) {
				Intent MyProfile = new Intent(this, MyProfile_Fragment.class);
				setResult(0, MyProfile);
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
