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
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.Group;
import edu.uark.spARK.entities.User;


public class NewsFeed_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    
	
	private SelectiveViewPager mPager;
	public static NewsFeedArrayAdapter mAdapter; 
    private PullToRefreshListView mListView;
    
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

        // Supply num input as an argument (0 for discussion fragment, 1 for bulletin).
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
                	   int contentID = mAdapter.getItem(position-2).getId();
                	   String contentType = "Discussion";
                	   
                	   // TODO: Added quick and dirty method to grab Content Type. May want to change this later
                	   if (mAdapter.getItem(position-2).getClass().toString().contains("Discussion")) {
                		   contentType = "Discussion";
                	   } else if (mAdapter.getItem(position-2).getClass().toString().contains("Bulletin")) {
                		   contentType = "Bulletin";
                	   }
                       mAdapter.remove(mAdapter.getItem(position-2));	//we need to ignore both the refresh header and map header, that's why there is a -2
                       
                       JSONQuery jquery = new JSONQuery(NewsFeed_Fragment.this);
                       jquery.execute(ServerUtil.URL_BLOCK_CONTENT, "Block", Integer.toString(MainActivity.myUserID), Integer.toString(contentID), contentType);
                   }
                   
                   mAdapter.notifyDataSetChanged();
                }

				@Override
				public boolean canDismiss(int position) {
					return true;
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
				MainActivity.mMapViewFragment.clearMarkers();
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
							MainActivity.mMapViewFragment.addContent(b);
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
							MainActivity.mMapViewFragment.addContent(d);
						}
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
    			.hide(NewsFeed_Fragment.this).addToBackStack("Map").commit();
    	fm.executePendingTransactions();
    	mPager.setPaging(false);
    	MainActivity.mMapViewFragment.zoomInMap();

	}
}
