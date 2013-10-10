package edu.uark.spARK;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

public class MapViewFragment extends MapFragment {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public MapViewFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
        //
        //int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()), "drawable", getActivity().getPackageName());
        //((ImageView) rootView.findViewById(R.id.image))
        //getActivity().setTitle(planet);
        return rootView;
    }
}
