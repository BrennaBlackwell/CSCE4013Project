package edu.uark.spARK;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.User;

public class ListFeed_Fragment extends Fragment {
	
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    private ArrayList<Content> arrayListContent = new ArrayList<Content>();
	private ListFeedArrayAdapter mAdapter; 
    private static PullToRefreshListView mListView;
    private static Bundle args;
    
	public static ListFeed_Fragment newInstance(String param1, String param2) {
		ListFeed_Fragment fragment = new ListFeed_Fragment();
		args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
	public ListFeed_Fragment() {
		
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mAdapter = new ListFeedArrayAdapter(getActivity(), R.layout.discussion_list_item, arrayListContent, this);		
		mListView.setAdapter(mAdapter);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		if (getArrayListContent().size() == 0)
			loadContent();
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		//View v = super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.list_feed, container, false);
//		mListView = new PullToRefreshListView(container.getContext());
		mListView = (PullToRefreshListView) v.findViewById(R.id.pullToRefreshListView);
//		mListView = new PullToRefreshListView(inflater.getContext());
//		mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    //set mapHeader clicklistener so the listview can be hidden
	    mListView.mapHeader.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
        	//set focus to the map fragment
        	MapView_Fragment f = (MapView_Fragment) getFragmentManager().findFragmentById(R.id.map);
        	FragmentManager fm = getFragmentManager();
        	FragmentTransaction ft = fm.beginTransaction();
        	
        	ft.addToBackStack(null);
        	//animations are ordered (enter, exit, popEnter, popExit)
        	ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, 
        			R.animator.slide_up, R.animator.slide_down)
        	.hide(getFragmentManager().findFragmentById(R.id.fragment_frame)).commit();                                                                      
        }
    });
        mListView.setOnRefreshListener(new OnRefreshListener() {

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
                    mListView.postDelayed(new Runnable() {

                            
                            @Override
                            public void run() {
                                    mListView.onRefreshComplete();
                            }
                    }, 2000);
            }
    });
//        mListView.setOnItemClickListener(new OnItemClickListener()
//        {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int id,
//					long position) {
//				Intent i = new Intent((MainActivity) getActivity(), CommentActivity.class);
//				System.out.println("getActivity: " + getActivity());
//				i.putExtra("Object", (Content) arrayListContent.get(id));
//				System.out.println("Object: " + arrayListContent.get(id));
//				System.out.println("position: " + id);
//				i.putExtra("position", id);
//				startActivityForResult(i, 1);						
//			}
//        	
//        });
		return v;

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
		User u1 = new User("user1");
		User u2 = new User("user2");
		User u3 = new User("user3");
		Discussion d1 = new Discussion(new User("bpanda"), 
				"Free Cheeseburgers in the Union!", 
				"Burger Shack is providing free cheeseburgers at the Union!", 
				"Campus Free Food",
				new Date(113, 11, 10, 12, 3));
		getArrayListContent().add(d1);
		getArrayListContent().add(new Discussion(new User("bpanda"), 
				"Yesterday's Game", 
				"C'mon Razorbacks! What was that yesterday? \n\nThat's it. I give up. I'm just going to retire.", 
				"Football",
				new Date(113, 11, 10, 15, 30)));		
		getArrayListContent().add(new Discussion(new User("fred"), 
				"SSBM Tournament", 
				"Super Smash Brothers Melee tournament at the Union Student Technology room. \n\nBring your own controllers!", 
				"Games",
				new Date(113, 11, 6, 9, 15)));
		getArrayListContent().add(new Bulletin(new User("crazyal"), 
				"10% Off All Posters!", 
				"Git over to the Student union for some cr-rrazy deals! \n\nWe got all kinds of posters! \n" + 
				"Everything from: \nBattlestar Galactica \nBreaking Bad (You're ******* right!) \n" + 
						"Miley Cyrus twerkin! \nFamous people gettin married that you don't really care about! \n" + 
						"And of course, food! \n   (*not actual food, posters of food)"
				+ "\nSale ends this Pos-ThursDay!",
				"Campus On Sale",
				new Date(113, 11, 1, 12, 36)));
		getArrayListContent().add(new Discussion(new User("hermanp"), 
				"Algorithms", 
				"I know what you are, but what am I? \n\n" + 
				"Why dont'cha make me?!",
				"Algorithms",
				new Date(113, 11, 4, 11, 9)));
		
		//add some comments
		d1.getCommentList().add(new Comment(u1, "ALRIGHT! FREE CHEESEBURGERS!"));
		d1.getCommentList().add(new Comment(u2, "Yes now I can forget all about that terrible Algorithms test!"));
	}

	
	public PullToRefreshListView getListView() {
		return mListView;
	}
	
	public ListFeedArrayAdapter getListAdapter() {
		return mAdapter;
	}

	public ArrayList<Content> getArrayListContent() {
		return arrayListContent;
	}

	public void setArrayListContent(ArrayList<Content> arrayListContent) {
		this.arrayListContent = arrayListContent;
	}
	
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        Log.i("FragmentList", "Item clicked: " + id);
//    }

	//onActivityResult which is received by the fragment
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Result not being pass to either activity or fragment for some reason after finishing commentActivity
		if (requestCode == 1) {
			 if(resultCode == Activity.RESULT_OK){
		         //notify array adapter of new value
				 Discussion d = (Discussion) intent.getSerializableExtra("Object");
				 int p = intent.getIntExtra("position", -1);
				 	if (p > -1) {
				 		getArrayListContent().set(p, d);
				 		getListAdapter().notifyDataSetChanged();
				 	}
		         
		         //load from server here like in hw???
//		         ContentResolver cr = getContentResolver();
//		         ContentValues values = new ContentValues();
//		         
//		         values.put(RazorSquareContentProvider.KEY_USER, username);
//		         values.put(RazorSquareContentProvider.KEY_DESC, e.getDescription());
//		         values.put(RazorSquareContentProvider.KEY_TIMESTAMP, e.getCreatedDateAsInt());
//		         cr.insert(RazorSquareContentProvider.CONTENT_URI, values);
//		         getLoaderManager().restartLoader(0, null, this);
		         
			 }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle cancel
        }
	}
}
