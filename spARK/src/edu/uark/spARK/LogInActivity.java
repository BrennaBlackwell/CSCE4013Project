package edu.uark.spARK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
 
public class LogInActivity extends Activity {
 
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
    	findViewById(R.id.button1).getBackground().setColorFilter(new LightingColorFilter(0xFFFF0000, 0xFFFF0000));
        findViewById(R.id.button2).getBackground().setColorFilter(new LightingColorFilter(0xFFFF0000, 0xFFFF0000));
        new Handler().postDelayed(new Runnable() {
       	 
            @Override
            public void run() {
            	ImageView ivSplash = (ImageView) findViewById(R.id.imageView1);
            	ivSplash.animate().translationY(-450).withLayer();
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
    
	public void login(View v){
		startActivity(new Intent(LogInActivity.this, MainActivity.class));
	}
}