package edu.uark.spARK;

import java.util.List;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;

public class NewsFeedArrayAdapter extends ArrayAdapter<Content> {
	private static final String tag = "NewsFeedAdapter";
	
	
	private Context mContext;
	private LayoutInflater mInflater;
	ViewHolder holder;
	
	
	public NewsFeedArrayAdapter(Context context, int layoutid, List<Content> content) {
		super(context, layoutid, content);
		
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		// PROBLEM WITH SETTING THE LAST POSITION, IT THINKS IT IS 0. 
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_discussion, null);
			final Content d = (Content) getItem(position);
			
			holder.mainFL = (FrameLayout) convertView.findViewById(R.id.list_discussionMainFrame);
			holder.titleTV = (TextView) convertView.findViewById(R.id.headerTextView);
			holder.descTV = (TextView) convertView.findViewById(R.id.descTextView);	
			//generic idea for expanding ellipsized text
			holder.descTV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Layout l = holder.descTV.getLayout();
					if (l != null) {
						int lines = l.getLineCount();
						if (lines > 0)
							if (l.getEllipsisCount(lines-1) > 0) {
								holder.descTV.setMaxLines(Integer.MAX_VALUE);
								holder.descTV.setEllipsize(null);
							}
							else {
								holder.descTV.setMaxLines(4);
								holder.descTV.setEllipsize(TextUtils.TruncateAt.END);
							}
						holder.descTV.invalidate();
					}
				}
				
			});
			holder.groupTV = (TextView) convertView.findViewById(R.id.groupTextView);
			holder.usernameTV = (TextView) convertView.findViewById(R.id.usernameTextView);
			holder.usernameTV.setTag(position);
			holder.usernameTV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
				
			});
			holder.creationDateTV = (TextView) convertView.findViewById(R.id.creationDateTextView);
			holder.totalScoreTV = (TextView) convertView.findViewById(R.id.totalScoreTextView);
			
			holder.likeBtn = (ToggleButton) convertView.findViewById(R.id.likeBtn);
			holder.likeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((RadioGroup)v.getParent()).check(v.getId());	
					d.increaseScore();
					update();
				}
				
			});
			holder.likeBtn.setTag(position);
				
			holder.dislikeBtn = (ToggleButton) convertView.findViewById(R.id.dislikeBtn);
			holder.dislikeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((RadioGroup)v.getParent()).check(v.getId());
					d.decreaseScore();
					update();
				}
				
			});
			holder.dislikeBtn.setTag(position);
			
			holder.scoreRG = (RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup);
			holder.scoreRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.titleTV.setText(getItem(position).getTitle());
		holder.descTV.setText(getItem(position).getDescription());
		holder.groupTV.setText(getItem(position).getGroup());
		holder.usernameTV.setText(getItem(position).getAuthor().getName());
		holder.totalScoreTV.setText("" + getItem(position).getScore());
		holder.creationDateTV.setText(getItem(position).getDate());
		holder.totalScoreTV.setText("" + getItem(position).getScore());
		

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
		return convertView;
	}
	
	//use this to load items, saving performance from not having to lookup id
	static class ViewHolder {
		FrameLayout mainFL;
		TextView titleTV;
		TextView descTV;
		TextView groupTV;
		TextView usernameTV;
		TextView creationDateTV;
		TextView totalScoreTV;
		RadioGroup scoreRG;
		ToggleButton likeBtn;
		ToggleButton dislikeBtn;
		int position;
	}
	
	public void update() {
		notifyDataSetChanged();
	}
}
