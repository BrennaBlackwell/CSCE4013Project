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
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Comment;
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
	    mNewsFeedAdapter = new NewsFeedArrayAdapter(getActivity().getApplicationContext(), R.layout.discussion_list, arrayListContent);
	    listView.setAdapter(mNewsFeedAdapter);
        listView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                    // Your code to refresh the list contents goes here

                    // for example:
                    // If this is a webservice call, it might be asynchronous so
                    // you would have to call listView.onRefreshComplete(); when
                    // the webservice returns the data
                    loadContent();
                    
                    // Make sure you call listView.onRefreshComplete()
                    // when the loading is done. This can be done from here or any
                    // other place, like on a broadcast receive from your loading
                    // service or the onPostExecute of your AsyncTask.

                    // For the sake of this sample, the code will pause here to
                    // force a delay when invoking the refresh
                    listView.postDelayed(new Runnable() {

                            
                            @Override
                            public void run() {
                                    listView.onRefreshComplete();
                            }
                    }, 2000);
            }
    });
//        listView.setOnItemClickListener(new OnItemClickListener()
//        {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int id,
//					long position) {
//				System.out.println("on item clicked! " + v.getId());
//				if (v.getId() == R.id.commentTextView)
//					System.out.println("textview selected!");
//				Intent i = new Intent(getActivity(), CommentActivity.class);
//				startActivity(i);
//				
//			}
//        	
//        });
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
	
	public void loadContent() {
		//test
		User u1 = new User(1, "user1", null);
		User u2 = new User(2, "user2", null);
		User u3 = new User(3, "user3", null);
		User bpanda = new User(1, "bpanda", null);
		Discussion d1 = new Discussion(0, "Free Cheeseburgers in the Union!", "Burger Shack is providing free cheeseburgers at the Union!", 
				bpanda, new Date(113, 11, 10, 12, 3));
		arrayListContent.add(d1);
		arrayListContent.add(new Discussion(1, "Yesterday's Game", "C'mon Razorbacks! What was that yesterday? \n\nThat's it. I give up. I'm just going to retire.", 
				bpanda,	new Date(113, 11, 10, 15, 30)));		
				
		//add some comments
		d1.addComment(new Comment(0, null, "ALRIGHT! FREE CHEESEBURGERS!", u1));
		d1.addComment(new Comment(1, null, "Yes now I can forget all about that terrible Algorithms test!", u2));
	}
	
}
