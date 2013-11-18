package edu.uark.spARK;

import java.util.List;

import edu.uark.spARK.NewsFeedArrayAdapter.ViewHolder;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Content;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

//Adapter specifically for the commentactivity - needs to be implemented differently as arrayadapter is not really necessary (maybe baseadapter or something)
public class DiscussionCommentArrayAdapter extends ArrayAdapter {
	
	private static final String tag = "DiscussionCommentArrayAdapter";
	
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Comment> mListComments;
	ViewHolder holder;
	
	
	public DiscussionCommentArrayAdapter(Context context, int layoutid, List<Comment> comments) {
		super(context, layoutid, comments);
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.comment_list_item, null);
			
			holder.commentTextView = (TextView) convertView.findViewById(R.id.commentTextView);
			holder.usernameTextView = (TextView) convertView.findViewById(R.id.usernameTextView);
			
			holder.commentTextView.setTag(position);
			holder.usernameTextView.setTag(position);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Comment c = (Comment) this.getItem(position);
		
		holder.commentTextView.setText(c.getText());
		holder.usernameTextView.setText(c.getCreator().getTitle());

		return convertView;
	}
	
	//use this to load items, saving performance from not having to lookup id
	static class ViewHolder {
		TextView commentTextView;
		TextView usernameTextView;
		QuickContactBadge userQuickContactBadge;

		int position;
	}
}
