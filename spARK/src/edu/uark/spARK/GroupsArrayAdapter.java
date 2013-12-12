package edu.uark.spARK;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.entities.Group;
import edu.uark.spARK.entities.User;

public class GroupsArrayAdapter extends ArrayAdapter<Group> implements AsyncResponse{

	public Groups_Fragment fragment;
//	private Context mContext;
	private LayoutInflater mInflater;
	public List<Group> mGroups;
	public ViewHolder holder;
	
	public GroupsArrayAdapter(Context context, int layoutid, List<Group> groups, Groups_Fragment f) {
		super(context, layoutid, groups);
		mGroups = groups;
//		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fragment = f;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Group g = (Group) mGroups.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.group_list_item, null);
			
			holder.mainFL = (FrameLayout) convertView.findViewById(R.id.list_group_main_frame);
			holder.locationLinearLayout = (LinearLayout) convertView.findViewById(R.id.locationLinearLayout);
			holder.locationImageButton = (ImageButton) convertView.findViewById(R.id.locationImageButton);
			holder.locationTextView = (TextView) convertView.findViewById(R.id.locationTextView);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.headerTextView);
			holder.titleTextView.setTag(position);
			holder.descTextView = (TextView) convertView.findViewById(R.id.descTextView);	
			holder.creationDateTextView = (TextView) convertView.findViewById(R.id.creationDateTextView);
			holder.userProfileIcon = (QuickContactBadge) convertView.findViewById(R.id.userQuickContactBadge);
			holder.usernameTextView = (TextView) convertView.findViewById(R.id.usernameTextView);
			holder.usernameTextView.setTag(position);
//			holder.totalScoreTextView = (TextView) convertView.findViewById(R.id.totalScoreTextView);
//			holder.likeBtn = (ToggleButton) convertView.findViewById(R.id.likeBtn);
//			holder.dislikeBtn = (ToggleButton) convertView.findViewById(R.id.dislikeBtn);
			holder.pinpointBtn = (Button) convertView.findViewById(R.id.pinpointBtn);
			holder.deleteBtn = (Button) convertView.findViewById(R.id.trashBtn);
			holder.deleteBtn.setVisibility(View.GONE);
//			holder.scoreRadioGroup = (RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup);
//			holder.scoreRadioGroup.setTag(position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (g.getLatitude().compareTo("") != 0 && g.getLatitude().compareTo("") != 0) {
			holder.locationLinearLayout.setVisibility(View.VISIBLE);
			//holder.locationImageButton
			holder.locationTextView.setText(g.getLatitude() + ", " + g.getLongitude());
			//holder.locationTextView.setMovementMethod(LinkMovementMethod.getInstance());
		}

		holder.titleTextView.setText(g.getTitle());
		holder.descTextView.setText(g.getText());
		holder.descTextView.setMovementMethod(LinkMovementMethod.getInstance());
		Linkify.addLinks(holder.descTextView, Linkify.ALL);
		holder.creationDateTextView.setText(new SimpleDateFormat("MMM d, yyyy").format(g.getCreationDate()));
		holder.usernameTextView.setText(g.getCreator().getTitle());
//		holder.totalScoreTextView.setText(String.valueOf(g.getTotalRating()));
//		holder.likeBtn.setTag(position);
//		holder.dislikeBtn.setTag(position);	
//		if (g.getUserRating() == 1) {
//			holder.likeBtn.setChecked(true);
//			holder.dislikeBtn.setChecked(false);
//		} else if (g.getUserRating() == -1) {
//			holder.likeBtn.setChecked(false);
//			holder.dislikeBtn.setChecked(true);
//		} else {
//			holder.likeBtn.setChecked(false);
//			holder.dislikeBtn.setChecked(false);
//		}
		if (g.getCreator().getId() == MainActivity.myUserID) {
			holder.deleteBtn.setVisibility(View.VISIBLE);
		} else {
			holder.deleteBtn.setVisibility(View.GONE);
		}

//		holder.scoreRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//	        @Override
//	        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
//	            for (int j = 0; j < radioGroup.getChildCount(); j++) {
//	            	if (radioGroup.getChildAt(j).isEnabled()) {
//	            		final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
//	            		if (view.getId() == i) {
//	            			view.setChecked(true);
//	            		} else {
//	            			view.setChecked(false);
//	            		}
//	            	}
//	            }
//	        }
//	    });	
		
//		holder.likeBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				JSONQuery jquery = new JSONQuery(GroupsArrayAdapter.this);
//				ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(0);
//				
//				if (button.isChecked()) {
//					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(g.getId()), "like");
//					
//					((RadioGroup)v.getParent()).check(v.getId());	
//					getItem((Integer) v.getTag()).incrementRating();
//					update();
//				} else {
//					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(g.getId()), "delete");
//					
//					((RadioGroup)v.getParent()).check(v.getId());
//					getItem((Integer) v.getTag()).decrementRating();
//					update();
//				}
//			}
//			
//		});
//		holder.dislikeBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				JSONQuery jquery = new JSONQuery(GroupsArrayAdapter.this);
//				ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(2);
//				
//				if (button.isChecked()) {
//					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(g.getId()), "dislike");
//					
//					((RadioGroup)v.getParent()).check(v.getId());
//					getItem((Integer) v.getTag()).decrementRating();
//					update();
//				} else {
//					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(g.getId()), "delete");
//					
//					((RadioGroup)v.getParent()).check(v.getId());	
//					getItem((Integer) v.getTag()).incrementRating();
//					update();
//				}
//			}
//			
//		});
		holder.userProfileIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: Need to implement addToBackStack on Profiles
				Fragment profileFragment;
				if (MainActivity.myUserID == g.getCreator().getId()) {
					profileFragment = new MyProfile_Fragment() {
						@Override
					    public void onCreate(Bundle savedInstanceState) {
						    super.onCreate(savedInstanceState);
							    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
						    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
						    	setHasOptionsMenu(true);
						    }
					};
				} else {
					profileFragment = new Profile_Fragment();
					Bundle args = new Bundle();
					args.putSerializable("ContentCreator", (User) g.getCreator());
		            profileFragment.setArguments(args);
				}
	            
				//fragment.getFragmentManager().beginTransaction().add(fragment.getView().getId(), profileFragment).commit();
				((MainActivity) fragment.getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
				fragment.getFragmentManager().beginTransaction().add(R.id.fragment_frame, profileFragment)
				.addToBackStack("Profile").commit();
		        MainActivity.mPager.setVisibility(View.GONE);		           
			}			
		});
		
		holder.deleteBtn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) { 
				final int contentID = g.getId();
				final int pos = position;
				JSONQuery jquery = new JSONQuery(GroupsArrayAdapter.this);
                jquery.execute(ServerUtil.URL_DELETE_CONTENT, Integer.toString(MainActivity.myUserID), Integer.toString(contentID), "Group", Integer.toString(pos));
			}
		});
			
		return convertView;
	}

	@Override
	public void processFinish(JSONObject result) {
		// TODO Auto-generated method stub
		
	}
	
	//use this to load items, saving performance from not having to lookup id
		static class ViewHolder {
			FrameLayout mainFL;
			LinearLayout locationLinearLayout;
			ImageButton locationImageButton;
			TextView locationTextView;
			TextView titleTextView;
			TextView descTextView;
			TextView creationDateTextView;
			QuickContactBadge userProfileIcon;
			TextView usernameTextView;
//			TextView totalScoreTextView;
//			TextView commentTextView;
//			LinearLayout commentLinearLayout;
//			RadioGroup scoreRadioGroup;
//			ToggleButton likeBtn;
//			ToggleButton dislikeBtn;
			Button pinpointBtn;
			Button deleteBtn;
			int position;
		}
		
		public void update() {
			notifyDataSetChanged();
		}

		public ViewHolder getHolder() {
			return holder;
		}

}
