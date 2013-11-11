package edu.uark.spARK;

import java.util.List;

import edu.uark.spARK.entities.Content;
import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NewsFeedArrayAdapter extends ArrayAdapter<Content> implements OnClickListener {
	private static final String tag = "NewsFeedAdapter";
	
	private Context mContext;
	private LayoutInflater mInflater;

	private RadioGroup mdiscussionScoreRadioGroup;
	static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
    		System.out.println("Radio id: " + radioGroup.getId());
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
            	if (radioGroup.getChildAt(j).isEnabled()) {
	                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
	                view.setChecked(view.getId() == i);
            	}
            }
        }
    };
    
	public NewsFeedArrayAdapter(Context context, int layoutid, List<Content> content) {
		super(context, layoutid, content);
		
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_discussion, null);
		}
		TextView t = (TextView) convertView.findViewById(R.id.headerTextView);
		final TextView d = (TextView) convertView.findViewById(R.id.descTextView);
		TextView g = (TextView) convertView.findViewById(R.id.groupTextView);
		TextView u = (TextView) convertView.findViewById(R.id.usernameTextView);
		TextView c = (TextView) convertView.findViewById(R.id.creationDateTextView);
		TextView s = (TextView) convertView.findViewById(R.id.totalScoreTextView);

		
		t.setText(getItem(position).getTitle());
		d.setText(getItem(position).getDescription());
		g.setText(getItem(position).getGroup());
		u.setText(getItem(position).getAuthor().getName());
		c.setText(getItem(position).getDate());
		s.setText("" + getItem(position).getScore());
		
		//generic idea for expanding ellipsized text
		d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Layout l = d.getLayout();
				if (l != null) {
					int lines = l.getLineCount();
					if (lines > 0)
						if (l.getEllipsisCount(lines-1) > 0) {
							d.setMaxLines(Integer.MAX_VALUE);
							d.setEllipsize(null);
						}
						else {
							d.setMaxLines(4);
							d.setEllipsize(TextUtils.TruncateAt.END);
						}
					d.invalidate();
				}
			}
			
		});
		
		//((RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup)).setOnClickListener(this);
		mdiscussionScoreRadioGroup = (RadioGroup) convertView.findViewById(R.id.discussionScoreRadioGroup);
		mdiscussionScoreRadioGroup.setOnCheckedChangeListener(ToggleListener);
		final Button mButtonLike = (ToggleButton) convertView.findViewById(R.id.likeBtn);
		final Button mButtonDislike = (ToggleButton) convertView.findViewById(R.id.dislikeBtn);
		mButtonLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("pos in ListView: " + ((PullToRefreshListView) parent).getPositionForView(v));
				((RadioGroup)v.getParent()).check(v.getId());
			}
			
		});
		mButtonDislike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("pos in Listview: " + ((PullToRefreshListView) parent).getPositionForView(v));
				((RadioGroup)v.getParent()).check(v.getId());
			}
			
		});

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

	@Override
	public void onClick(View v) {

	}

}
