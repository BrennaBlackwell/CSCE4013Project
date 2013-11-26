package edu.uark.spARK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;



public class MapView_Fragment extends MapFragment implements 
GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener {

	
	// Google Map
    private GoogleMap map;	//this is the map which is instantiated in the initializeMap();
//    private View v;
    private LocationClient mLocationClient;

    
    public MapView_Fragment() {

    }
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(this.getActivity(), this, this);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
    	View v = super.onCreateView(inflater, container, savedInstanceState);
//    	View view = inflater.inflate(R.layout.fragment_map_view, container, false);
//         if (v.findViewById(R.id.FrameLayout1) == null)
//        	 System.out.println("ID NULL");
        //container.addView(fl);
//        mListFragment = new ListFeed_Fragment();
//        getChildFragmentManager().beginTransaction()
//        .add(R.id.FrameLayout1, mListFragment).commit();
        try {
        	initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
//	    final float scale = this.getResources().getDisplayMetrics().density;
//	    int pixels = (int) (100 * scale + 0.5f);
//	    v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, pixels));
    	return v;
    }       	

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
    		map = getMap();
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            map.setMyLocationEnabled(true);

    }
    
    @Override
	public void onStart() {
        super.onStart();
        // Connect the client.
        if(isGooglePlayServicesAvailable()){
            mLocationClient.connect();
        }

    }
    
    @Override
	public void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
 
    @Override
	public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onPause() {
    	super.onPause();

    }
        
    @Override
    public void onDestroyView() { 	
        super.onDestroyView();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this.getActivity(),
                        9000);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
           Toast.makeText(getActivity().getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

	@Override
	public void onConnected(Bundle dataBundle) {
	    // Display the connection status
	    Toast.makeText(this.getActivity(), "Connected to gps services", Toast.LENGTH_SHORT).show();
	    Location location = mLocationClient.getLastLocation();
	    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	    CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(latLng)
		.zoom(18)
		.build();
	    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	    //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
	    //map.animateCamera(cameraUpdate);
	}

	@Override
	public void onDisconnected() {
	    // Display the connection status
	    Toast.makeText(this.getActivity(), "Disconnected from gps services. Please re-connect.",
	            Toast.LENGTH_SHORT).show();
	}
	
	private boolean isGooglePlayServicesAvailable() {
	    // Check that Google Play services is available
	    int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
	    // If Google Play services is available
	    if (ConnectionResult.SUCCESS == resultCode) {
	        // In debug mode, log the status
	        Log.d("Location Updates", "Google Play services is available.");
	        return true;
	    } else {

	        return false;
	    }
	}
    
	@Override
	public void onActivityResult(
	                int requestCode, int resultCode, Intent data) {
	    // Decide what to do based on the original request code
	    switch (requestCode) {

	        case 9000:
	            /*
	             * If the result code is Activity.RESULT_OK, try
	             * to connect again
	             */
	            switch (resultCode) {
	                case Activity.RESULT_OK:
	                    mLocationClient.connect();
	                    break;
	            }

	    }
	}
}
