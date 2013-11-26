package edu.uark.spARK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/*We should probably extend MapFragment or SupportMapFragment instead of Fragment so that the class
itself will take care of alot of the map problems we've been having
*/

@SuppressLint("NewApi")
public class MapView_Fragment extends MapFragment {

	
	// Google Map
    private LocationManager locationManager;
    private GoogleMap map;	//this is the map which is instantiated in the initializeMap();
    private Criteria criteria = new Criteria();

//    private View v;
    
    public MapView_Fragment() {

    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setSpeedRequired(false);
//        criteria.setCostAllowed(true);

        // String provider = LocationManager.GPS_PROVIDER;
//        String provider = locationManager.getBestProvider(criteria, true);
//        Location loc = locationManager.getLastKnownLocation(provider);

        // updateWithNewLocation(loc);
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
    	return v;
    }       	

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
    		map = getMap();
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            map.setMyLocationEnabled(true);
            //this causes an error message in Logcat
            //map.setMyLocationEnabled(true);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(36.065853, -94.173818)).zoom(18).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            
            //MAP IS ALREADY CREATED SO WE DONT REALLY NEED THIS (DO WE?)
//            // check if map is created successfully or not
//            if (map == null) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        "Map could not be created", Toast.LENGTH_SHORT)
//                        .show();
//            }
    }
 
    @Override
	public void onResume() {
        super.onResume();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
        locationManager.removeUpdates(locationListener);
    }
        
    @Override
    public void onDestroyView() {
    	
        super.onDestroyView();
//        MapFragment m = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//
//        if (m != null)
//        	getFragmentManager().beginTransaction().remove(m).commit();
//
    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            //updateWithNewLocation(location);
        }
    };
    
}

