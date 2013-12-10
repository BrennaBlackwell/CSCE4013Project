package edu.uark.spARK;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

import edu.uark.spARK.JSONQuery.AsyncResponse;

public class LogInActivity extends Activity implements AsyncResponse,
        ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener{


    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

	private static String username;
	private static String password;
	
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private View mLoginView;

    private boolean GoogleLogout = false;
	private SignInButton plusSignInButton;
    
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
                .build();
        // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);





        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.splash);
        
        if (preferences.getBoolean("autoLogin", false)){
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
                ImageView ivSplash = (ImageView) findViewById(R.id.profileImageView);
                float spark_Y = ivSplash.getTop();
            	float distance = (0-spark_Y);
            	ivSplash.animate().translationY(distance).withLayer();
            }
        }, (SPLASH_TIME_OUT));
        
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mLoginView.animate().alpha(1f).setDuration(450).setListener(null);
            }
        }, SPLASH_TIME_OUT + 450);

        EditText txtUsername = (EditText)findViewById(R.id.Username);
        txtUsername.setText(preferences.getString("currentUsername", ""));
        plusSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        plusSignInButton.setOnClickListener(this);
        ProgressBar spinner = (ProgressBar) findViewById(R.id.loginProgress);
        spinner.getIndeterminateDrawable().setColorFilter(0xFFFF0000, Mode.SRC_IN);
        setGooglePlusButtonText(plusSignInButton, "Sign in to Google");
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
        	if (mPlusClient.isConnected()) {
        		Logout();
        	}
        	else {
	            if (mConnectionResult == null) {
	                mConnectionProgressDialog.show();
	            } else {
	                try {
	                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	                } catch (SendIntentException e) {
	                    // Try connecting again.
	                    mConnectionResult = null;
	                    mPlusClient.connect();
	                }
	            }
        	}
        }
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
			JSONQuery jquery = new JSONQuery(this) {	

				@Override
				public void onPreExecute() {
//					mConnectionProgressDialog.setMessage("Signing in...");
//					mConnectionProgressDialog.show();
					findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);
				}
			};
			jquery.execute(ServerUtil.URL_AUTHENTICATE, username, password);
			
		}
	}

    public void Logout(){
        mPlusClient.clearDefaultAccount();
        mPlusClient.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
            @Override
            public void onAccessRevoked(ConnectionResult status) {
                // mPlusClient is now disconnected and access has been revoked.
                // Trigger app logic to comply with the developer policies
                Toast toast = Toast.makeText(getApplicationContext(), "Disconnected from Google+", Toast.LENGTH_SHORT);
                setGooglePlusButtonText(plusSignInButton, "Sign in to Google");
                toast.show();
                GoogleLogout = false;
                mPlusClient.connect();
            }
        });
        //mPlusClient.disconnect();
    }
	
	public void CreateAccount(View v){
		Intent CreateAccountIntent = new Intent(this, RegisterActivity.class);
		startActivityForResult(CreateAccountIntent, 1);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            new CustomDialogBuilder(this)
            .setIcon(R.drawable.ic_dialog_info_holo_light)
            .setTitle("Signup Complete")
            .setTitleColor(getResources().getColor(R.color.red))
            .setDividerColor(getResources().getColor(R.color.red))
            .setMessage("Account successfully created! You can now login below.")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {

               }
           }).create().show();
        }

        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
	}
	
	@Override
	public void processFinish(JSONObject result) {
		//mConnectionProgressDialog.hide();
		try {
			
			int success = result.getInt("success");

			if (success == 1) {

                SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                
                editor.putString("currentUsername", username);
                editor.putString("currentPassword", password);
                editor.putBoolean("autoLogin", true);
                editor.apply();
				
				Intent MainIntent = new Intent(this, MainActivity.class);
				startActivity(MainIntent);
                finish();
            } else {
                new CustomDialogBuilder(this)
                .setIcon(R.drawable.ic_dialog_alert_holo_light)
                .setTitle("Incorrect Login")
                .setTitleColor(getResources().getColor(R.color.red))
                .setDividerColor(getResources().getColor(R.color.red))
                .setMessage("The username and password you have entered is incorrect. Please check your login information and try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {

                   }
               }).create().show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		findViewById(R.id.loginProgress).setVisibility(View.INVISIBLE);
	}


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mConnectionProgressDialog.isShowing()) {
            // The user clicked the sign-in button already. Start to resolve
            // connection errors. Wait until onConnected() to dismiss the
            // connection dialog.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (SendIntentException e) {
                    mPlusClient.connect();
                }
            }
        }
        // Save the result and resolve the connection failure upon a user click.
        mConnectionResult = result;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
//        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
//            mConnectionResult = null;
//            mPlusClient.connect();
//        }
//    }

    @Override
    public void onConnected(Bundle connectionHint) {
    	setGooglePlusButtonText(plusSignInButton, "Sign out of Google");
        String fullEmail = mPlusClient.getAccountName();
        String username = fullEmail.replace(fullEmail.substring(fullEmail.lastIndexOf("@"),fullEmail.length()),"");
        String userID = ""+mPlusClient.getCurrentPerson().getId();
        Toast.makeText(this, username + " is connected, ID = " + userID, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Log.d("ERROR", "disconnected");
    }



	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                                                        INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
	
	//We can use the same button for signing in/out of google. The only caveat is that the TextView has to be found
	protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
	    // Find the TextView that is inside of the SignInButton and set its text
	    for (int i = 0; i < signInButton.getChildCount(); i++) {
	        View v = signInButton.getChildAt(i);

	        if (v instanceof TextView) {
	            TextView tv = (TextView) v;
	            tv.setText(buttonText);
	            return;
	        }
	    }
	}
}
