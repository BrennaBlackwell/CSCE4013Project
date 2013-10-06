package edu.uark.spARK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
		
		String username = edit_text1.getText().toString();
		String email = edit_text2.getText().toString();		
		String password = edit_text3.getText().toString();
		
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute("http://csce.uark.edu/~mmmcclel/spark/register.php", username, email, password);		
	}
	
	public void Cancel(View v){
		finish();
	}
	
	@Override
	public void processFinish(String output) {
		TextView text_view1 = (TextView)findViewById(R.id.userTaken);
		
		if (output.contains("Success")) {
			text_view1.setVisibility(View.INVISIBLE);
			
			Intent LogInIntent = new Intent(this, LogInActivity.class);
			LogInIntent.putExtra("Account_Created", "true");
			setResult(Activity.RESULT_OK, LogInIntent);

			finish();
		} else {
			text_view1.setVisibility(View.VISIBLE);
		}
	}
}
