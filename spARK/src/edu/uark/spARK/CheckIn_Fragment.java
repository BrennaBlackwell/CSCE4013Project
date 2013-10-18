package edu.uark.spARK;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CheckIn_Fragment extends Fragment{
	
    public CheckIn_Fragment() {
        // Empty constructor required for fragment subclasses
    }    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_in, container, false);
        return rootView;
    }
    
//    final Button loginButton = (Button) getView().findViewById(R.id.scanButton);
    
/*    public void scanCode(View v) {
        
    }*/

    @Override
    public void onDestroyView() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(getFragmentManager().findFragmentById(R.id.checkin_map));
        transaction.commit();

        super.onDestroyView();
    }



}
