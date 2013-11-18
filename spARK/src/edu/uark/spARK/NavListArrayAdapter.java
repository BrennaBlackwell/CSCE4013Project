package edu.uark.spARK;

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
	private static final String MENU[] = 
		{"Account", "Home", "Check In", "Groups", "Bookmarks", "Settings", "About", "Logout"};
	
	private LayoutInflater mInflater;
	private Context context;
	private int resourceID;
	private Drawable userIcon;
	
	public NavListArrayAdapter(Context context, int layoutid) {
		super(context, layoutid, MENU);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.resourceID = layoutid;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//System.out.println("getView " + position + " " + convertView);
		Drawable icon;
		//if statement to determine if the position is the account, which requires different layout
		//if (convertView == null) {
			switch (position) {
				case 0:	//profile
					convertView = mInflater.inflate(R.layout.drawer_account, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_profile);
					break;
				case 1: //home
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.drawer_home);
					break;
				case 2:	//check in
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_checkin);
					break;
				case 3:	//groups
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_group);
					break;
				case 4:	//bookmarks
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_bookmarks);
					break;
				case 5:	//settings
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_preferences);
					break;
				case 6:	//about
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_about);
					break;
				case 7:	//logout
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_logout);
					break;
				default:
					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					icon = convertView.getResources().getDrawable(R.drawable.ic_menu_logout);
					break;
			}
			ImageView imageview = (ImageView) convertView.findViewById(R.id.navListOptionIcon);
			imageview.setImageDrawable (icon);
		//}
		//else {
			
		//}	
		TextView textview = (TextView) convertView.findViewById(R.id.navListOptionTextView);
		String s = MENU[position];
		textview.setText(s);
		return convertView;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}
	
}
