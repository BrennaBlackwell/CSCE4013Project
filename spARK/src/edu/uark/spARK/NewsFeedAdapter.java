package edu.uark.spARK;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsFeedAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private final String[] TEST = {
			"Why did the deer cross the road? \n\n" + 
			"Because humans built a road through its territory, damaging its habitat and forcing it to go through traffic to reach its feeding zone.", 
			"What did Batman say to Robin before they got in the Batmobile? \n\n" + 
				"Robin, get in the Batmobile.", 
			"A horse walks into a bar. The bartender asks, why the long face? \n\n" + 
					"The horse responds, My rampant alcoholism is tearing my marriage apart",
			"An owl and a squirrel are sitting in a tree, watching a farmer go by. The owl turns to the squirrel and says nothing, because owls can't talk. The owl then eats the squirrel because it's a bird of prey."};
	
	public NewsFeedAdapter(Context context) {
		mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return TEST.length;
	}
	

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return TEST[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_discussion, null);
		}
		TextView tv1 = (TextView) convertView.findViewById(R.id.headerTextView);
		tv1.setText("Anti-joke " + (position+1));
		TextView tv2 = (TextView) convertView.findViewById(R.id.descTextView);
		tv2.setText(TEST[position]);
		
		return convertView;
	}

}
