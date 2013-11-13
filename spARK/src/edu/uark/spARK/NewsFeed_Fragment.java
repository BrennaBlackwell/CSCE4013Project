package edu.uark.spARK;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.User;


public class NewsFeed_Fragment extends Fragment {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

    public static ArrayList<Content> arrayListContent = new ArrayList<Content>();
    NewsFeedArrayAdapter mNewsFeedAdapter;
    
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
		loadContent();
	}

	public void loadContent() {
		//test
		arrayListContent.add(new Discussion(new User("bpanda"), 
				"Free Cheeseburgers in the Union!", 
				"Burger Shack is providing free cheeseburgers at the Union!", 
				"Campus Free Food",
				new Date(113, 11, 10, 12, 3)));
		arrayListContent.add(new Discussion(new User("bpanda"), 
				"Yesterday's Game", 
				"C'mon Razorbacks! What was that yesterday? \n\nThat's it. I give up. I'm just going to retire.", 
				"Football",
				new Date(113, 11, 10, 15, 30)));		
		arrayListContent.add(new Discussion(new User("fred"), 
				"SSBM Tournament", 
				"Super Smash Brothers Melee tournament at the Union Student Technology room. \n\nBring your own controllers!", 
				"Games",
				new Date(113, 11, 6, 9, 15)));
		arrayListContent.add(new Bulletin(new User("crazyal"), 
				"10% Off All Posters!", 
				"Git over to the Student union for some cr-rrazy deals! \n\nWe got all kinds of posters! \n" + 
				"Everything from: \nBattlestar Galactica \nBreaking Bad (You're ******* right!) \n" + 
						"Miley Cyrus twerkin! \nFamous people gettin married that you don't really care about! \n" + 
						"And of course, food! \n   (*not actual food, posters of food)"
				+ "\nSale ends this Pos-ThursDay!",
				"Campus On Sale",
				new Date(113, 11, 1, 12, 36)));
		arrayListContent.add(new Discussion(new User("hermanp"), 
				"Algorithms", 
				"I know what you are, but what am I? \n\n" + 
				"Why dont'cha make me?!",
				"Algorithms",
				new Date(113, 11, 4, 11, 9)));
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);

	    View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
	    final PullToRefreshListView listView = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);
	    //set mapHeader clicklistener so the listview can be hidden
	    listView.mapHeader.setOnClickListener(new OnClickListener() {
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
	    mNewsFeedAdapter = new NewsFeedArrayAdapter(getActivity().getApplicationContext(), R.layout.list_discussion, arrayListContent);
	    listView.setAdapter(mNewsFeedAdapter);

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
