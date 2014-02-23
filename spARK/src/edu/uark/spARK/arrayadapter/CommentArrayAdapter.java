package edu.uark.spARK.arrayadapter;

import java.util.List;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.*;
import android.widget.*;
import edu.uark.spARK.R;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import edu.uark.spARK.entity.Comment;

//Adapter specifically for the commentactivity - needs to be implemented differently as arrayadapter is not really necessary (maybe baseadapter or something)
public class CommentArrayAdapter extends ArrayAdapter {
	
	private static final String tag = "DiscussionCommentArrayAdapter";
	
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Comment> mListComments;
	ViewHolder holder;
	
	
	public CommentArrayAdapter(Context context, int layoutid, List<Comment> comments) {
		super(context, layoutid, comments);
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.content_list_item_comment, null);
			
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
		holder.commentTextView.setMovementMethod(LinkMovementMethod.getInstance());
		Linkify.addLinks(holder.commentTextView, Linkify.ALL);
		holder.usernameTextView.setText(c.getCreator().getName());

		return convertView;
	}
	
	//use this to load items, saving performance from not having to lookup id
	static class ViewHolder {
		TextView commentTextView;
		TextView usernameTextView;
		ImageView userQuickContactBadge;

		int position;
	}
}
