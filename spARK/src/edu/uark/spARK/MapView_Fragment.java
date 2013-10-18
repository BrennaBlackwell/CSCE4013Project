package edu.uark.spARK;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapView_Fragment extends Fragment{

	
	// Google Map
    private GoogleMap map;
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        try {
            // Loading map
        	initializeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_map_view, container, false);
    }
    

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            map.setMyLocationEnabled(true);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(36.065853, -94.173818)).zoom(18).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            
            // check if map is created successfully or not
            if (map == null) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Map could not be created", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 
    @Override
	public void onResume() {
        super.onResume();
        initializeMap();
    }
        
    @Override
    public void onDestroyView() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(getFragmentManager().findFragmentById(R.id.map));
        transaction.commit();

        super.onDestroyView();
    }
}
