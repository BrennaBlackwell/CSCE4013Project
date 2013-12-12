package edu.uark.spARK;

import java.util.ArrayList;

import org.json.*;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.entities.Group;
import edu.uark.spARK.entities.User;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class Groups_Fragment extends Fragment implements AsyncResponse {

	public GroupsArrayAdapter mAdapter;
	private PullToRefreshListView mListView;
	
	public ArrayList<Group> arrayListGroup = new ArrayList<Group>();
	
	public static Groups_Fragment newInstance() {
		return new Groups_Fragment();
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public Groups_Fragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mAdapter = new GroupsArrayAdapter(getActivity(), R.layout.group_list_item, arrayListGroup, this);
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadContent();
			}
			
		});
        loadContent();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.list_feed, container, false);
		
		mListView = (PullToRefreshListView) v.findViewById(R.id.pullToRefreshListView);

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

	public void loadContent() {
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_LOAD_ALL_POSTS, MainActivity.myUsername, "Group");
	}
	
	@Override
	public void processFinish(JSONObject result) {
		try {
			int success = result.getInt(ServerUtil.TAG_SUCCESS);

			if (success == 1) {
				arrayListGroup.clear();
				// Get Array of groups
				JSONArray groups = result.getJSONArray(ServerUtil.TAG_GROUPS);

				for (int i = 0; i < groups.length(); i++) {
					JSONObject g = groups.getJSONObject(i);
					
					// Group
					int groupID = g.getInt(ServerUtil.TAG_GROUP_ID);
					String groupName = g.getString(ServerUtil.TAG_GROUP_NAME).trim();
					String groupDesc = g.getString(ServerUtil.TAG_GROUP_DESC).trim();
//					Date date = Timestamp.valueOf(g.getString(ServerUtil.TAG_TIMESTAMP).trim());
					String latitude = g.getString(ServerUtil.TAG_LATITUDE).trim();     
					String longitude = g.getString(ServerUtil.TAG_LONGITUDE).trim();
					boolean open = g.getString(ServerUtil.TAG_PRIVACY).equalsIgnoreCase("Open");
					boolean visible = g.getString(ServerUtil.TAG_VISIBILITY).equalsIgnoreCase("Visible");
					
					// Group Creator
//					int contentUserID = g.getInt(ServerUtil.TAG_USER_ID);
					String contentUsername = g.getString(ServerUtil.TAG_USER_NAME).trim();
					String contentUserFullName = g.getString(ServerUtil.TAG_USER_FULL_NAME).trim();
					String contentUserDesc = g.getString(ServerUtil.TAG_USER_DESC).trim();
					User user = new User(/*contentUserID*/0, contentUsername, "", contentUserFullName, contentUserDesc, 0);
					
					Group group = new Group(groupID, groupName, groupDesc, user, latitude, longitude, open, visible);
					
					arrayListGroup.add(group);
				}
				mAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mListView.onRefreshComplete();
		
	}
	
	public PullToRefreshListView getListView() {
		return mListView;
	}
	
	public GroupsArrayAdapter getListAdapter() {
		return mAdapter;
	}

	public ArrayList<Group> getArrayListGroup() {
		return arrayListGroup;
	}

	public void setArrayListGroup(ArrayList<Group> arrayListGroup) {
		this.arrayListGroup = arrayListGroup;
	}
	
	//onActivityResult which is received by the fragment
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

	}
	
	@Override
	public void onStart() {
		super.onStart();

	}
	
	@Override
	public void onStop() {
		super.onStop();

	}
	
}
