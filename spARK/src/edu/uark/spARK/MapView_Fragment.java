package edu.uark.spARK;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.google.android.gms.common.*;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import edu.uark.spARK.entities.*;



public class MapView_Fragment extends MapFragment implements 
GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener {

	// Google Map
    private GoogleMap map;	//this is the map which is instantiated in the initializeMap();
//    private View v;
    private LocationClient mLocationClient;
    
    private List<Marker> discussionMarkers = new ArrayList<Marker>();
    private List<Marker> bulletinMarkers = new ArrayList<Marker>();
    private List<Marker> groupMarkers = new ArrayList<Marker>();
    
    public MapView_Fragment() {

    }
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(this.getActivity(), this, this);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
    	final View v = super.onCreateView(inflater, container, savedInstanceState);
        try {
        	initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
	    final float scale = this.getResources().getDisplayMetrics().density;
	    final int pixels = (int) (100 * scale + 0.5f);
	    
	    // caculates the height of the view right after it's created, otherwise getHeight() will return 0.
	    final ViewTreeObserver observer = v.getViewTreeObserver();
	    observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {
	            v.setY((int) (-v.getHeight()/2 + pixels/2));
	            v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	            //this will be called as the layout is finished, prior to displaying.
	        }
	    });
	    
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
		.zoom(16)
		.build();
	    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
	
	//setMapPadding is called in PullToRefreshListView on EVENT_MOVE to move slightly slower than the listview padding size
    public void setMapPadding(int padding){
        final float scale = this.getResources().getDisplayMetrics().density;
        final int pixels = (int) (100 * scale + 0.5f);
        MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) getView().getLayoutParams();
        mlp.setMargins(0, padding, 0, 0);
        mlp.topMargin = padding;
    	this.getView().setY((-getView().getHeight()/2 + pixels/2) + (float) (padding * .72));
    }

    //resets the framelayout when listview is done refreshing
	public void resetView() {
        final float scale = this.getResources().getDisplayMetrics().density;
        final int pixels = (int) (100 * scale + 0.5f);
		this.getView().setY((-getView().getHeight()/2 + pixels/2));
	}

	public void bounceBackMap() {
		// TODO Auto-generated method stub	
	}
	
	public void zoomInMap() {
	    Location location = mLocationClient.getLastLocation();
	    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	    CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(latLng)
		.zoom(17)
		.build();
	    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		getView().animate().y(0).setDuration(250);
	}
	
	public void zoomOutMap() {
        final float scale = this.getResources().getDisplayMetrics().density;
        final int pixels = (int) (100 * scale + 0.5f);

		getView().animate().y((-getView().getHeight()/2 + pixels/2)).setDuration(250);
	    Location location = mLocationClient.getLastLocation();
	    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	    CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(latLng)
		.zoom(16)
		.build();
	    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public LocationClient getLocationClient() {
		return mLocationClient;
	}
	
	public void addContent(Content c) {
		addContent(c, true);
	}
	
	public void addContent(Content c, boolean visible) {
		LatLng latLng = new LatLng(Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude()));
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title(c.getTitle());
		markerOptions.visible(visible);
		
		if(c instanceof Discussion) {
			discussionMarkers.add(map.addMarker(markerOptions));
		} else if (c instanceof Bulletin) {
			bulletinMarkers.add(map.addMarker(markerOptions));
		} else if (c instanceof Group) {
			groupMarkers.add(map.addMarker(markerOptions));
		}
	}
	
	public void updateMarkers(String contentType) {
		boolean isDiscussion = contentType.equalsIgnoreCase("discussions");
		boolean isBulletin = contentType.equalsIgnoreCase("bulletins");
		boolean isGroup = contentType.equalsIgnoreCase("groups");
		
		for(Marker marker : discussionMarkers) {
			marker.setVisible(isDiscussion);
		}
		
		for(Marker marker : bulletinMarkers) {
			marker.setVisible(isBulletin);
		}
		
		for(Marker marker : groupMarkers) {
			marker.setVisible(isGroup);
		}
	}
}
