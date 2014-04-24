package edu.uark.spARK.fragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import edu.uark.spARK.PullToRefreshListView;
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.R;
import edu.uark.spARK.SwipeDismissListViewTouchListener;
import edu.uark.spARK.activity.MainActivity;
import edu.uark.spARK.arrayadapter.NewsFeedArrayAdapter;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.entity.Bulletin;
import edu.uark.spARK.entity.Comment;
import edu.uark.spARK.entity.Content;
import edu.uark.spARK.entity.Discussion;
import edu.uark.spARK.entity.Event;
import edu.uark.spARK.entity.Group;
import edu.uark.spARK.entity.User;
import edu.uark.spARK.view.SelectiveViewPager;


public class NewsFeedFragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    
	
	private SelectiveViewPager mPager;
	private NewsFeedArrayAdapter mAdapter; 
    private PullToRefreshListView mListView;
    private RelativeLayout loadScreen;
    
    public ArrayList<Content> arrayListContent = new ArrayList<Content>();
    private JSONArray contents = null;
    private JSONArray comments = null;
    
	public static NewsFeedFragment newInstance(String param1, String param2) {
		NewsFeedFragment fragment = new NewsFeedFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
    public static NewsFeedFragment newInstance(int contentType, int sortType) {
    	NewsFeedFragment f = new NewsFeedFragment();

        // Supply int inputs as arguments
        Bundle args = new Bundle();
        args.putInt("content", contentType);
        args.putInt("sort", sortType);
        f.setArguments(args);

        return f;
    }
    
	public NewsFeedFragment() {
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mAdapter = new NewsFeedArrayAdapter(getActivity(), R.layout.content_list_item_discussion, arrayListContent, this);
	   // mListView.setBackgroundResource(R.color.list_item_bg);
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new OnRefreshListener() {
		
			@Override
			public void onRefresh() {
				loadContent();
				//update map with markers of discussions/bulletins nearby
				//MainActivity.mMapViewFragment.getLocationClient().connect();
			}
			
		});
		//TODO: move loadContent to onCreate() so it's not being called every time, still trying to create a proper loading screen that doesnt' happen
		//every time onActivityCreated is called
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
                	   } else if (((Content) listView.getItemAtPosition(position)).getClass().toString().contains("Event")) {
                		   contentType = "Event";
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
		loadScreen = (RelativeLayout) v.findViewById(R.id.load_screen);
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
		//get ints from instantiating the content fragment type
		int content = getArguments().getInt("content");
		String sort = String.valueOf(getArguments().getInt("sort"));
		
		//set content type (THIS VALUE IS POSITION IN NAVLISTDRAWER, so we might change it to a string to make it easier to understand)
		if (content == 2) {
			contentType = "Bulletin";
		} else if (content == 3) {
			contentType = "Event";
		} else if (content == 4){
			contentType = "Discussion";
		}	
		//TODO: implement new content types as they are added (eg events)
		//TODO: set sorting type
		Location cur = null;
		try {
		cur = ((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).getLocationClient().getLastLocation();
		}
		catch (IllegalStateException ise) {
			ise.printStackTrace();
		}
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_LOAD_ALL_POSTS, MainActivity.myUsername, contentType, sort, String.valueOf(cur.getLatitude()), String.valueOf(cur.getLongitude()));
		
	}
	
	@Override
	public void processFinish(JSONObject result) {
    	loadScreen.animate().alpha(0).setDuration(500);
    	mListView.setBackgroundColor(Color.TRANSPARENT);
		try {
			int success = result.getInt(ServerUtil.TAG_SUCCESS);

			if (success == 1) {
				((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).clearMarkers();
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
					boolean favorited = content.getBoolean(ServerUtil.TAG_FAVORITE); 
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
					String contentBase64Image = content.getString(ServerUtil.TAG_USER_PIC).trim();
					Bitmap contentProfilePicture = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.drawer_profile);
					if (!contentBase64Image.isEmpty()) {
						byte[] rawImage = Base64.decode(contentBase64Image, Base64.DEFAULT);
						contentProfilePicture = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
					} 
					
					if (content.getInt(ServerUtil.TAG_USER_RATING_FLAG) == 1) {
						if (content.getString(ServerUtil.TAG_USER_RATING) != null) {
							userRating = content.getInt(ServerUtil.TAG_USER_RATING);
						}
					}
					User user = new User(contentUserID, contentUsername, "", contentUserFullName, contentUserDesc, 0, contentProfilePicture);
					
					// Group Attached
					int groupID = Integer.parseInt(content.getString(ServerUtil.TAG_GROUP_ID).trim());
					String groupName = content.getString(ServerUtil.TAG_GROUP_NAME).trim();
					String groupDesc = content.getString(ServerUtil.TAG_GROUP_DESC).trim();
					Group group = new Group(groupID, groupName, groupDesc);
					
					
					if (contentType.equals("Bulletin")) {
						Bulletin b = new Bulletin(contentID, contentTitle, contentBody, user, contentTimestamp, latitude, longitude, group, favorited);
						b.setTotalRating(totalRating);
						b.setUserRating(userRating);
						if (b.hasLocation()) {
							((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).addContent(b, true);
						}
						arrayListContent.add(b);
					} else if (contentType.equals("Event")) {
						
						// Location
						String location = content.getString(ServerUtil.TAG_LOCATION).trim();
						
						// Dates/Times
						String startDate = content.getString(ServerUtil.TAG_STARTDATE).trim();
						String startTime = content.getString(ServerUtil.TAG_STARTTIME).trim();
						String endDate = content.getString(ServerUtil.TAG_ENDDATE).trim();
						String endTime = content.getString(ServerUtil.TAG_ENDTIME).trim();
						
						Event e = new Event(contentID, contentTitle, contentBody, user, contentTimestamp, location, latitude, longitude, group, favorited, startDate, startTime, endDate, endTime);
						e.setTotalRating(totalRating);
						e.setUserRating(userRating);
						if (e.hasLocation()) {
							((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).addContent(e, true);
						}
						arrayListContent.add(e);
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
							String commentBase64Image = content.getString(ServerUtil.TAG_USER_PIC).trim();
							Bitmap commentProfilePicture = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.drawer_profile);
							if (!commentBase64Image.isEmpty()) {
								byte[] rawImage = Base64.decode(commentBase64Image, Base64.DEFAULT);
								commentProfilePicture = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
							}
							
							String commentBody = comment.getString(ServerUtil.TAG_BODY).trim();
							Date commentTimestamp = Timestamp.valueOf(content.getString(ServerUtil.TAG_TIMESTAMP).trim());
							
							Comment c = new Comment(commentID, commentBody, new User(commentUserID, commentUsername, "", commentUserFullName, commentUserDesc, 0, commentProfilePicture), commentTimestamp);
							commentsList.add(c);
						}
						
						Discussion d = new Discussion(contentID, contentTitle, contentBody, user, contentTimestamp, latitude, longitude, commentsList, group, favorited);
						d.setTotalRating(totalRating);
						d.setUserRating(userRating);
						if (d.hasLocation()) {
							((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).addContent(d, true);
						}
						arrayListContent.add(d);
					}
				}
				mAdapter.notifyDataSetChanged();
				((MainActivity)getParentFragment().getActivity()).updateMapMarkers();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//TODO: set height of footer equal to hide map when no content is being shown (and make sure it doesn't block UI, commented code is too slow)
        //mListView.getFooterFillView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, params.height+400));
	    //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fill.getLayoutParams();

//	    int totHeight = 0;
//	    for (int i = 0; i < mAdapter.getCount(); i++) {
//	        View mView = mAdapter.getView(i, null, mListView);
//	        mView.measure(
//	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//	        totHeight += mView.getMeasuredHeight();
//	        System.out.println("HEIGHT " + i + ": " + String.valueOf(totHeight));
//	    }
		if (mAdapter.getCount() < 3) {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mListView.getFooterFillView().getLayoutParams();
			params.height = mListView.getMeasuredHeight() - mListView.getHeaderHeight();
	    	mListView.getFooterFillView().setLayoutParams(params);
	    	mListView.requestLayout();
		}
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
    	FragmentManager fm = this.getParentFragment().getFragmentManager();
    	ContentFragment curFragment = (ContentFragment) fm.findFragmentById(R.id.fragment_frame);
    	FragmentTransaction ft = fm.beginTransaction();
    	//animations are ordered (enter, exit, popEnter, popExit)
    	ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, 
    			R.anim.slide_up, R.anim.slide_down)
    			.hide(curFragment).addToBackStack("Map").commit();
    	((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).zoomInMap();

	}
}
