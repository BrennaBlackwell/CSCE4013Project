package edu.uark.spARK;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.QuickContactBadge;
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
		holder.groupAndDateTextView.setVisibility(View.GONE);
		// TODO: What would userProfileIcon set to onRefresh?
		//holder.userProfileIcon.setImageAlpha(??);
		holder.usernameTextView.setText(mDiscussion.getCreator().getName());
		holder.totalScoreTextView.setText(String.valueOf(mDiscussion.getTotalRating()));
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
		holder.commentTextView.setOnTouchListener(new View.OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        return true;
		    }
		});
		holder.groupAndDateTextView = (TextView) findViewById(R.id.groupAndDateTextView);
		holder.userProfileIcon = (QuickContactBadge) findViewById(R.id.userQuickContactBadge);
		holder.usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		holder.totalScoreTextView = (TextView) findViewById(R.id.totalScoreTextView);		
		holder.likeBtn = (ToggleButton) findViewById(R.id.likeBtn);	
		holder.dislikeBtn = (ToggleButton) findViewById(R.id.dislikeBtn);	
		if (mDiscussion.getUserRating() == 1) {
			holder.likeBtn.setChecked(true);
			holder.dislikeBtn.setChecked(false);
		} else if (mDiscussion.getUserRating() == -1) {
			holder.likeBtn.setChecked(false);
			holder.dislikeBtn.setChecked(true);
		} else {
			holder.likeBtn.setChecked(false);
			holder.dislikeBtn.setChecked(false);
		}
		
		holder.deleteBtn = (Button) findViewById(R.id.trashBtn);
		if (mDiscussion.getCreator().getId() != MainActivity.myUserID) {
			holder.deleteBtn.setVisibility(View.GONE);
		}
		
		holder.scoreRadioGroup = (RadioGroup) findViewById(R.id.discussionScoreRadioGroup);
		
		holder.scoreRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
	        @Override
	        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
	            for (int j = 0; j < radioGroup.getChildCount(); j++) {
	            	if (radioGroup.getChildAt(j).isEnabled()) {
	            		final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
	            		if (view.getId() == i) {
	            			view.setChecked(true);
	            		} else {
	            			view.setChecked(false);
	            		}
	            	}
	            }
	        }
	    });	
		
		holder.likeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONQuery jquery = new JSONQuery(CommentActivity.this);
				ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(0);
				
				if (button.isChecked()) {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(mDiscussion.getId()), "like");
					
					((RadioGroup)v.getParent()).check(v.getId());	
					mDiscussion.incrementRating();
					refreshDiscussion(holder);
				} else {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(mDiscussion.getId()), "delete");
					
					((RadioGroup)v.getParent()).check(v.getId());
					mDiscussion.decrementRating();
					refreshDiscussion(holder);
				}
			}
			
		});
		holder.dislikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				JSONQuery jquery = new JSONQuery(CommentActivity.this);
				ToggleButton button = (ToggleButton)((RadioGroup)v.getParent()).getChildAt(2);
				
				if (button.isChecked()) {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(mDiscussion.getId()), "dislike");
					
					((RadioGroup)v.getParent()).check(v.getId());
					mDiscussion.decrementRating();
					refreshDiscussion(holder);
				} else {
					jquery.execute(ServerUtil.URL_LIKE_DISLIKE, Integer.toString(MainActivity.myUserID), Integer.toString(mDiscussion.getId()), "delete");
					
					((RadioGroup)v.getParent()).check(v.getId());	
					mDiscussion.incrementRating();
					refreshDiscussion(holder);
				}
			}
			
		});
		
//		TODO:  Users need to be able to view other users profiles within CommentActivity.
//			   Having trouble getting the following code to work
		
//		holder.userProfileIcon.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO: Need to implement addToBackStack on Profiles
//				Fragment profileFragment;
//				if (MainActivity.myUserID == mDiscussion.getCreator().getId()) {
//					profileFragment = new MyProfile_Fragment() {
//						@Override
//					    public void onCreate(Bundle savedInstanceState) {
//						    super.onCreate(savedInstanceState);
//							    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//						    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//						    	setHasOptionsMenu(true);
//						    }
//					};
//				} else {
//					profileFragment = new Profile_Fragment();
//					Bundle args = new Bundle();
//					args.putSerializable("ContentCreator", (User) mDiscussion.getCreator());
//		            profileFragment.setArguments(args);
//				}
//	            
//				//fragment.getFragmentManager().beginTransaction().add(fragment.getView().getId(), profileFragment).commit();
//				((MainActivity) NewsFeedArrayAdapter.fragment.getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
//				NewsFeedArrayAdapter.fragment.getFragmentManager().beginTransaction()
//		        .add(R.id.fragment_frame, profileFragment).commit();
//		        MainActivity.mPager.setVisibility(View.GONE);		           
//			}			
//		});
		
		holder.deleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				final int contentID = mDiscussion.getId();
				final int pos = 0;
				final String contentType = "Discussion";
				
//				TODO: Fix processFinish() before uncommenting here
//				JSONQuery jquery = new JSONQuery(CommentActivity.this);
//              jquery.execute(ServerUtil.URL_DELETE_CONTENT, Integer.toString(MainActivity.myUserID), Integer.toString(contentID), contentType, Integer.toString(pos));
                
                
//				new AlertDialog.Builder(mContext)
//		        .setIcon(android.R.drawable.ic_dialog_alert)
//		        .setTitle("Delete " + contentType)
//		        .setMessage("Are you sure you want to delete this item?")
//		        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//		    {
//		        @Override
//		        public void onClick(DialogInterface dialog, int which) {
//		        	JSONQuery jquery = new JSONQuery(NewsFeedArrayAdapter.this);
//                
//					TODO: Wish I could get the following to work instead. For some reason 
//                	here the contentID, contentType, and pos values are unknown. Check in debug for details
//                
//                  jquery.execute(ServerUtil.URL_DELETE_CONTENT, Integer.toString(MainActivity.myUserID), Integer.toString(contentID), contentType, Integer.toString(pos));
//		        }
//
//		    })
//		    .setNegativeButton("No", null)
//		    .show();
			}
		});
		
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
					JSONQuery jquery = new JSONQuery(CommentActivity.this);
					jquery.execute(ServerUtil.URL_POST_COMMENT, Integer.toString(MainActivity.myUserID), Integer.toString(mDiscussion.getId()), body);
					
					User u = new User(MainActivity.myUserID, MainActivity.myUsername, null, MainActivity.myFullName, MainActivity.myDesc, 0);
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
		holder.groupAndDateTextView.setVisibility(View.GONE);
		holder.usernameTextView.setText(mDiscussion.getCreator().getName());
		holder.totalScoreTextView.setText(String.valueOf(mDiscussion.getTotalRating()));
		holder.commentTextView.setText(mDiscussion.getNumComments() + " comments");

		// Show the Up button in the action bar.
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setTitle("test");
		ab.setSubtitle(mDiscussion.getCreationDateAsPrettyTime());
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
			int success = result.getInt("deleteSuccess");
			if (success == 1) {
//				TODO: Need to remove current discussion from NewsFeedArrayAdapter
//				mContent.remove(mDiscussion);
//				notifyDataSetChanged();
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
