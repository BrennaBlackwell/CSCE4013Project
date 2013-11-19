package edu.uark.spARK;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;

public class NewsFeedArrayAdapter extends ArrayAdapter<Content> implements AsyncResponse{
	//private static final String tag = "NewsFeedArrayAdapter";
	
	private Fragment fragment;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Content> mContent;
	private int Likes = 0;
	public ViewHolder holder;
	
	
	public NewsFeedArrayAdapter(Context context, int layoutid, List<Content> content, Fragment f) {
		super(context, layoutid, content);
		this.mContent = content;
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fragment = f;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.discussion_list_item, null);
			
			holder.mainFL = (FrameLayout) convertView.findViewById(R.id.list_discussionMainFrame);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.headerTextView);
			holder.titleTextView.setTag(position);
			holder.descTextView = (TextView) convertView.findViewById(R.id.descTextView);	
			holder.commentTextView = (TextView) convertView.findViewById(R.id.commentTextView);
			holder.commentLinearLayout = (LinearLayout) holder.commentTextView.getParent();
			holder.groupTextView = (TextView) convertView.findViewById(R.id.groupTextView);
			holder.usernameTextView = (TextView) convertView.findViewById(R.id.usernameTextView);
			holder.usernameTextView.setTag(position);
//			holder.usernameTV.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//				}
//				
//			});
			holder.creationDateTextView = (TextView) convertView.findViewById(R.id.creationDateTextView);
			holder.totalScoreTextView = (TextView) convertView.findViewById(R.id.totalScoreTextView);
			
			holder.likeBtn = (ToggleButton) convertView.findViewById(R.id.likeBtn);

				
			holder.dislikeBtn = (ToggleButton) convertView.findViewById(R.id.dislikeBtn);
			
			holder.scoreRadioGroup = (RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup);
			holder.scoreRadioGroup.setTag(position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Content c = (Content) mContent.get(position);
		
		holder.titleTextView.setText(c.getTitle());
		holder.descTextView.setText(c.getText());
		holder.groupTextView.setText("test");
		holder.usernameTextView.setText(c.getCreator().getTitle());
		holder.totalScoreTextView.setText("0");
		holder.creationDateTextView.setText(c.getCreationDateString());
		
		SharedPreferences preferences = mContext.getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
		String currentUser = preferences.getString("currentUser", "");
		String content_ID = Integer.toString(c.getId());
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_LIKE_DISLIKE, currentUser, content_ID, "total");
		c.setRating(Likes);
		
		holder.totalScoreTextView.setText(String.valueOf(c.getRating()));
		if (c instanceof Discussion)	
			holder.commentTextView.setText(((Discussion) c).getComments().size() + " comments");
		holder.likeBtn.setTag(Integer.valueOf(position));
		holder.dislikeBtn.setTag(position);		//generic idea for expanding ellipsized text
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
	            		final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
	            		view.setChecked(view.getId() == i);
	            	}
	            }
	        }
	    });	
		
		holder.likeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences preferences = mContext.getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
				String currentUser = preferences.getString("currentUser", "");
				
				JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
				
				ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(0);
				
				if (button.isChecked()) {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, currentUser, Integer.toString(c.getId()), "like");
					
					((RadioGroup)v.getParent()).check(v.getId());	
					getItem((Integer) v.getTag()).incrementRating();
					update();
				} else {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, currentUser, Integer.toString(c.getId()), "dislike");
					
					((RadioGroup)v.getParent()).check(v.getId());
					getItem((Integer) v.getTag()).decrementRating();
					update();
				}
			}
			
		});
		holder.dislikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences preferences = mContext.getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
				String currentUser = preferences.getString("currentUser", "");
				
				JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
				
				ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(2);
				
				if (button.isChecked()) {
					
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, currentUser, Integer.toString(c.getId()), "dislike");
					
					((RadioGroup)v.getParent()).check(v.getId());
					getItem((Integer) v.getTag()).decrementRating();
					update();
				} else {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, currentUser, Integer.toString(c.getId()), "like");
					
					((RadioGroup)v.getParent()).check(v.getId());	
					getItem((Integer) v.getTag()).incrementRating();
					update();
				}
			}
			
		});
		holder.commentLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, CommentActivity.class);
				i.putExtra("Object", (Content) c);
				i.putExtra("position", position);
				fragment.startActivityForResult(i, 1);		
			}
			
		});
		return convertView;
	}
	
	@Override
	public void processFinish(JSONObject result) {
		try { 
			int success = result.getInt("totals_success");
			if (success == 1) {
				if (result.getString("like_total") != null) {
					Likes = Integer.parseInt(result.getString("like_total"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//use this to load items, saving performance from not having to lookup id
	static class ViewHolder {
		FrameLayout mainFL;
		TextView titleTextView;
		TextView descTextView;
		TextView groupTextView;
		TextView usernameTextView;
		TextView creationDateTextView;
		TextView totalScoreTextView;
		TextView commentTextView;
		LinearLayout commentLinearLayout;
		RadioGroup scoreRadioGroup;
		ToggleButton likeBtn;
		ToggleButton dislikeBtn;
		int position;
	}
	
	public void update() {
		notifyDataSetChanged();
	}

	public ViewHolder getHolder() {
		return holder;
	}
	
}
