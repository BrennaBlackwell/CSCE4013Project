package edu.uark.spARK;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.User;


public class NewsFeed_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    private static final String TAG_SUCCESS = "success";
	private static final String TAG_CONTENTS = "contents";
	private static final String TAG_COMMENTS = "comments";
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_BODY = "body";
	private static final String TAG_TYPE = "type";
	private static final String TAG_TIMESTAMP = "timestamp";
	private static final String TAG_USER_ID = "userid";
	private static final String TAG_USER_NAME = "username";
	private static final String TAG_RATING_TOTAL = "rating_total";
	private static final String TAG_RATING_TOTAL_FLAG = "rating_total_flag";
	private static final String TAG_USER_RATING = "user_rating";
	private static final String TAG_USER_RATING_FLAG = "user_rating_flag";
	
	private SelectiveViewPager mPager;
	private NewsFeedArrayAdapter mAdapter; 
    private static PullToRefreshListView mListView;
    
    public ArrayList<Content> arrayListContent = new ArrayList<Content>();
    private JSONArray contents = null;
    private JSONArray comments = null;

    
	public static NewsFeed_Fragment newInstance(String param1, String param2) {
		NewsFeed_Fragment fragment = new NewsFeed_Fragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
    static NewsFeed_Fragment newInstance(int num) {
    	NewsFeed_Fragment f = new NewsFeed_Fragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
    
	public NewsFeed_Fragment() {
		
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mAdapter = new NewsFeedArrayAdapter(getActivity(), R.layout.discussion_list_item, arrayListContent, this);		
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
		
		View v = inflater.inflate(R.layout.list_feed, container, false);
//		mListView = new PullToRefreshListView(container.getContext());
        mPager = (SelectiveViewPager) getActivity().findViewById(R.id.pager);
        getFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {

			@Override
			public void onBackStackChanged() {
				if (getFragmentManager().getBackStackEntryCount() == 0)
					mPager.setPaging(true);
				
			}
        	
        });
		
		mListView = (PullToRefreshListView) v.findViewById(R.id.pullToRefreshListView);
	    
		
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
	        			.hide(NewsFeed_Fragment.this).commit();
	        	mPager.setPaging(false);
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
//	                    mListView.postDelayed(new Runnable() {
//	                    	
//	                            
//	                            @Override
//	                            public void run() {
//	                                    mListView.onRefreshComplete();
//	                            }
//	                    }, 2000);
	            }
	    });
//		mListView = new PullToRefreshListView(inflater.getContext());
//		mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    //set mapHeader clicklistener so the listview can be hidden

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
		String contentType = "Discussion";
		int tab_position = getActivity().getActionBar().getSelectedTab().getPosition();
		
		if (tab_position == 0) {
			contentType = "Discussion";
		} else if (tab_position == 1) {
			contentType = "Bulletin";
		}
		
		SharedPreferences preferences = this.getActivity().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
		String currentUser = preferences.getString("currentUsername", "");

		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_LOAD_ALL_POSTS, currentUser, contentType);
	}
	
	@Override
	public void processFinish(JSONObject result) {
		arrayListContent.clear();
		try {
			int success = result.getInt(TAG_SUCCESS);

			if (success == 1) {
				// Get Array of discussions
				contents = result.getJSONArray(TAG_CONTENTS);

				for (int i = 0; i < contents.length(); i++) {
					JSONObject content = contents.getJSONObject(i);

					int contentID = Integer.parseInt(content.getString(TAG_ID));
					int contentUserID = Integer.parseInt(content.getString(TAG_USER_ID).trim());
					String contentUsername = content.getString(TAG_USER_NAME).trim();
					String contentTitle = content.getString(TAG_TITLE).trim();
					String contentBody = content.getString(TAG_BODY).trim();
					String contentType = content.getString(TAG_TYPE).trim(); 
					Date contentTimestamp = Timestamp.valueOf(content.getString(TAG_TIMESTAMP).trim());
					    
					int totalRating = 0;
					int userRating = 0;
					if (content.getInt(TAG_RATING_TOTAL_FLAG) == 1) {
						if (content.getString(TAG_RATING_TOTAL) != null) {
							totalRating = Integer.parseInt(content.getString(TAG_RATING_TOTAL));
						} 
					}
					if (content.getInt(TAG_USER_RATING_FLAG) == 1) {
						if (content.getString(TAG_USER_RATING) != null) {
							userRating = content.getInt(TAG_USER_RATING);
						}
					}
					
					if (contentType.equals("Bulletin")) {
						Bulletin b = new Bulletin(contentID, contentTitle, contentBody, new User(contentUserID, contentUsername, null), contentTimestamp);
						b.setTotalRating(totalRating);
						b.setUserRating(userRating);
						arrayListContent.add(b);
					} else if (contentType.equals("Discussion")) {
						comments = content.getJSONArray(TAG_COMMENTS);
						List<Comment> commentsList = new ArrayList<Comment>();
						for (int j = 0; j < comments.length(); j++) {
							JSONObject comment = comments.getJSONObject(j);
	
							int commentID = Integer.parseInt(comment.getString(TAG_ID).trim());
							int commentUserID = Integer.parseInt(comment.getString(TAG_USER_ID).trim());
							String commentUsername = comment.getString(TAG_USER_NAME).trim();
							String commentBody = comment.getString(TAG_BODY).trim();
							Date commentTimestamp = Timestamp.valueOf(content.getString(TAG_TIMESTAMP).trim());
							
							//String comment_timestamp = comment.getString(TAG_TIMESTAMP).trim();
							User user = new User(commentUserID, commentUsername, null);
							Comment c = new Comment(commentID, commentBody, user, commentTimestamp);
							
							commentsList.add(c);
						}
						
						Discussion d = new Discussion(contentID, contentTitle, contentBody, new User(contentUserID, contentUsername, null), contentTimestamp, commentsList);
						d.setTotalRating(totalRating);
						d.setUserRating(userRating);
						arrayListContent.add(d);
					}
					mAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}	
		
	}
	
	public PullToRefreshListView getListView() {
		return mListView;
	}
	
	public NewsFeedArrayAdapter getListAdapter() {
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
