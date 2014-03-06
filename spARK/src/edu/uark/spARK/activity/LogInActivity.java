package edu.uark.spARK.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Input.InputInput;

import edu.uark.spARK.R;
import edu.uark.spARK.R.color;
import edu.uark.spARK.R.drawable;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.dialog.CustomDialogBuilder;

public class LogInActivity extends Activity implements AsyncResponse,
  	ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener{

	// Prediction API variables
	private static final String APPLICATION_NAME = "csce.uark.edu-spark/1.0";
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/prediction";
	static final String PREF_AUTH_TOKEN = "authToken";
	static final String PREF_ACCOUNT_NAME = "accountName";
	private static final int REQUEST_AUTHENTICATE = 11;
	private final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
	static Prediction client;
	GoogleCredential credential = new GoogleCredential();
	String accountName;
	SharedPreferences settings;
	GoogleAccountManager accountManager;
	

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
		//initialize Google API account preferences
		settings = getPreferences(Context.MODE_PRIVATE);
//		settings.edit().remove(PREF_ACCOUNT_NAME).commit();
//		settings.edit().remove(PREF_AUTH_TOKEN).commit();
		accountName = settings.getString(PREF_ACCOUNT_NAME, null);
	    //Logger.getLogger("com.google.api.client").setLevel(Level.OFF);
	    accountManager = new GoogleAccountManager(this);
//	    gotAccount(savedInstanceState);
        
        
        
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
        else if (requestCode == REQUEST_AUTHENTICATE && resultCode == Activity.RESULT_OK) {
	        if (resultCode == RESULT_OK) {
	            gotAccount(data.getExtras());
	          } else {
	            chooseAccount();
	          }
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
				//gotAccount(this.getIntent().getExtras());
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
	
	void setAuthToken(String authToken) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_AUTH_TOKEN, authToken);
		editor.commit();
		credential.setAccessToken(authToken);
	}

	void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		this.accountName = accountName;
	}

	void onAuthToken() {
		try {
			Set<String> scopes = new HashSet<String>();
			scopes.add(PredictionScopes.DEVSTORAGE_FULL_CONTROL);
			scopes.add(PredictionScopes.DEVSTORAGE_READ_ONLY);
			scopes.add(PredictionScopes.DEVSTORAGE_READ_WRITE);
			scopes.add(PredictionScopes.PREDICTION);
			GoogleAccountCredential accountCredential = GoogleAccountCredential.usingOAuth2(this, scopes);
			accountCredential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
			client = new com.google.api.services.prediction.Prediction.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
			// test
			final Input input = new Input();
			InputInput inputInput = new InputInput();
			List<Object> params = new ArrayList<Object>();
			params.add("Como se llama?");
			inputInput.setCsvInstance(params);
			input.setInput(inputInput);
			// Thread t = new Thread(new Runnable() {
			// @Override
			// public void run() {
			// try {
			// Output output = client.hostedmodels().predict("414649711441", "sample.languageid", input).execute();
			// System.out.println(output.toPrettyString());
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			// });
			// t.start();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void chooseAccount() {
		accountManager.getAccountManager().getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null, LogInActivity.this, null, null, new AccountManagerCallback<Bundle>() {

			public void run(AccountManagerFuture<Bundle> future) {
				Bundle bundle;
				try {
					bundle = future.getResult();
					setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
					setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
					onAuthToken();
				} catch (OperationCanceledException e) {
					// user canceled
				} catch (AuthenticatorException e) {
					Log.e("Oauth", e.getMessage(), e);
				} catch (IOException e) {
					Log.e("Oauth", e.getMessage(), e);
				}
			}
		}, null);
		//TODO: make fancy layout like our app
		// CustomDialogBuilder builder = new CustomDialogBuilder(this);
		// builder.setTitle("Choose an Account");
		// final Account[] accounts = accountManager.getAccountManager().getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		// final int size = accountManager.getAccounts().length;
		// String[] names = new String[size];
		// for (int i = 0; i < size; i++)
		// names[i] = accounts[i].name;
		// builder.setItems(names, new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// setAccountName(accounts[which].name);
		// //setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
		// //setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
		// onAuthToken();
		// }
		// }).show();
	}

	void gotAccount(Bundle b) {
		Account account = accountManager.getAccountByName(accountName);
		if (account == null) {
			chooseAccount();
			return;
		}
		if (credential.getAccessToken() != null) {
			onAuthToken();
			return;
		}
		accountManager.getAccountManager().getAuthToken(account, AUTH_TOKEN_TYPE, b, true, new AccountManagerCallback<Bundle>() {
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle bundle = future.getResult();
					if (bundle.containsKey(AccountManager.KEY_INTENT)) {
						Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
						intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivityForResult(intent, REQUEST_AUTHENTICATE);
					} else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
						setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
						onAuthToken();
					}
				} catch (Exception e) {
					Log.e("Oauth", e.getMessage(), e);
				}
			}
		}, null);
	}
}
