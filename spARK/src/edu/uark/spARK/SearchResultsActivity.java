package edu.uark.spARK;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.entities.Bulletin;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.Group;
import edu.uark.spARK.entities.User;

public class SearchResultsActivity extends Activity implements AsyncResponse {
    
    public ArrayList<Content> arrayListContent = new ArrayList<Content>();
    private JSONArray contents = null;
    private JSONArray comments = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }


    private void doMySearch(String query)
    {
    	JSONQuery jquery = new JSONQuery(SearchResultsActivity.this);
        jquery.execute(ServerUtil.URL_SEARCH, Integer.toString(MainActivity.myUserID), query);
    }

	@Override
	public void processFinish(JSONObject result) {
		try {
			int success = result.getInt(ServerUtil.TAG_SUCCESS);

			if (success == 1) {
				MainActivity.mMapViewFragment.clearMarkers();
				arrayListContent.clear();
				// Get Array of discussions
				contents = result.getJSONArray(ServerUtil.TAG_CONTENTS);

				for (int i = 0; i < contents.length(); i++) {
					JSONObject content = contents.getJSONObject(i);

					// Content
					int contentID = Integer.parseInt(content.getString(ServerUtil.TAG_ID));
					String contentTitle = content.getString(ServerUtil.TAG_TITLE).trim();
					String contentBody = content.getString(ServerUtil.TAG_BODY).trim();
					String contentType = content.getString(ServerUtil.TAG_TYPE).trim(); 
					Date contentTimestamp = Timestamp.valueOf(content.getString(ServerUtil.TAG_TIMESTAMP).trim());
					String latitude = content.getString(ServerUtil.TAG_LATITUDE).trim();     
					String longitude = content.getString(ServerUtil.TAG_LONGITUDE).trim(); 
					int totalRating = 0;
					int userRating = 0;
					if (content.getInt(ServerUtil.TAG_RATING_TOTAL_FLAG) == 1) {
						if (content.getString(ServerUtil.TAG_RATING_TOTAL) != null) {
							totalRating = Integer.parseInt(content.getString(ServerUtil.TAG_RATING_TOTAL));
						} 
					}
					
					
					// Creator
					int contentUserID = Integer.parseInt(content.getString(ServerUtil.TAG_USER_ID).trim());
					String contentUsername = content.getString(ServerUtil.TAG_USER_NAME).trim();
					String contentUserFullName = content.getString(ServerUtil.TAG_USER_FULL_NAME).trim();
					String contentUserDesc = content.getString(ServerUtil.TAG_USER_DESC).trim();
					if (content.getInt(ServerUtil.TAG_USER_RATING_FLAG) == 1) {
						if (content.getString(ServerUtil.TAG_USER_RATING) != null) {
							userRating = content.getInt(ServerUtil.TAG_USER_RATING);
						}
					}
					User user = new User(contentUserID, contentUsername, "", contentUserFullName, contentUserDesc, 0);
					
					
					// Group Attached
					int groupID = Integer.parseInt(content.getString(ServerUtil.TAG_GROUP_ID).trim());
					String groupName = content.getString(ServerUtil.TAG_GROUP_NAME).trim();
					String groupDesc = content.getString(ServerUtil.TAG_GROUP_DESC).trim();
					Group group = new Group(groupID, groupName, groupDesc);
					
					
					if (contentType.equals("Bulletin")) {
						Bulletin b = new Bulletin(contentID, contentTitle, contentBody, user, contentTimestamp, latitude, longitude, group);
						b.setTotalRating(totalRating);
						b.setUserRating(userRating);
						if (b.hasLocation()) {
							MainActivity.mMapViewFragment.addContent(b);
						}
						arrayListContent.add(b);
					} else if (contentType.equals("Discussion")) {
						comments = content.getJSONArray(ServerUtil.TAG_COMMENTS);
						List<Comment> commentsList = new ArrayList<Comment>();
						for (int j = 0; j < comments.length(); j++) {
							JSONObject comment = comments.getJSONObject(j);
	
							int commentID = Integer.parseInt(comment.getString(ServerUtil.TAG_ID).trim());
							int commentUserID = Integer.parseInt(comment.getString(ServerUtil.TAG_USER_ID).trim());
							String commentUsername = comment.getString(ServerUtil.TAG_USER_NAME).trim();
							String commentUserFullName = content.getString(ServerUtil.TAG_USER_FULL_NAME).trim();
							String commentUserDesc = content.getString(ServerUtil.TAG_USER_DESC).trim();
							String commentBody = comment.getString(ServerUtil.TAG_BODY).trim();
							Date commentTimestamp = Timestamp.valueOf(content.getString(ServerUtil.TAG_TIMESTAMP).trim());
							
							Comment c = new Comment(commentID, commentBody, new User(commentUserID, commentUsername,  commentUserFullName, commentUserDesc, "", 0), commentTimestamp);
							commentsList.add(c);
						}
						
						Discussion d = new Discussion(contentID, contentTitle, contentBody, user, contentTimestamp, latitude, longitude, commentsList, group);
						d.setTotalRating(totalRating);
						d.setUserRating(userRating);
						if (d.hasLocation()) {
							MainActivity.mMapViewFragment.addContent(d);
						}
						arrayListContent.add(d);
					}
					//MainActivity.mPager.getAdapter().getListAdapter().notifyDataSetChanged();
					//mListView.onRefreshComplete();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}			
	}
}

