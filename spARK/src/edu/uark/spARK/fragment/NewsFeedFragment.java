package edu.uark.spARK.fragment;

import java.sql.Timestamp;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemLongClickListener;
import edu.uark.spARK.PullToRefreshListView;
import edu.uark.spARK.R;
import edu.uark.spARK.SwipeDismissListViewTouchListener;
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.R.animator;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import edu.uark.spARK.SwipeDismissListViewTouchListener.DismissCallbacks;
import edu.uark.spARK.activity.MainActivity;
import edu.uark.spARK.arrayadapter.NewsFeedArrayAdapter;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.entity.*;
import edu.uark.spARK.view.SelectiveViewPager;


public class NewsFeedFragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    
	
	private SelectiveViewPager mPager;
	private NewsFeedArrayAdapter mAdapter; 
    private PullToRefreshListView mListView;
    
    public ArrayList<Content> arrayListContent = new ArrayList<Content>();
    private JSONArray contents = null;
    private JSONArray comments = null;
    
	public static NewsFeedFragment newInstance(String param1, String param2) {
		NewsFeedFragment fragment = new NewsFeedFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
    public static NewsFeedFragment newInstance(int num) {
    	NewsFeedFragment f = new NewsFeedFragment();

        // Supply num input as an argument (0 for discussion fragment, 1 for bulletin).
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
    
	public NewsFeedFragment() {
		
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mAdapter = new NewsFeedArrayAdapter(getActivity(), R.layout.discussion_list_item, arrayListContent, this);
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadContent();
				//update map with markers of discussions/bulletins nearby
				//MainActivity.mMapViewFragment.getLocationClient().connect();
			}
			
		});
        loadContent();
        
        final SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(mListView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
               public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                   for (int position : reverseSortedPositions) {
                	   
                	   int contentID = ((Content) listView.getItemAtPosition(position)).getId();
                	   String contentType = null;
                	   
                	   // TODO: Added quick and dirty method to grab Content Type. May want to change this later
                	   if (((Content) listView.getItemAtPosition(position)).getClass().toString().contains("Discussion")) {
                		   contentType = "Discussion";
                	   } else if (((Content) listView.getItemAtPosition(position)).getClass().toString().contains("Bulletin")) {
                		   contentType = "Bulletin";
                	   }
                       JSONQuery jquery = new JSONQuery(NewsFeedFragment.this);
                       jquery.execute(ServerUtil.URL_BLOCK_CONTENT, "Block", Integer.toString(MainActivity.myUserID), Integer.toString(contentID), contentType);
                       mAdapter.remove(((Content) listView.getItemAtPosition(position)));	//we need to ignore both the refresh header and map header, that's why there is a -2

                   }
                   mAdapter.notifyDataSetChanged();
                }

				@Override
				public boolean canDismiss(int position) {
					//ignore header views
					if (position > 1)
						return true;
					else
						return false;
				}
		          });
        
        		 mListView.setOnTouchListener(touchListener);
        		 mListView.setOnScrollListener(touchListener.makeScrollListener());
        		 
        		 mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
						if (v.getId() == R.id.list_discussionMainFrame) {
							v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
							RelativeLayout darkenTop = (RelativeLayout) v.getRootView().findViewById(R.id.darkenScreenTop);
			                ImageView darkenBottom = (ImageView) v.getRootView().findViewById(R.id.darkenScreenBottom);
			                LayoutParams paramsTop = darkenTop.getLayoutParams();
			                LayoutParams paramsBottom = darkenBottom.getLayoutParams();
			                paramsTop.height = v.getTop();
			                paramsBottom.height = parent.getHeight()-(v.getTop()+v.getHeight());
			                darkenTop.setLayoutParams(paramsTop);
			                darkenBottom.setLayoutParams(paramsBottom);
			                
			                darkenTop.animate()
			                .alpha(.75f)
			                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
			                .setListener(null);
			                darkenBottom.animate()
			                .alpha(.75f)
			                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
			                .setListener(null);
			                touchListener.setEnabled(true);
			                touchListener.setLongClickActive(true);
			                
						}
						return false;
					}
        			 
        		 });
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.list_feed, container, false);
		
        mPager = (SelectiveViewPager) getActivity().findViewById(R.id.pager);
		
		mListView = (PullToRefreshListView) v.findViewById(R.id.pullToRefreshListView);
		
		mListView.mapHeader.setOnClickListener(new OnClickListener() {

			@Override
	        public void onClick(View v) {
				hideFragment();
	        }
	    });
	    final float scale = v.getContext().getResources().getDisplayMetrics().density;
	    int pixels = (int) (100 * scale + 0.5f);
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
		String contentType = null;
		//get int from instantiating the content fragment type
		int pos = getArguments().getInt("num");
		
		//int tab_position = getActivity().getActionBar().getSelectedTab().getPosition();
		if (pos == 0) {
			contentType = "Discussion";
		} else if (pos == 1){
			contentType = "Bulletin";
		}	
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_LOAD_ALL_POSTS, MainActivity.myUsername, contentType);
	}
	
	@Override
	public void processFinish(JSONObject result) {
		try {
			int success = result.getInt(ServerUtil.TAG_SUCCESS);

			if (success == 1) {
				//MainActivity.mMapViewFragment.clearMarkers();
				arrayListContent.clear();
				// Get Array of discussions
				contents = result.getJSONArray(ServerUtil.TAG_CONTENTS);

				for (int i = 0; i < contents.length(); i++) {
					JSONObject content = contents.getJSONObject(i);

					// Content
					int contentID = Integer.parseInt(content.getString(ServerUtil.TAG_ID));
					String contentTitle = content.getString(ServerUtil.TAG_TITLE).trim();
					String contentBody = content.getString(ServerUtil.TAG_BODY).trim();
					String contentType = content.getString(ServerUtil.TAG_TYPE).trim(); 
					Date contentTimestamp = Timestamp.valueOf(content.getString(ServerUtil.TAG_TIMESTAMP).trim());
					//boolean favorited = content.getBoolean(ServerUtil.TAG_FAVORITE); 
					String latitude = content.getString(ServerUtil.TAG_LATITUDE).trim();     
					String longitude = content.getString(ServerUtil.TAG_LONGITUDE).trim(); 
					int totalRating = 0;
					int userRating = 0;
					if (content.getInt(ServerUtil.TAG_RATING_TOTAL_FLAG) == 1) {
						if (content.getString(ServerUtil.TAG_RATING_TOTAL) != null) {
							totalRating = Integer.parseInt(content.getString(ServerUtil.TAG_RATING_TOTAL));
						} 
					}
					
					
					// Creator
					int contentUserID = Integer.parseInt(content.getString(ServerUtil.TAG_USER_ID).trim());
					String contentUsername = content.getString(ServerUtil.TAG_USER_NAME).trim();
					String contentUserFullName = content.getString(ServerUtil.TAG_USER_FULL_NAME).trim();
					String contentUserDesc = content.getString(ServerUtil.TAG_USER_DESC).trim();
					if (content.getInt(ServerUtil.TAG_USER_RATING_FLAG) == 1) {
						if (content.getString(ServerUtil.TAG_USER_RATING) != null) {
							userRating = content.getInt(ServerUtil.TAG_USER_RATING);
						}
					}
					User user = new User(contentUserID, contentUsername, "", contentUserFullName, contentUserDesc, 0);
					
					
					// Group Attached
					int groupID = Integer.parseInt(content.getString(ServerUtil.TAG_GROUP_ID).trim());
					String groupName = content.getString(ServerUtil.TAG_GROUP_NAME).trim();
					String groupDesc = content.getString(ServerUtil.TAG_GROUP_DESC).trim();
					Group group = new Group(groupID, groupName, groupDesc);
					
					
					if (contentType.equals("Bulletin")) {
						Bulletin b = new Bulletin(contentID, contentTitle, contentBody, user, contentTimestamp, latitude, longitude, group);
						b.setTotalRating(totalRating);
						b.setUserRating(userRating);
						if (b.hasLocation()) {
							MainActivity.mMapViewFragment.addContent(b, true);
						}
						arrayListContent.add(b);
					} else if (contentType.equals("Discussion")) {
						comments = content.getJSONArray(ServerUtil.TAG_COMMENTS);
						List<Comment> commentsList = new ArrayList<Comment>();
						for (int j = 0; j < comments.length(); j++) {
							JSONObject comment = comments.getJSONObject(j);
	
							int commentID = Integer.parseInt(comment.getString(ServerUtil.TAG_ID).trim());
							int commentUserID = Integer.parseInt(comment.getString(ServerUtil.TAG_USER_ID).trim());
							String commentUsername = comment.getString(ServerUtil.TAG_USER_NAME).trim();
							String commentUserFullName = content.getString(ServerUtil.TAG_USER_FULL_NAME).trim();
							String commentUserDesc = content.getString(ServerUtil.TAG_USER_DESC).trim();
							String commentBody = comment.getString(ServerUtil.TAG_BODY).trim();
							Date commentTimestamp = Timestamp.valueOf(content.getString(ServerUtil.TAG_TIMESTAMP).trim());
							
							Comment c = new Comment(commentID, commentBody, new User(commentUserID, commentUsername,  commentUserFullName, commentUserDesc, "", 0), commentTimestamp);
							commentsList.add(c);
						}
						
						Discussion d = new Discussion(contentID, contentTitle, contentBody, user, contentTimestamp, latitude, longitude, commentsList, group);
						d.setTotalRating(totalRating);
						d.setUserRating(userRating);
						if (d.hasLocation()) {
							MainActivity.mMapViewFragment.addContent(d, true);
						}
						arrayListContent.add(d);
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		((MainActivity)getActivity()).updateMapMarkers();
		mListView.onRefreshComplete();
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
		         
			 }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle cancel
        	
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();

	}
	
	@Override
	public void onStop() {
		super.onStop();

	}
	
	public SelectiveViewPager getSelectiveViewPager() {
		return mPager;
	}
	
	public void hideFragment() {
    	FragmentManager fm = getFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	
    	//animations are ordered (enter, exit, popEnter, popExit)
    	ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, 
    			R.animator.slide_up, R.animator.slide_down)
    			.hide(MainActivity.mDiscussionFragment)
    			.hide(MainActivity.mBulletinFragment).addToBackStack("Map").commit();
    	fm.executePendingTransactions();
    	mPager.setPaging(false);
    	MainActivity.mMapViewFragment.zoomInMap();

	}
	

}