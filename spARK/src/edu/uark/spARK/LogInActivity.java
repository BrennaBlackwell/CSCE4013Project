package edu.uark.spARK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
 
public class LogInActivity extends Activity implements AsyncResponse {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private View mLoginView;
    

    
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        
        setContentView(R.layout.splash);
    	mLoginView = findViewById(R.id.includeLogin);
    	mLoginView.setAlpha(0f);
    	findViewById(R.id.likeButton).getBackground().setColorFilter(new LightingColorFilter(0xFFFF0000, 0xFFFF0000));
        findViewById(R.id.button2).getBackground().setColorFilter(new LightingColorFilter(0xFFFF0000, 0xFFFF0000));
        new Handler().postDelayed(new Runnable() {
       	 
            @Override
            public void run() {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                //float width = size.x;
                float height = size.y;
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
    }
    
	public void Login(View v){
		//debugging so we don't have to enter user/password every time. We will have to delete this later, but it might be useful for now
		if (v.getId() == R.id.buttonDebug) {
			Intent MainIntent = new Intent(this, MainActivity.class);
			startActivity(MainIntent);
			return;
		}
		
		EditText edit_text1 = (EditText)findViewById(R.id.Username);
		EditText edit_text2 = (EditText)findViewById(R.id.Password);
		
		String Username = edit_text1.getText().toString();
		String Password = edit_text2.getText().toString();		
		
		//make sure text has been added to the login screen
		if (Username.matches("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a username!", 2);
			toast.show();
			return;
		}
		else if (Password.matches("")) {
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a password!", 2);
			toast.show();
			return;
		}
		else {
			JSONQuery jquery = new JSONQuery(this);
			jquery.execute("http://csce.uark.edu/~mmmcclel/spark/authentication.php", Username, Password);
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
	public void processFinish(String output) {
		TextView text_view1 = (TextView)findViewById(R.id.invalidLogin);
		TextView text_view2 = (TextView)findViewById(R.id.accountCreated);
		text_view2.setVisibility(View.INVISIBLE);
		if (output.contains("Success")) {
			text_view1.setVisibility(View.INVISIBLE);
			Intent MainIntent = new Intent(this, MainActivity.class);
			startActivity(MainIntent);
		} else {
			text_view1.setVisibility(View.VISIBLE);
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//hides the invalid login
		findViewById(R.id.invalidLogin).setVisibility(View.INVISIBLE);
	}
}
