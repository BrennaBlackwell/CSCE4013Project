package edu.uark.spARK;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import edu.uark.spARK.NewsFeedArrayAdapter.ViewHolder;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.User;

public class CommentActivity extends Activity {

	// method to allow updating the UI whenever new content is added
	// this is needed because main discussion in this activity isn't part of
	// an arrayadapter, not really sure if this is the right way to do it
	public void refreshDiscussion(ViewHolder holder) {
		holder.titleTextView.setText(mDiscussion.getTitle());
		holder.descTextView.setText(mDiscussion.getText());
		holder.groupTextView.setVisibility(View.GONE);
		holder.usernameTextView.setText(mDiscussion.getCreator().getTitle());
		holder.totalScoreTextView.setText("0"); // FIX LATER
		holder.creationDateTextView.setVisibility(View.GONE);
		holder.totalScoreTextView.setText("0"); // FIX LATER
		holder.commentTextView.setText(mDiscussion.getComments().size() + " comments");		
	}
	
	private CommentArrayAdapter mCommentArrayAdapter;
	private Discussion mDiscussion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		//initialize original discussion view
		mDiscussion = (Discussion) getIntent().getSerializableExtra("Object");
		
		
		//get user from preferences (in this case just a test user)
		final User u1 = new User(1, "test", null);
		
//		FrameLayout rl = (FrameLayout) findViewById(android.R.id.content);
		final ListView lv = (ListView) findViewById(R.id.commentListView);
//		LinearLayout ll = (LinearLayout) findViewById(R.id.addCommentLinearLayout);
//		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lv.getLayoutParams();

		mCommentArrayAdapter = new CommentArrayAdapter(getApplicationContext(), R.layout.comment_list_item, mDiscussion.getComments());
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
				String s = e.getText().toString();
				if (s.matches("")) 
					//prevent user from posting
					return;
				else {
					//post comment
					//get user name from stored preferences or something
					User u = new User(0, "test", null);
					Comment c = new Comment(0, null, s, u);
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
		holder.groupTextView.setVisibility(View.GONE);
		holder.usernameTextView.setText(mDiscussion.getCreator().getTitle());
		holder.totalScoreTextView.setText("0"); // FIX LATER
		holder.creationDateTextView.setVisibility(View.GONE);
		holder.totalScoreTextView.setText("0"); // FIX LATER
		holder.commentTextView.setText(mDiscussion.getComments().size() + " comments");

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
}
