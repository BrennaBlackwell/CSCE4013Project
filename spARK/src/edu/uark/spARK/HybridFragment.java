package edu.uark.spARK;

import java.util.ArrayList;
import java.util.Date;

import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.User;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;


//DON'T USE THIS CLASS
@SuppressLint("NewApi")
public class HybridFragment extends Fragment {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    //Our references to the mapview fragment
	private static ListFeed_Fragment mListFragment;
	
    private ArrayList<Content> mArrayList = new ArrayList<Content>();    
    private ListFeedArrayAdapter mAdapter;
    PullToRefreshListView mListView;
	
	public static HybridFragment newInstance(String param1, String param2) {
		HybridFragment fragment = new HybridFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

    
	public HybridFragment() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);;
	}	
    
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		super.onResume();	
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}
}
