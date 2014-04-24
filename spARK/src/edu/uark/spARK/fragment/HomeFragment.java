package edu.uark.spARK.fragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.uark.spARK.R;
import edu.uark.spARK.activity.CommentActivity;
import edu.uark.spARK.activity.MainActivity;
import edu.uark.spARK.arrayadapter.HomeFeedArrayAdapter;
import edu.uark.spARK.arrayadapter.HomeFeedArrayAdapter.ViewHolder;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.entity.Bulletin;
import edu.uark.spARK.entity.Comment;
import edu.uark.spARK.entity.Content;
import edu.uark.spARK.entity.Discussion;
import edu.uark.spARK.entity.Entity;
import edu.uark.spARK.entity.Event;
import edu.uark.spARK.entity.Group;
import edu.uark.spARK.entity.User;
import edu.uark.spARK.location.MyLocation;

public class HomeFragment extends Fragment implements AsyncResponse {
	final String[] CONTENT = {"Headlines", "Events", "Groups", "Discussions"};
	ExpandableListView mListView;
	NewAdapter mAdapter;
	public ViewHolder holder;
	ArrayList<String> groupItem = new ArrayList<String>();
	//ArrayList<Content> childItem = new ArrayList<Content>();
	ArrayList<ArrayList<Content>> group = new ArrayList<ArrayList<Content>>();
	
    public static HomeFragment newInstance(int num) {
    	HomeFragment f = new HomeFragment();

        // Supply num input as an argument (0 for discussion fragment, 1 for bulletin).
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }
    
    public HomeFragment() {
        // Empty constructor required for fragment subclasses
    }  
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupItem.add("Headlines");
        groupItem.add("Events");
        groupItem.add("Discussions");
        
        ArrayList<String> child = new ArrayList<String>();

        group.add(new ArrayList<Content>());
        group.add(new ArrayList<Content>());
        group.add(new ArrayList<Content>());
        //childItem.add(child);  
        //childItem.add(child);  
        //childItem.add(child);
        
	    mAdapter = new NewAdapter(groupItem, group);
	    mAdapter.setInflater((LayoutInflater) getActivity().getLayoutInflater(), getActivity());
		loadContent();
    }
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);		
		View v = inflater.inflate(R.layout.fragment_home, container, false);		
		mListView = (ExpandableListView) v.findViewById(R.id.fragment_home_list_view);
		mListView.setGroupIndicator(null);
	    mListView.setAdapter(mAdapter);
		for (int groupPosition = 0; groupPosition < mAdapter.getGroupCount(); groupPosition++)
			mListView.expandGroup(groupPosition);
		//keep group from collapsing
		mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
		{
		    public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId)
		    {
		        mListView.expandGroup(itemPosition);
		        return true;
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
	
	@Override
	public void onStart() {
		super.onStart();

	}
	
	@Override
	public void onStop() {
		super.onStop();

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
	@Override
	public void processFinish(JSONObject result) {
		int eventnum = 0;
    	//mListView.setBackgroundColor(Color.TRANSPARENT);
		try {
			int success = result.getInt(ServerUtil.TAG_SUCCESS);

			if (success == 1) {
				//((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).clearMarkers();
				for (int i = 0; i < group.size(); i++) {
					group.get(i).clear();
				}
				// Get Array of discussions
				JSONArray contents = result.getJSONArray(ServerUtil.TAG_CONTENTS);

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
							//((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).addContent(b, true);
						}
						this.group.get(0).add(b);
					} else if (contentType.equals("Event")) {
						System.out.println("Event " + eventnum++);
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
							//((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).addContent(e, true);
						}
						this.group.get(1).add(e);
					} else if (contentType.equals("Discussion")) {
						JSONArray comments = content.getJSONArray(ServerUtil.TAG_COMMENTS);
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
							//((MapViewFragment) getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).addContent(d, true);
						}
						this.group.get(2).add(d);
					}
				}
				mAdapter.notifyDataSetChanged();
				//((MainActivity)getParentFragment().getActivity()).updateMapMarkers();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	public class NewAdapter extends BaseExpandableListAdapter {

		 public ArrayList<String> groupItem;
		 public Content tempChild;
		 public ArrayList<ArrayList<Content>> Childtem = new ArrayList<ArrayList<Content>>();
		 public LayoutInflater mInflater;
		 public Activity activity;

		 public NewAdapter(ArrayList<String> grList, ArrayList<ArrayList<Content>> childItem) {
		  groupItem = grList;
		  this.Childtem = childItem;
		 }

		 public void setInflater(LayoutInflater mInflater, Activity act) {
		  this.mInflater = mInflater;
		  activity = act;
		 }

		 @Override
		 public Object getChild(int groupPosition, int childPosition) {
		  return null;
		 }

		 @Override
		 public long getChildId(int groupPosition, int childPosition) {
		  return 0;
		 }

		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			// tempChild = (Content) Childtem.get(groupPosition);
			final Content c = (Content) Childtem.get(groupPosition).get(childPosition);

			//if (convertView == null) {
				holder = new ViewHolder();
				if (c instanceof Discussion) {
					convertView = mInflater.inflate(R.layout.content_list_item_discussion, null);
					holder.commentTextView = (TextView) convertView.findViewById(R.id.commentTextView);
					holder.commentLinearLayout = (LinearLayout) holder.commentTextView.getParent();
				}
				if (c instanceof Bulletin) {
					convertView = mInflater.inflate(R.layout.content_list_item_bulletin, null);
				}
				if (c instanceof Event) {
					convertView = mInflater.inflate(R.layout.content_list_item_event, null);
					holder.event_where_text = (TextView) convertView.findViewById(R.id.event_where_text);
					holder.event_start_text = (TextView) convertView.findViewById(R.id.event_start_text);
					holder.event_end_text = (TextView) convertView.findViewById(R.id.event_end_text);
					
				}
				convertView.findViewById(R.id.tableRow3).setVisibility(View.GONE);
				holder.mainFL = (FrameLayout) convertView.findViewById(R.id.list_discussionMainFrame);
				holder.locationLinearLayout = (LinearLayout) convertView.findViewById(R.id.locationLinearLayout);
				holder.locationImageButton = (ImageButton) convertView.findViewById(R.id.locationImageButton);
				holder.locationTextView = (TextView) convertView.findViewById(R.id.locationTextView);
				holder.titleTextView = (TextView) convertView.findViewById(R.id.headerTextView);
				holder.titleTextView.setTag(childPosition);
				holder.descTextView = (TextView) convertView.findViewById(R.id.descTextView);	
				holder.groupAndDateTextView = (TextView) convertView.findViewById(R.id.groupAndDateTextView);
				holder.userProfileIcon = (ImageView) convertView.findViewById(R.id.userQuickContactBadge);
				holder.usernameTextView = (TextView) convertView.findViewById(R.id.usernameTextView);
				holder.usernameTextView.setTag(childPosition);
				holder.totalScoreTextView = (TextView) convertView.findViewById(R.id.totalScoreTextView);
				holder.likeBtn = (ToggleButton) convertView.findViewById(R.id.likeBtn);
				holder.dislikeBtn = (ToggleButton) convertView.findViewById(R.id.dislikeBtn);
				holder.pinpointBtn = (Button) convertView.findViewById(R.id.pinpointBtn);
				holder.favoriteBtn = (ToggleButton) convertView.findViewById(R.id.favoriteBtn);
				holder.deleteBtn = (Button) convertView.findViewById(R.id.trashBtn);
				holder.deleteBtn.setVisibility(View.GONE);
				holder.scoreRadioGroup = (RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup);
				holder.scoreRadioGroup.setTag(childPosition);
				convertView.setTag(holder);
			//} else {
				holder = (ViewHolder) convertView.getTag();
			//}
			
			if (c.getLatitude().compareTo("") != 0 && c.getLatitude().compareTo("") != 0) {
				holder.locationLinearLayout.setVisibility(View.VISIBLE);
				//holder.locationImageButton
				if (((MapViewFragment) getFragmentManager().findFragmentById(R.id.map_frame)).getLocationClient().isConnected()) {
					Location cur = ((MapViewFragment) getFragmentManager().findFragmentById(R.id.map_frame)).getLocationClient().getLastLocation();
					float[] results = new float[3];
					Location.distanceBetween(cur.getLatitude(), cur.getLongitude(), Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude()), results);
					//holder.locationTextView.setText(c.getLatitude() + ", " + c.getLongitude());
					holder.locationTextView.setText(String.format("%.1f", results[0]*0.000621371) + " miles away");
				}
				//holder.locationTextView.setMovementMethod(LinkMovementMethod.getInstance());

				holder.locationTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//fragment.hideFragment();
						//((MapViewFragment) fragment.getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).moveCameraToLatLng(new LatLng(Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude())));
					}
					
				});
				
				holder.pinpointBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//fragment.hideFragment();
						//((MapViewFragment) fragment.getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).moveCameraToLatLng(new LatLng(Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude())));
					}
					
				});
			}

			holder.titleTextView.setText(c.getTitle());
			holder.descTextView.setText(c.getText());
			if (c.getText() == null || c.getText() == "null" || c.getText().isEmpty()) {
				holder.descTextView.setText("N/A");
			}
			holder.descTextView.setMovementMethod(LinkMovementMethod.getInstance());
			Linkify.addLinks(holder.descTextView, Linkify.ALL);
			holder.groupAndDateTextView.setText("posted publicly - " + c.getCreationDateAsPrettyTime());
			try {
				if (c.getGroupAttached().getId() != 0) {
					holder.groupAndDateTextView.setText("posted to '" + c.getGroupAttached().getTitle() + "' - " + c.getCreationDateAsPrettyTime());
				}
			} catch (Exception e) {
				//this means no group was attached and it is public
				e.printStackTrace();
			}
			
			holder.userProfileIcon.setImageBitmap(c.getCreator().getBitmap());
			holder.usernameTextView.setText(c.getCreator().getTitle());
			holder.totalScoreTextView.setText(String.valueOf(c.getTotalRating()));
			if (c instanceof Discussion) {
				holder.commentTextView.setText(((Discussion) c).getNumComments() + " comments");
				holder.commentLinearLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getActivity().getApplicationContext(), CommentActivity.class);
						i.putExtra("Object", (Content) c);
						i.putExtra("position", childPosition);
						startActivityForResult(i, 1);		
					}
					
				});
			} else if (c instanceof Event) {
				holder.event_where_text.setText(((Event) c).getLocation());
				if (((Event) c).getLocation() == null || ((Event) c).getLocation().isEmpty()) {
					holder.event_where_text.setText("N/A");
				}
				holder.event_start_text.setText(((Event) c).getStartDate() + " - " + ((Event) c).getStartTime());
				if (((Event) c).getStartTime() == null || ((Event) c).getStartTime().isEmpty()) {
					holder.event_start_text.setText(((Event) c).getStartDate() + " - All Day");
				}
				holder.event_end_text.setText(((Event) c).getEndDate() + " - " + ((Event) c).getEndTime());			
				if (((Event) c).getEndTime() == null || ((Event) c).getEndTime().isEmpty()) {
					holder.event_end_text.setText(((Event) c).getEndDate() + " - All Day");
				}
			}
			holder.likeBtn.setTag(childPosition);
			holder.dislikeBtn.setTag(childPosition);	
			if (c.getUserRating() == 1) {
				holder.likeBtn.setChecked(true);
				holder.dislikeBtn.setChecked(false);
			} else if (c.getUserRating() == -1) {
				holder.likeBtn.setChecked(false);
				holder.dislikeBtn.setChecked(true);
			} else {
				holder.likeBtn.setChecked(false);
				holder.dislikeBtn.setChecked(false);
			}
			
			holder.favoriteBtn.setTag(childPosition);
			holder.favoriteBtn.setChecked(c.isFavorited());
			
			if (c.getCreator().getId() == MainActivity.myUserID) {
				holder.deleteBtn.setVisibility(View.VISIBLE);
			} else {
				holder.deleteBtn.setVisibility(View.GONE);
			}
			
			if (c.getCreator().getId() == MainActivity.myUserID) {
				holder.deleteBtn.setVisibility(View.VISIBLE);
			}
			//generic idea for expanding ellipsized text
//			holder.descTV.setOnClickListener(new OnClickListener() {
	//
//				@Override
//				public void onClick(View v) {
//					
//					Layout l = holder.descTV.getLayout();
//					if (l != null) {
//						int lines = l.getLineCount();
//						System.out.println("num lines: " + lines);
//						if (lines > 0)
//							if (l.getEllipsisCount(lines-1) > 0) {
//								System.out.println("getEllipsisCount(): " + l.getEllipsisCount(lines-1));
//								holder.descTV.setMaxLines(Integer.MAX_VALUE);
//								holder.descTV.setEllipsize(null);
//							}
//							else {
//								holder.descTV.setMaxLines(4);
//								holder.descTV.setEllipsize(TextUtils.TruncateAt.END);
//							}
//						holder.descTV.invalidate();
//					}
//				}
//				
//			});

			//increase touch area of like and dislike buttons (not really working)
//			final View buttonparent = (View) mButtonLike.getParent();
//			buttonparent.post(new Runnable() {
//				@Override
//				public void run() {
//					Rect delegateArea = new Rect();
//					mButtonLike.getHitRect(delegateArea);
//					delegateArea.bottom += 8;
//					delegateArea.top -= 8;
//					delegateArea.left -= 8;
//					delegateArea.right += 8;
//					buttonparent.setTouchDelegate(new TouchDelegate(delegateArea, mButtonLike));
//					buttonparent.setTouchDelegate(new TouchDelegate(delegateArea, mButtonDislike));
//				}
//			});
			holder.scoreRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		        @Override
		        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
		            for (int j = 0; j < radioGroup.getChildCount(); j++) {
		            	if (radioGroup.getChildAt(j).isEnabled()) {
		            		final ToggleButton button = (ToggleButton) radioGroup.getChildAt(j);
		            		if (button.getId() == i) {
		            			button.setChecked(true);
		            		} else {
		            			button.setChecked(false);
		            		}
		            	}
		            }
		        }
		    });	
			
/*			holder.likeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					JSONQuery jquery = new JSONQuery(mAdapter.this);
					ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(0);
					
					if (button.isChecked()) {
						jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "like");
						
						((RadioGroup)v.getParent()).check(v.getId());	
						getItem((Integer) v.getTag()).incrementRating();
						mAdapter.notifyDataSetChanged();
					} else {
						jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "delete");
						
						((RadioGroup)v.getParent()).check(v.getId());
						getItem((Integer) v.getTag()).decrementRating();
						mAdapter.notifyDataSetChanged();
					}
				}
				
			});
			holder.dislikeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					JSONQuery jquery = new JSONQuery(HomeFeedArrayAdapter.this);
					ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(2);
					
					if (button.isChecked()) {
						jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "dislike");
						
						((RadioGroup)v.getParent()).check(v.getId());
						getItem((Integer) v.getTag()).decrementRating();
						update();
					} else {
						jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "delete");
						
						((RadioGroup)v.getParent()).check(v.getId());	
						getItem((Integer) v.getTag()).incrementRating();
						update();
					}
				}
				
			});
			
			holder.favoriteBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					JSONQuery jquery = new JSONQuery(HomeFeedArrayAdapter.this);
					ToggleButton button = (ToggleButton) v;
					
					if (button.isChecked()) {
						jquery.execute(ServerUtil.URL_FAVORITE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "favorite");
						button.setChecked(true);	
						getItem((Integer) v.getTag()).setFavorited(true);
						update();
					} else {
						jquery.execute(ServerUtil.URL_FAVORITE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "unfavorite");
						button.setChecked(false);	
						getItem((Integer) v.getTag()).setFavorited(false);
						update();
					}
				}
				
			});*/
			
			holder.userProfileIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Fragment profileFragment;
					if (MainActivity.myUserID == c.getCreator().getId()) {
						profileFragment = new MyProfileFragment() {
							@Override
						    public void onCreate(Bundle savedInstanceState) {
							    super.onCreate(savedInstanceState);
								    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
							    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
							    	setHasOptionsMenu(true);
							    }
						};
					} else {
						profileFragment = new ProfileFragment();
						Bundle args = new Bundle();
						args.putSerializable("ContentCreator", (User) c.getCreator());
			            profileFragment.setArguments(args);
					}
		            
					//fragment.getFragmentManager().beginTransaction().add(fragment.getView().getId(), profileFragment).commit();
					((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
					getFragmentManager().beginTransaction().detach(((MapViewFragment) getFragmentManager().findFragmentById(R.id.map_frame)))
			        .replace(R.id.fragment_frame, profileFragment, "Profile").addToBackStack("Profile").commit();
				}			
			});
			
/*			holder.deleteBtn.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) { 
					final int contentID = c.getId();
					final int pos = childPosition;
					String content = "Discussion";
					if (c.getClass().toString().contains("Bulletin")) {
						content = "Bulletin";
					} else if (c.getClass().toString().contains("Event")) {
						content = "Event";
					}
					final String contentType = content;
					
					JSONQuery jquery = new JSONQuery(HomeFeedArrayAdapter.this);
	                jquery.execute(ServerUtil.URL_DELETE_CONTENT, Integer.toString(MainActivity.myUserID), Integer.toString(contentID), contentType, Integer.toString(pos));
				}
			});*/

			
			
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
	/*				Toast.makeText(activity, group.get(groupPosition).get(childPosition) + " " + groupPosition, Toast.LENGTH_SHORT).show();
					((MainActivity) HomeFragment.this.getActivity()).mNavDrawerList.performItemClick(
							((MainActivity) HomeFragment.this.getActivity()).mNavListArrayAdapter.getView(3, null, null),
							3,
							((MainActivity) HomeFragment.this.getActivity()).mNavListArrayAdapter.getItemId(3));
			*/	}
			});
			return convertView;
		}

		 @Override
		 public int getChildrenCount(int groupPosition) {
			 return ((ArrayList<Content>) Childtem.get(groupPosition)).size();
		 }

		 @Override
		 public Object getGroup(int groupPosition) {
		  return null;
		 }

		 @Override
		 public int getGroupCount() {
		  return groupItem.size();
		 }

		 @Override
		 public void onGroupCollapsed(int groupPosition) {
		  super.onGroupCollapsed(groupPosition);
		 }

		 @Override
		 public void onGroupExpanded(int groupPosition) {
		  super.onGroupExpanded(groupPosition);
		 }

		 @Override
		 public long getGroupId(int groupPosition) {
		  return 0;
		 }

		 @Override
		 public View getGroupView(int groupPosition, boolean isExpanded,
		   View convertView, ViewGroup parent) {
		  if (convertView == null) {
		   convertView = mInflater.inflate(R.layout.home_list_group, null);
		  }
		  ((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
		  ((CheckedTextView) convertView).setChecked(isExpanded);
		  return convertView;
		 }

		 @Override
		 public boolean hasStableIds() {
		  return false;
		 }

		 @Override
		 public boolean isChildSelectable(int groupPosition, int childPosition) {
		  return false;
		 }
	}
	
	public void loadContent() {
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_HOME, MainActivity.myUsername);
	}
	
	public static class ViewHolder {
		public FrameLayout mainFL;
		public LinearLayout locationLinearLayout;
		public ImageButton locationImageButton;
		public TextView locationTextView;
		public TextView titleTextView;
		public TextView descTextView;
		public TextView groupAndDateTextView;
		public ImageView userProfileIcon;
		public TextView usernameTextView;
		public TextView totalScoreTextView;
		public TextView commentTextView;
		public LinearLayout commentLinearLayout;
		public RadioGroup scoreRadioGroup;
		public ToggleButton likeBtn;
		public ToggleButton dislikeBtn;
		public Button pinpointBtn;
		public ToggleButton favoriteBtn;
		public Button deleteBtn;
		int position;
		
		public TextView event_where_text;
		public TextView event_start_text;
		public TextView event_end_text;
	}
	
	public ViewHolder getHolder() {
		return holder;
	}
	
}
