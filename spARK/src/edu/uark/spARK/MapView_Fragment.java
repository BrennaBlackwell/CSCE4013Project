package edu.uark.spARK;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/*We should probably extend MapFragment or SupportMapFragment instead of Fragment so that the class
itself will take care of alot of the map problems we've been having
*/

@SuppressLint("NewApi")
public class MapView_Fragment extends MapFragment{

	
	// Google Map
    private GoogleMap map;	//this is the map which is instantiated in the initializeMap();
//    private View v;
    
    public MapView_Fragment() {

    }
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    }
    
    @Override
    public void onPause() {
    	super.onPause();

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
    
}
