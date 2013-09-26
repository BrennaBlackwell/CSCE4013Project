package edu.uark.csce.groupproject.spark;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavListArrayAdapter extends ArrayAdapter<String>{
	private static final String tag = "NavListArrayAdapter";
	
	private LayoutInflater mInflater;
	private Context context;
	private int resourceID;
	private Drawable userIcon;
	private final String[] options;
	
	public NavListArrayAdapter(Context context, int layoutid, String[] mListTitles) {
		super(context, layoutid, mListTitles);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.resourceID = layoutid;
		this.options = mListTitles;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("getView " + position + " " + convertView);
		//if statement to determine if the position is the account, which requires different layout
		if (convertView == null) {
			if (position == 0) {
				convertView = mInflater.inflate(R.layout.drawer_account, null);
			}
			else {
				convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			}
		}
		else {
			
		}	
		TextView textview = (TextView) convertView.findViewById(R.id.navListOptionTextView);
		ImageView imageview = (ImageView) convertView.findViewById(R.id.navListOptionIcon);

		String s = options[position];
		textview.setText(s);
		Log.d(tag, "XML Inflated!");	
		userIcon = convertView.getResources().getDrawable(R.drawable.ic_launcher);
		//just a test for preferences icon
		imageview.setImageDrawable(userIcon);

		return convertView;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}
	
}
