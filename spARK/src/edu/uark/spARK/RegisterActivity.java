package edu.uark.spARK;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.uark.spARK.JSONQuery.AsyncResponse;

public class RegisterActivity extends Activity implements AsyncResponse {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}

	public void Register(View v){
		EditText edit_text1 = (EditText)findViewById(R.id.registerUsername);
		EditText edit_text2 = (EditText)findViewById(R.id.registerEmail);		
		EditText edit_text3 = (EditText)findViewById(R.id.registerPassword);
		
		String Username = edit_text1.getText().toString().trim();
		String Email = edit_text2.getText().toString().trim();		
		String Password = edit_text3.getText().toString().trim();
		
		//make sure text has been added to the login screen
		if (Username.matches("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a Username", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		else if (Email.matches("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter an Email", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		else if (Password.matches("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a Password", Toast.LENGTH_SHORT);
			toast.show();
			return;
		} else { 		
			JSONQuery jquery = new JSONQuery(this);
			jquery.execute(ServerUtil.URL_REGISTER, Username, Email, Password);		
		}
	}
	
	public void Cancel(View v){
		finish();
	}
	
	@Override
	public void processFinish(JSONObject result) {		
		try {
			int success = result.getInt("success");
			if (success == 1) {				
				Intent LogInIntent = new Intent(this, LogInActivity.class);
				LogInIntent.putExtra("Account_Created", "true");
				setResult(Activity.RESULT_OK, LogInIntent);
				finish();
			} else {
                new CustomDialogBuilder(this)
                .setIcon(R.drawable.ic_dialog_alert_holo_light)
                .setTitle("Username Taken")
                .setTitleColor(getResources().getColor(R.color.red))
                .setDividerColor(getResources().getColor(R.color.red))
                .setMessage("The username you have entered has already been taken. Please enter a different username.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {

                   }
               }).create().show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
