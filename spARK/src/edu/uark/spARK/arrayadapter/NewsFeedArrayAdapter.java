package edu.uark.spARK.arrayadapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.google.android.gms.maps.model.LatLng;

import edu.uark.spARK.R;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import edu.uark.spARK.activity.CommentActivity;
import edu.uark.spARK.activity.MainActivity;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.entity.*;
import edu.uark.spARK.fragment.MyProfileFragment;
import edu.uark.spARK.fragment.NewsFeedFragment;
import edu.uark.spARK.fragment.ProfileFragment;
import edu.uark.spARK.fragment.MapViewFragment;

public class NewsFeedArrayAdapter extends ArrayAdapter<Content> implements AsyncResponse{
	
	public NewsFeedFragment fragment;
	private Context mContext;
	private LayoutInflater mInflater;
	public List<Content> mContent;
	public ViewHolder holder;
	
	
	public NewsFeedArrayAdapter(Context context, int layoutid, List<Content> content, NewsFeedFragment f) {
		super(context, layoutid, content);
		this.mContent = content;
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fragment = f;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Content c = (Content) mContent.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			if (c instanceof Discussion) {
				convertView = mInflater.inflate(R.layout.content_list_item_discussion, null);
				holder.commentTextView = (TextView) convertView.findViewById(R.id.commentTextView);
				holder.commentLinearLayout = (LinearLayout) holder.commentTextView.getParent();
			}
			if (c instanceof Bulletin) {
				convertView = mInflater.inflate(R.layout.content_list_item_discussion, null);
			}
			if (c instanceof Event) {
				convertView = mInflater.inflate(R.layout.content_list_item_event, null);
				holder.event_where_text = (TextView) convertView.findViewById(R.id.event_where_text);
				holder.event_start_text = (TextView) convertView.findViewById(R.id.event_start_text);
				holder.event_end_text = (TextView) convertView.findViewById(R.id.event_end_text);				
			}
			
			holder.mainFL = (FrameLayout) convertView.findViewById(R.id.list_discussionMainFrame);
			holder.locationLinearLayout = (LinearLayout) convertView.findViewById(R.id.locationLinearLayout);
			holder.locationImageButton = (ImageButton) convertView.findViewById(R.id.locationImageButton);
			holder.locationTextView = (TextView) convertView.findViewById(R.id.locationTextView);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.headerTextView);
			holder.titleTextView.setTag(position);
			holder.descTextView = (TextView) convertView.findViewById(R.id.descTextView);	
			holder.groupAndDateTextView = (TextView) convertView.findViewById(R.id.groupAndDateTextView);
			holder.userProfileIcon = (ImageView) convertView.findViewById(R.id.userQuickContactBadge);
			holder.usernameTextView = (TextView) convertView.findViewById(R.id.usernameTextView);
			holder.usernameTextView.setTag(position);
			holder.totalScoreTextView = (TextView) convertView.findViewById(R.id.totalScoreTextView);
			holder.likeBtn = (ToggleButton) convertView.findViewById(R.id.likeBtn);
			holder.dislikeBtn = (ToggleButton) convertView.findViewById(R.id.dislikeBtn);
			holder.pinpointBtn = (Button) convertView.findViewById(R.id.pinpointBtn);
			holder.favoriteBtn = (ToggleButton) convertView.findViewById(R.
					id.favoriteBtn);
			holder.deleteBtn = (Button) convertView.findViewById(R.id.trashBtn);
			holder.deleteBtn.setVisibility(View.GONE);
			holder.scoreRadioGroup = (RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup);
			holder.scoreRadioGroup.setTag(position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (c.getLatitude().compareTo("") != 0 && c.getLatitude().compareTo("") != 0) {
			holder.locationLinearLayout.setVisibility(View.VISIBLE);
			//holder.locationImageButton
			if (((MapViewFragment) fragment.getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).getLocationClient().isConnected()) {
				Location cur = ((MapViewFragment) fragment.getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).getLocationClient().getLastLocation();
				float[] results = new float[3];
				Location.distanceBetween(cur.getLatitude(), cur.getLongitude(), Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude()), results);
				//holder.locationTextView.setText(c.getLatitude() + ", " + c.getLongitude());
				holder.locationTextView.setText(String.format("%.1f", results[0]*0.000621371) + " miles away");
			}
			//holder.locationTextView.setMovementMethod(LinkMovementMethod.getInstance());

			holder.locationTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					fragment.hideFragment();
					((MapViewFragment) fragment.getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).moveCameraToLatLng(new LatLng(Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude())));
				}
				
			});
			
			holder.pinpointBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					fragment.hideFragment();
					((MapViewFragment) fragment.getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)).moveCameraToLatLng(new LatLng(Double.valueOf(c.getLatitude()), Double.valueOf(c.getLongitude())));
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
					Intent i = new Intent(mContext, CommentActivity.class);
					i.putExtra("Object", (Content) c);
					i.putExtra("position", position);
					fragment.startActivityForResult(i, 1);		
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
		holder.likeBtn.setTag(position);
		holder.dislikeBtn.setTag(position);	
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
		
		holder.favoriteBtn.setTag(position);
		if (c.isFavorited()) {
			holder.favoriteBtn.setChecked(true);
		} else {
			holder.favoriteBtn.setChecked(false);
		}
		
		if (c.getCreator().getId() == MainActivity.myUserID) {
			holder.deleteBtn.setVisibility(View.VISIBLE);
		} else {
			holder.deleteBtn.setVisibility(View.GONE);
		}
		
		if (c.getCreator().getId() == MainActivity.myUserID) {
			holder.deleteBtn.setVisibility(View.VISIBLE);
		}
		//generic idea for expanding ellipsized text
//		holder.descTV.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//				Layout l = holder.descTV.getLayout();
//				if (l != null) {
//					int lines = l.getLineCount();
//					System.out.println("num lines: " + lines);
//					if (lines > 0)
//						if (l.getEllipsisCount(lines-1) > 0) {
//							System.out.println("getEllipsisCount(): " + l.getEllipsisCount(lines-1));
//							holder.descTV.setMaxLines(Integer.MAX_VALUE);
//							holder.descTV.setEllipsize(null);
//						}
//						else {
//							holder.descTV.setMaxLines(4);
//							holder.descTV.setEllipsize(TextUtils.TruncateAt.END);
//						}
//					holder.descTV.invalidate();
//				}
//			}
//			
//		});

		//increase touch area of like and dislike buttons (not really working)
//		final View buttonparent = (View) mButtonLike.getParent();
//		buttonparent.post(new Runnable() {
//			@Override
//			public void run() {
//				Rect delegateArea = new Rect();
//				mButtonLike.getHitRect(delegateArea);
//				delegateArea.bottom += 8;
//				delegateArea.top -= 8;
//				delegateArea.left -= 8;
//				delegateArea.right += 8;
//				buttonparent.setTouchDelegate(new TouchDelegate(delegateArea, mButtonLike));
//				buttonparent.setTouchDelegate(new TouchDelegate(delegateArea, mButtonDislike));
//			}
//		});
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
		
		holder.likeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
				ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(0);
				
				if (button.isChecked()) {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "like");
					
					((RadioGroup)v.getParent()).check(v.getId());	
					getItem((Integer) v.getTag()).incrementRating();
					update();
				} else {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "delete");
					
					((RadioGroup)v.getParent()).check(v.getId());
					getItem((Integer) v.getTag()).decrementRating();
					update();
				}
			}
			
		});
		holder.dislikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
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
		
		// TODO: Favorite button not refreshing immediately
		holder.favoriteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
				ToggleButton button = (ToggleButton) v;
				
				if (button.isChecked()) {
					jquery.execute(ServerUtil.URL_FAVORITE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "favorite");
					button.setChecked(true);	
					update();
				} else {
					jquery.execute(ServerUtil.URL_FAVORITE, Integer.toString(MainActivity.myUserID), Integer.toString(c.getId()), "unfavorite");
					button.setChecked(false);	
					update();
				}
			}
			
		});
		
		holder.userProfileIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: Need to implement addToBackStack on Profiles
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
				((MainActivity) fragment.getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
				fragment.getParentFragment().getFragmentManager().beginTransaction().detach(((MapViewFragment) fragment.getParentFragment().getFragmentManager().findFragmentById(R.id.map_frame)))
		        .replace(R.id.fragment_frame, profileFragment, "Profile").addToBackStack("Profile").commit();
			}			
		});
		
		holder.deleteBtn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) { 
				final int contentID = c.getId();
				final int pos = position;
				String content = "Discussion";
				if (c.getClass().toString().contains("Bulletin")) {
					content = "Bulletin";
				} else if (c.getClass().toString().contains("Event")) {
					content = "Event";
				}
				final String contentType = content;
				
				JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
                jquery.execute(ServerUtil.URL_DELETE_CONTENT, Integer.toString(MainActivity.myUserID), Integer.toString(contentID), contentType, Integer.toString(pos));
                
                
//				new AlertDialog.Builder(mContext)
//		        .setIcon(android.R.drawable.ic_dialog_alert)
//		        .setTitle("Delete " + contentType)
//		        .setMessage("Are you sure you want to delete this item?")
//		        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//		    {
//		        @Override
//		        public void onClick(DialogInterface dialog, int which) {
//		        	JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
//                
//					TODO: Wish I could get the following to work instead. For some reason 
//                	here the contentID, contentType, and pos values are unknown. Check in debug for details
//                
//                  jquery.execute(ServerUtil.URL_DELETE_CONTENT, Integer.toString(MainActivity.myUserID), Integer.toString(contentID), contentType, Integer.toString(pos));
//		        }
//
//		    })
//		    .setNegativeButton("No", null)
//		    .show();
			}
		});
			
		return convertView;
	}
	
	@Override
	public void processFinish(JSONObject result) {
		try { 
			int deleteSuccess = result.getInt("deleteSuccess");
			if (deleteSuccess == 1) {
				int position = result.getInt("position");
				mContent.remove(position);
				notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try { 
			int favoriteSuccess = result.getInt("favoriteSuccess");
			if (favoriteSuccess == 1) {
				// Do something for favoriting later
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//use this to load items, saving performance from not having to lookup id
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
	
	public void update() {
		notifyDataSetChanged();
	}

	public ViewHolder getHolder() {
		return holder;
	}
	

}
