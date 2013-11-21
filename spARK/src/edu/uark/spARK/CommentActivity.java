package edu.uark.spARK;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.NewsFeedArrayAdapter.ViewHolder;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.User;

public class CommentActivity extends Activity implements AsyncResponse{

	// method to allow updating the UI whenever new content is added
	// this is needed because main discussion in this activity isn't part of
	// an arrayadapter, not really sure if this is the right way to do it
	public void refreshDiscussion(ViewHolder holder) {
		holder.titleTextView.setText(mDiscussion.getTitle());
		holder.descTextView.setText(mDiscussion.getText());
		holder.groupTextView.setVisibility(View.GONE);
		holder.usernameTextView.setText(mDiscussion.getCreator().getName());
		holder.creationDateTextView.setText(mDiscussion.getCreationDateString());
		holder.totalScoreTextView.setText(String.valueOf(mDiscussion.getRating()));
		holder.commentTextView.setText(mDiscussion.getNumComments() + " comments");		
	}
	
	private CommentArrayAdapter mCommentArrayAdapter;
	private Discussion mDiscussion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		//initialize original discussion view
		mDiscussion = (Discussion) getIntent().getSerializableExtra("Object");
//		FrameLayout rl = (FrameLayout) findViewById(android.R.id.content);
		final ListView lv = (ListView) findViewById(R.id.commentListView);
		View header = getLayoutInflater().inflate(R.layout.comment_discussion_header, null);
		lv.addHeaderView(header);
//		LinearLayout ll = (LinearLayout) findViewById(R.id.addCommentLinearLayout);
//		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lv.getLayoutParams();

		mCommentArrayAdapter = new CommentArrayAdapter(this, R.layout.comment_list_item, mDiscussion.getComments());
		lv.setAdapter(mCommentArrayAdapter);
		
		final ViewHolder holder = new ViewHolder();
		holder.titleTextView = (TextView) findViewById(R.id.headerTextView);
		holder.descTextView = (TextView) findViewById(R.id.descTextView);	
		holder.commentTextView = (TextView) findViewById(R.id.commentTextView);
		holder.groupTextView = (TextView) findViewById(R.id.groupTextView);
		holder.usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		holder.creationDateTextView = (TextView) findViewById(R.id.creationDateTextView);
		holder.totalScoreTextView = (TextView) findViewById(R.id.totalScoreTextView);		
		holder.likeBtn = (ToggleButton) findViewById(R.id.likeBtn);	
		holder.dislikeBtn = (ToggleButton) findViewById(R.id.dislikeBtn);
		holder.scoreRadioGroup = (RadioGroup) findViewById(R.id.discussionScoreRadioGroup);
		
		ImageButton ib = (ImageButton) findViewById(R.id.postCommentBtn);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText e = (EditText) findViewById(R.id.addCommentEditText);
				String body = e.getText().toString();
				if (body.matches("")) 
					//prevent user from posting
					return;
				else {
					//post comment
					//get user name from stored preferences or something
					SharedPreferences preferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
					String currentUsername = preferences.getString("currentUsername", "");
					
					JSONQuery jquery = new JSONQuery(CommentActivity.this);
					jquery.execute(ServerUtil.URL_POST_COMMENT, currentUsername, Integer.toString(mDiscussion.getId()), body);
					
					User u = new User(0, currentUsername, null);
					Comment c = new Comment(0, body, u);
					mDiscussion.addComment(c);
					e.setText("");
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
					mCommentArrayAdapter.notifyDataSetChanged();
					refreshDiscussion(holder);
					lv.setSelection(mCommentArrayAdapter.getCount() - 1);
				}
			}
			
		});

		holder.titleTextView.setText(mDiscussion.getTitle());
		holder.descTextView.setText(mDiscussion.getText());
		holder.descTextView.setMovementMethod(LinkMovementMethod.getInstance());
		Linkify.addLinks(holder.descTextView, Linkify.ALL);
		holder.groupTextView.setVisibility(View.GONE);
		holder.usernameTextView.setText(mDiscussion.getCreator().getName());
		holder.creationDateTextView.setText(mDiscussion.getCreationDateString());
		holder.totalScoreTextView.setText(String.valueOf(mDiscussion.getRating()));
		holder.commentTextView.setText(mDiscussion.getNumComments() + " comments");

		// Show the Up button in the action bar.
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setTitle("test");
		ab.setSubtitle(mDiscussion.getCreationDateString());
	    ab.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
//		EditText addComment = (EditText) menu.findItem(R.id.menuAddCommentTextView).getActionView();
//		
//		addComment.setTextSize(24.0f);
//		//addComment.getLayoutParams().width = LayoutParams.MATCH_PARENT;
//		Point size = new Point();
//		getWindow().getWindowManager().getDefaultDisplay().getSize(size);
//		addComment.setWidth(size.x);
//		addComment.invalidate();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Pass the result back to ListFragment
	@Override
	public void finish() {
	    getIntent().putExtra("Object", mDiscussion);
	    setResult(RESULT_OK, getIntent());
	    super.finish();  
	}
	
	@Override
	public void processFinish(JSONObject result) {
		try { 
			// Checking for SUCCESS TAG
			int success = result.getInt("success");
			if (success == 1 ) {
				// Comment posted successfully
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
