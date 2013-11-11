package edu.uark.spARK;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class CombinedMapNews_Fragment extends Fragment {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public CombinedMapNews_Fragment() {
        // Empty constructor required for fragment subclasses
    }
    
    @Override
 	public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_list_combined, container, false);
    }
    
    @Override
    public void onDestroyView() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(getFragmentManager().findFragmentById(R.id.map));
        transaction.commit();

        super.onDestroyView();
    }
    
}
