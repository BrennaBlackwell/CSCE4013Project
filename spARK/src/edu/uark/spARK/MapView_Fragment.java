package edu.uark.spARK;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.*;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
    private HashMap<Marker, Content> contentMap = new HashMap<Marker, Content>();
    private Marker lastClicked;
	private float scale, pixels;
    
    public MapView_Fragment() {

    }
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scale = this.getResources().getDisplayMetrics().density;
        pixels = (int) (100 * scale + 0.5f);
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
            map.setInfoWindowAdapter(new MyInfoWindowAdapter());
            map.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
//			        if (lastClicked != null && lastClicked.equals(marker)) {
//			            lastClicked = null;
//			            marker.hideInfoWindow();
//			            return true;
//			        } else {
			            lastClicked = marker;
//			            return false;
//			        }
					return false;
				}
            	
            });
            map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					if (contentMap.get(marker) instanceof Discussion) {
						Intent i = new Intent(getActivity(), CommentActivity.class);
						i.putExtra("Object", (Content) contentMap.get(marker));
						MapView_Fragment.this.startActivityForResult(i, contentMap.get(marker).getId());	
					}
					else if (contentMap.get(marker) instanceof Bulletin) {
						//	
					}
					
				}
            	
            });
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
           //Toast.makeText(getActivity().getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

	@Override
	public void onConnected(Bundle dataBundle) {
	    // Display the connection status
	    //Toast.makeText(this.getActivity(), "Connected to gps services", Toast.LENGTH_SHORT).show();
	    LatLng latLng = getCurrentLatLng();
	    if (latLng == null) {
	    	latLng = map.getCameraPosition().target;
	    }
	    CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(latLng)
		.zoom(16)
		.build();
	    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	@Override
	public void onDisconnected() {
	    // Display the connection status
	    //Toast.makeText(this.getActivity(), "Disconnected from gps services. Please re-connect.",
	            //Toast.LENGTH_SHORT).show();
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
    	if (lastClicked != null)
    		lastClicked.hideInfoWindow();
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
	
	public void addContent(Content c, boolean visible) {
		LatLng latLng = new LatLng(Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude()));
		MarkerOptions markerOptions = new MarkerOptions()
		.position(latLng)
		.visible(visible)
		.title(c.getTitle());
		
		Marker marker = map.addMarker(markerOptions);
		
		if (c instanceof Discussion) {	
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_discussion));
			discussionMarkers.add(marker);
		} else if (c instanceof Bulletin) {
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bulletin));
			bulletinMarkers.add(marker);
		} else if (c instanceof Group) {
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_group));
			groupMarkers.add(marker);
		}
		contentMap.put(marker, c);
	}
	
	public void clearMarkers() {
		for(Marker marker : discussionMarkers) {
			marker.remove();
		}
		for(Marker marker : bulletinMarkers) {
			marker.remove();
		}
		for(Marker marker : groupMarkers) {
			marker.remove();
		}
		discussionMarkers.clear();
		bulletinMarkers.clear();
		groupMarkers.clear();
	}
	
	public void updateMarkers(int position) {
		if (position == 0) {
			for (Marker marker : discussionMarkers) {
				marker.setVisible(true);
			}
			for (Marker marker : bulletinMarkers) {
				marker.setVisible(false);
			}
		}
		else if (position == 1) {
			for (Marker marker : discussionMarkers) {
				marker.setVisible(false);
			}
			for (Marker marker : bulletinMarkers) {
				marker.setVisible(true);
			}
		}
	}
	
	public void moveCameraToLatLng(LatLng latLng) {
	    CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(latLng)
		.zoom(17)
		.build();
	    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	class MyInfoWindowAdapter implements InfoWindowAdapter {
		
		private final View mContentView;
		MyInfoWindowAdapter() {
			mContentView = getActivity().getLayoutInflater().inflate(R.layout.map_info_contents, null);
		}
		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			Content c = contentMap.get(marker);
			TextView title = (TextView) mContentView.findViewById(R.id.headerTextView);
			TextView dateAndGroup = (TextView) mContentView.findViewById(R.id.groupAndDateTextView);
			TextView desc = (TextView) mContentView.findViewById(R.id.descTextView);
			TextView comments = (TextView) mContentView.findViewById(R.id.commentTextView);
			TextView rating = (TextView) mContentView.findViewById(R.id.totalScoreTextView);
			ToggleButton likeBtn = (ToggleButton) mContentView.findViewById(R.id.likeBtn);
			ToggleButton dislikeBtn = (ToggleButton) mContentView.findViewById(R.id.dislikeBtn);
			title.setText(c.getTitle());
			desc.setText(c.getText());
			//loc.setText(c.getLatitude() + ", " + c.getLongitude());
			rating.setText(String.valueOf(c.getTotalRating()));
			if (c.getUserRating() == 1) {
				likeBtn.setChecked(true);
				dislikeBtn.setChecked(false);
			} else if (c.getUserRating() == -1) {
				likeBtn.setChecked(false);
				dislikeBtn.setChecked(true);
			} else {
				likeBtn.setChecked(false);
				dislikeBtn.setChecked(false);
			}
			dateAndGroup.setText("posted publicly - " + c.getCreationDateAsPrettyTime());
			if (c.getGroupAttached().getId() != 0) {
				dateAndGroup.setText("posted to '" + c.getGroupAttached().getTitle() + "' - " + c.getCreationDateAsPrettyTime());
			}
			if (c instanceof Discussion)
				comments.setText(((Discussion) c).getNumComments() + " comments");
			else if (c instanceof Bulletin)
				comments.setText("");
			return mContentView;
		}
		
	}
	
	public LatLng getCurrentLatLng() {
		Location location;
		LatLng latLng = null;
		if (map.getMyLocation() == null) {
		    location = mLocationClient.getLastLocation();
		    latLng = new LatLng(location.getLatitude(), location.getLongitude());
		}
		
		return latLng;
	}
}
