package edu.uark.spARK.arrayadapter;

import java.util.Map;

import edu.uark.spARK.R;
import edu.uark.spARK.R.drawable;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import edu.uark.spARK.activity.MainActivity;
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
		{MainActivity.myUsername, "Home", "Headlines", "Events", "Groups", "Discussions", "Settings", "Logout"};
	private static Drawable MENU_ICON[] = new Drawable[MENU.length];
	//private static ArrayList<String,Drawable> MENU = new ArrayList<String,Drawable>();
	private static Map<Integer, Drawable> menuMap;
	private LayoutInflater mInflater;
	private Context context;
	private int resourceID;
	private Drawable userIcon;
	
	public NavListArrayAdapter(Context context, int layoutid) {
		super(context, layoutid, MENU);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.resourceID = layoutid;
		
		MENU_ICON[0] = context.getResources().getDrawable(R.drawable.drawer_profile);
		MENU_ICON[1] = context.getResources().getDrawable(R.drawable.drawer_home);
		MENU_ICON[2] = context.getResources().getDrawable(R.drawable.drawer_headlines);
		MENU_ICON[3] = context.getResources().getDrawable(R.drawable.drawer_event);
		MENU_ICON[4] = context.getResources().getDrawable(R.drawable.drawer_group);
		MENU_ICON[5] = context.getResources().getDrawable(R.drawable.drawer_discussion);
		MENU_ICON[6] = context.getResources().getDrawable(R.drawable.drawer_preferences);
		MENU_ICON[7] = context.getResources().getDrawable(R.drawable.drawer_logout);

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String s;
		//if statement to determine if the position is the account, which requires different layout
		//if (convertView == null) {
		if (position == 0) {
				convertView = mInflater.inflate(R.layout.drawer_account, null);
				s = MainActivity.myUsername;
		}
		else {
				convertView = mInflater.inflate(R.layout.drawer_list_item, null);
				s = MENU[position];
		}

		ImageView imageview = (ImageView) convertView.findViewById(R.id.navListOptionIcon);
		imageview.setImageDrawable (MENU_ICON[position]);
		convertView.setTag(position);

		TextView tv = (TextView) convertView.findViewById(R.id.navListOptionTextView);
		tv.setText(s);
		return convertView;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}
	
}
