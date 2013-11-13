package edu.uark.spARK;

import java.util.ArrayList;
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
	private static final String tag = "NewsFeedArrayAdapter";
	
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Content> mContent;
	ViewHolder holder;
	
	
	public NewsFeedArrayAdapter(Context context, int layoutid, List<Content> content) {
		super(context, layoutid, content);
		this.mContent = content;
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		// PROBLEM WITH SETTING THE LAST POSITION, IT THINKS IT IS 0. 
		//ViewHolder holder;
		System.out.println("position: " + position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_discussion, null);
			
			holder.mainFL = (FrameLayout) convertView.findViewById(R.id.list_discussionMainFrame);
			holder.titleTV = (TextView) convertView.findViewById(R.id.headerTextView);
			holder.titleTV.setTag(position);
			holder.descTV = (TextView) convertView.findViewById(R.id.descTextView);	

			holder.groupTV = (TextView) convertView.findViewById(R.id.groupTextView);
			holder.usernameTV = (TextView) convertView.findViewById(R.id.usernameTextView);
			holder.usernameTV.setTag(position);
//			holder.usernameTV.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//				}
//				
//			});
			holder.creationDateTV = (TextView) convertView.findViewById(R.id.creationDateTextView);
			holder.totalScoreTV = (TextView) convertView.findViewById(R.id.totalScoreTextView);
			
			holder.likeBtn = (ToggleButton) convertView.findViewById(R.id.likeBtn);

				
			holder.dislikeBtn = (ToggleButton) convertView.findViewById(R.id.dislikeBtn);
			
			holder.scoreRG = (RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup);
			holder.scoreRG.setTag(position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Content c = (Content) mContent.get(position);
		
		holder.titleTV.setText(c.getTitle());
		holder.descTV.setText(c.getDescription());
		holder.groupTV.setText(c.getGroup());
		holder.usernameTV.setText(c.getAuthor().getName());
		holder.totalScoreTV.setText("" + c.getScore());
		holder.creationDateTV.setText(c.getDate());
		holder.totalScoreTV.setText("" + c.getScore());
		holder.likeBtn.setTag(Integer.valueOf(position));
		holder.dislikeBtn.setTag(position);
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
		holder.likeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((RadioGroup)v.getParent()).check(v.getId());	
				getItem((Integer) v.getTag()).increaseScore();
				update();
			}
			
		});
		holder.dislikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((RadioGroup)v.getParent()).check(v.getId());
				getItem((Integer) v.getTag()).decreaseScore();
				update();
			}
			
		});
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
	
	@Override
	public Content getItem(int pos) {
		return super.getItem(pos);
	}
	
}
