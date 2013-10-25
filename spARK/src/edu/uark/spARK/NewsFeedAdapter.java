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
	private final String[] TEST = {"Test 1", "Test 2", "Test 3", "Test 4"};
	
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
		TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
		tv1.setText(TEST[position]);
		
		return convertView;
	}

}
