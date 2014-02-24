package edu.uark.spARK.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(edu.uark.spARK.R.xml.preferences);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setTitle("Settings");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

	    // get selected item
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		onBackPressed();
	    		break;
    		default:
    			break;
	    }
	    return true;
    }
}
