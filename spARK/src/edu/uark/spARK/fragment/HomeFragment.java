package edu.uark.spARK.fragment;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.uark.spARK.R;
import edu.uark.spARK.activity.MainActivity;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;

public class HomeFragment extends Fragment implements AsyncResponse {
	final String[] CONTENT = {"Headlines", "Events", "Groups", "Discussions"};
	ExpandableListView mListView;
	NewAdapter mAdapter;
	ArrayList<String> groupItem = new ArrayList<String>();
	ArrayList<Object> childItem = new ArrayList<Object>();
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupItem.add("Headlines");
        groupItem.add("Events");
        groupItem.add("Groups");
        groupItem.add("Discussions");
        
        ArrayList<String> child = new ArrayList<String>();

        child.add("Test 1");
        childItem.add(child);
        childItem.add(child);  
        childItem.add(child);  
        childItem.add(child);  
        
	    mAdapter = new NewAdapter(groupItem, childItem);
	    mAdapter.setInflater((LayoutInflater) getActivity().getLayoutInflater(), getActivity());
	    
    }
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);		
		View v = inflater.inflate(R.layout.fragment_home, container, false);		
		mListView = (ExpandableListView) v.findViewById(R.id.fragment_home_list_view);
		mListView.setGroupIndicator(null);
	    mListView.setAdapter(mAdapter);
		for (int groupPosition = 0; groupPosition < mAdapter.getGroupCount(); groupPosition++)
			mListView.expandGroup(groupPosition);
		//keep group from collapsing
		mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
		{
		    public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId)
		    {
		        mListView.expandGroup(itemPosition);
		        return true;
		    }
		});
		return v;
	}
	
	    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onStart() {
		super.onStart();

	}
	
	@Override
	public void onStop() {
		super.onStop();

	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}
	@Override
	public void processFinish(JSONObject result) {
		// TODO Pull main data to show in the home page
		
	}

	public class NewAdapter extends BaseExpandableListAdapter {

		 public ArrayList<String> groupItem, tempChild;
		 public ArrayList<Object> Childtem = new ArrayList<Object>();
		 public LayoutInflater minflater;
		 public Activity activity;

		 public NewAdapter(ArrayList<String> grList, ArrayList<Object> childItem) {
		  groupItem = grList;
		  this.Childtem = childItem;
		 }

		 public void setInflater(LayoutInflater mInflater, Activity act) {
		  this.minflater = mInflater;
		  activity = act;
		 }

		 @Override
		 public Object getChild(int groupPosition, int childPosition) {
		  return null;
		 }

		 @Override
		 public long getChildId(int groupPosition, int childPosition) {
		  return 0;
		 }

		 @Override
		 public View getChildView(final int groupPosition, final int childPosition,
		   boolean isLastChild, View convertView, ViewGroup parent) {
		  tempChild = (ArrayList<String>) Childtem.get(groupPosition);
		  TextView text = null;
		  //if (convertView == null) {
			  switch (groupPosition) {
			  case 0:	//headlines
				  convertView = minflater.inflate(R.layout.home_list_item_headline, null);
				  break;
			  case 1:	//events
				  convertView = minflater.inflate(R.layout.home_list_item_event, null);
				  break;
			  case 2:	//groups
				  convertView = minflater.inflate(R.layout.home_list_item_group, null);
				  break;
			  case 3:	//discussions
				  convertView = minflater.inflate(R.layout.home_list_item_discussion, null);
				  break;
			  default:	//shouldn't happen
				  convertView = minflater.inflate(R.layout.home_list_item_headline, null);
				  break;
			 // }
		  }
//		  LinearLayout llMain = (LinearLayout) convertView.findViewById(R.id.home_list_item_linearlayout);
//		  TextView tv = new TextView(getActivity());
//		  tv.setText(tempChild.get(childPosition));
//		  llMain.addView(tv);
//		  llMain.addView(tv);
//		  //text = (TextView) convertView.findViewById(R.id.placerow_name);
//		  //text.setText(tempChild.get(childPosition));
		  convertView.setOnClickListener(new OnClickListener() {
		   @Override
		   public void onClick(View v) {
		    Toast.makeText(activity, tempChild.get(childPosition) + " " + groupPosition,
		      Toast.LENGTH_SHORT).show();
		   }
		  });
		  return convertView;
		 }

		 @Override
		 public int getChildrenCount(int groupPosition) {
		  return ((ArrayList<String>) Childtem.get(groupPosition)).size();
		 }

		 @Override
		 public Object getGroup(int groupPosition) {
		  return null;
		 }

		 @Override
		 public int getGroupCount() {
		  return groupItem.size();
		 }

		 @Override
		 public void onGroupCollapsed(int groupPosition) {
		  super.onGroupCollapsed(groupPosition);
		 }

		 @Override
		 public void onGroupExpanded(int groupPosition) {
		  super.onGroupExpanded(groupPosition);
		 }

		 @Override
		 public long getGroupId(int groupPosition) {
		  return 0;
		 }

		 @Override
		 public View getGroupView(int groupPosition, boolean isExpanded,
		   View convertView, ViewGroup parent) {
		  if (convertView == null) {
		   convertView = minflater.inflate(R.layout.home_list_group, null);
		  }
		  ((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
		  ((CheckedTextView) convertView).setChecked(isExpanded);
		  return convertView;
		 }

		 @Override
		 public boolean hasStableIds() {
		  return false;
		 }

		 @Override
		 public boolean isChildSelectable(int groupPosition, int childPosition) {
		  return false;
		 }
	}
}
