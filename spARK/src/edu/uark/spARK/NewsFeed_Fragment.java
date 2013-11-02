package edu.uark.spARK;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class NewsFeed_Fragment extends Fragment {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

	public static NewsFeed_Fragment newInstance(String param1, String param2) {
		NewsFeed_Fragment fragment = new NewsFeed_Fragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public NewsFeed_Fragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// TODO: Rename method, update argument and hook method into UI event

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);

	    View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
	    ListView listView = (ListView) view.findViewById(R.id.ListView1);
	    //listView.setBackgroundResource(R.color.grey_bg);
	    // An invisible view added as a header to the list and clicking it leads to the mapfragment
	    TextView invisibleView = new TextView(inflater.getContext());
	    invisibleView.setBackgroundColor(Color.TRANSPARENT);
	    invisibleView.setHeight(300);
	    invisibleView.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	//set focus to the map fragment
	        	MapView_Fragment f = (MapView_Fragment) getFragmentManager().findFragmentById(R.id.map_fragment);
	        	NewsFeed_Fragment n = (NewsFeed_Fragment) getFragmentManager().findFragmentById(R.id.news_fragment);

	        	FragmentManager fm = getFragmentManager();
	        	FragmentTransaction ft = fm.beginTransaction();
	        	ft.addToBackStack(null);
	        	//animations are ordered (enter, exit, popEnter, popExit)
	        	ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, 
	        			android.R.animator.fade_in, android.R.animator.fade_out)
	        	.hide(n).commit();
	                                                                        }
	    });
	    listView.addHeaderView(invisibleView);
	    listView.setAdapter(new NewsFeedAdapter(getActivity().getApplicationContext()));
		return view;
	}
	
	    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
