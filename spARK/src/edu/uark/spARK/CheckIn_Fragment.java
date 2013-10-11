package edu.uark.spARK;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
