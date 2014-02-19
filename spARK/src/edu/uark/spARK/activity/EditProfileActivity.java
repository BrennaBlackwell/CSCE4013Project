package edu.uark.spARK.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import edu.uark.spARK.R;
import edu.uark.spARK.data.JSONQuery;
import edu.uark.spARK.data.JSONQuery.AsyncResponse;
import edu.uark.spARK.data.LoadImages;
import edu.uark.spARK.data.ServerUtil;
import edu.uark.spARK.fragment.MyProfileFragment;


public class EditProfileActivity extends Activity implements AsyncResponse {
	private static final int SELECT_PHOTO = 100; 
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    private ImageView myImageView;
    private Bitmap tempPhoto;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_edit_profile);
		// Show the Up button in the action bar.
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		myImageView = (ImageView) findViewById(R.id.profileImageView);
        TextView userName = (TextView)findViewById(R.id.userName);
		EditText userFullName = (EditText)findViewById(R.id.userFullNameField);
		EditText aboutMeField = (EditText)findViewById(R.id.aboutMeField);
		userName.setText(MainActivity.myUsername);
		userFullName.setText(MainActivity.myFullName);
		aboutMeField.setText(MainActivity.myDesc);
		myImageView.setImageBitmap(MainActivity.myProfilePicture);
		
		Button photoButton = (Button) findViewById(R.id.addPhoto);
		photoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
			}
		});
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	switch(requestCode) { 
        case SELECT_PHOTO:
        	Cursor cursor = null;
        	ParcelFileDescriptor parcelFD = null;
        	try {
	            if(resultCode == RESULT_OK){  
	                Uri selectedImage = intent.getData();
	                parcelFD = getContentResolver().openFileDescriptor(selectedImage, "r");
	                FileDescriptor imageSource = parcelFD.getFileDescriptor();
	         
	                tempPhoto = LoadImages.decodeSampledBitmapFromResource(imageSource, 130, 130);
	                myImageView.setImageBitmap(tempPhoto);
	                
	                String[] proj = { MediaStore.Images.Media.DATA };
	        	    cursor = getApplicationContext().getContentResolver().query(selectedImage,  proj, null, null, null);
	        	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        	    cursor.moveToFirst();
	        	    filePath = cursor.getString(column_index);
	        	    
	            }
        	} catch (FileNotFoundException e) {
                // handle errors
            } finally {
                if (parcelFD != null) {
                    try {
                        parcelFD.close();
                    } catch (IOException e) {
                        // ignored
                    }
                } if (cursor != null) {
          	      cursor.close();
          	    }
            }
            break;
    	}
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
    
	public void Save(View v){
		EditText userFullName = (EditText)findViewById(R.id.userFullNameField);
		String fullname = userFullName.getText().toString();
		MainActivity.myFullName = fullname;
		
		EditText aboutMeField = (EditText)findViewById(R.id.aboutMeField);
		String aboutme = aboutMeField.getText().toString();
		MainActivity.myDesc = aboutme;
		
		MainActivity.myProfilePicture = tempPhoto;
        myImageView.setImageBitmap(MainActivity.myProfilePicture);
		
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        MainActivity.myProfilePicture.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        String encoding = Base64.encodeToString(ba, Base64.DEFAULT);
        
		JSONQuery jquery = new JSONQuery(this);
        jquery.execute(ServerUtil.URL_EDIT_PROFILE, Integer.toString(MainActivity.myUserID), MainActivity.myFullName, MainActivity.myDesc, encoding, filePath);
	}
	
	public void Cancel(View v){
		myImageView.setImageBitmap(MainActivity.myProfilePicture);
		finish();
	}
    
	@Override
	public void processFinish(JSONObject result) {
		try { 
			int success = result.getInt("success");
			if (success == 1) {
				Intent MyProfile = new Intent(this, MyProfileFragment.class);
				setResult(0, MyProfile);
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
