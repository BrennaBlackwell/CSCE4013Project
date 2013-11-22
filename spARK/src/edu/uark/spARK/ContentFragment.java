package edu.uark.spARK;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;

public class ContentFragment extends Fragment {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public ContentFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_feed, container, false);
        //int i = getArguments().getInt(ARG_FRAGMENT_TYPE);
        //String title = getResources().getStringArray(R.array.nav_drawer_title_array)[i];
        //
        //int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()), "drawable", getActivity().getPackageName());
        //((ImageView) rootView.findViewById(R.id.image))
        //getActivity().setTitle(planet);
        return rootView;
    }
}
