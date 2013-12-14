package edu.uark.spARK.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.uark.spARK.R;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import edu.uark.spARK.R.menu;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.fragment.MyProfileFragment;


public class EditAccountActivity extends Activity implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_edit_profile);
		// Show the Up button in the action bar.
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
        TextView userName = (TextView)findViewById(R.id.userName);
		EditText userFullName = (EditText)findViewById(R.id.userFullNameField);
		EditText aboutMeField = (EditText)findViewById(R.id.aboutMeField);
		userName.setText(MainActivity.myUsername);
		userFullName.setText(MainActivity.myFullName);
		aboutMeField.setText(MainActivity.myDesc);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
//		EditText addComment = (EditText) menu.findItem(R.id.menuAddCommentTextView).getActionView();
//		
//		addComment.setTextSize(24.0f);
//		//addComment.getLayoutParams().width = LayoutParams.MATCH_PARENT;
//		Point size = new Point();
//		getWindow().getWindowManager().getDefaultDisplay().getSize(size);
//		addComment.setWidth(size.x);
//		addComment.invalidate();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				Intent MyProfile = new Intent(this, MyProfileFragment.class);
				setResult(0, MyProfile);
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
