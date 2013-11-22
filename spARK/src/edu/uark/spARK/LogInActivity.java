package edu.uark.spARK;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import android.net.Uri;


public class LogInActivity extends Activity implements AsyncResponse {
 
	private static String username;
	private static String password;
	
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private View mLoginView;


    
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.splash);
        
        if (preferences.getBoolean("autoLogin", false)){
            //Login(mLoginView);
			Intent MainIntent = new Intent(this, MainActivity.class);
			startActivity(MainIntent);
            finish();
        }


        
    	mLoginView = findViewById(R.id.includeLogin);
    	mLoginView.setAlpha(0f);
        new Handler().postDelayed(new Runnable() {
       	 
            @Override
            public void run() {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                //float width = size.x;
                //float height = size.y;
                ImageView ivSplash = (ImageView) findViewById(R.id.profileImageView);
                //float spark_X = ivSplash.getLeft();
                float spark_Y = ivSplash.getTop();
            	float distance = (0-spark_Y);
            	ivSplash.animate().translationY(distance).withLayer();
            }
        }, (SPLASH_TIME_OUT));
        
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mLoginView.animate().alpha(1f).setDuration(450).setListener(null);
                //Intent i = new Intent(SplashActivity.this, LogInActivity.class);
                //startActivity(i);
                //findViewById(R.id.relativeLayoutLogin).setVisibility(View.VISIBLE);
                // close this activity
                //finish();
            }
        }, SPLASH_TIME_OUT + 450);

        EditText txtUsername = (EditText)findViewById(R.id.Username);
        txtUsername.setText(preferences.getString("currentUsername", ""));
    }
    
	public void Login(View v){

        EditText txtUsername = (EditText)findViewById(R.id.Username);
        EditText txtPassword = (EditText)findViewById(R.id.Password);
        username = txtUsername.getText().toString().trim();
        password = txtPassword.getText().toString().trim();

		//make sure text has been added to the login screen
		if (username.matches("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a Username", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		else if (password.matches("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a Password", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		else {		
			JSONQuery jquery = new JSONQuery(this);
			jquery.execute(ServerUtil.URL_AUTHENTICATE, username, password);
			
		}
	}
	
	public void CreateAccount(View v){
		Intent CreateAccountIntent = new Intent(this, RegisterActivity.class);
		startActivityForResult(CreateAccountIntent, 1);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
        	TextView text_view2 = (TextView)findViewById(R.id.accountCreated);
        	text_view2.setVisibility(View.VISIBLE);
        }
	}
	
	@Override
	public void processFinish(JSONObject result) {
		TextView text_view1 = (TextView)findViewById(R.id.invalidLogin);
		TextView text_view2 = (TextView)findViewById(R.id.accountCreated);
		text_view2.setVisibility(View.INVISIBLE);
		
		try {
			
			int success = result.getInt("success");

			if (success == 1) {
				text_view1.setVisibility(View.INVISIBLE);

                SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                
                editor.putString("currentUsername", username);
                editor.putString("currentPassword", password);
                editor.putBoolean("autoLogin", true);
                editor.commit();
				
				Intent MainIntent = new Intent(this, MainActivity.class);
				startActivity(MainIntent);
                finish();
            } else {
				text_view1.setVisibility(View.VISIBLE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//hides the invalid login
		findViewById(R.id.invalidLogin).setVisibility(View.INVISIBLE);
	}
}
