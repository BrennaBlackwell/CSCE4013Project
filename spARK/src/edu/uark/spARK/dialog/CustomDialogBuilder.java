package edu.uark.spARK.dialog;

import edu.uark.spARK.R;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CustomDialogBuilder extends AlertDialog.Builder {

        private View mView;
        private TextView mTitle;
        private ImageView mIcon;
        private TextView mMessage;
        private ListView mItems;
        private ArrayAdapter<CharSequence> mAdapter;
        private View mDivider;
        public DialogInterface.OnClickListener mOnClickListener;
        
    public CustomDialogBuilder(Context context) {
        super(context);

        mView = View.inflate(context, R.layout.alert_dialog_holo, null);
        setView(mView);

        mTitle = (TextView) mView.findViewById(R.id.alertTitle);
        mIcon = (ImageView) mView.findViewById(R.id.icon);
        mDivider = mView.findViewById(R.id.titleDivider);
        mMessage = (TextView) mView.findViewById(R.id.message);
        mItems = (ListView) mView.findViewById(R.id.items);

        }

    public CustomDialogBuilder setDividerColor(int colorId) {
            mDivider.setBackgroundColor(colorId);
            return this;
    }
 
    @Override
    public CustomDialogBuilder setTitle(CharSequence text) {
        mTitle.setText(text);
        return this;
    }

    public CustomDialogBuilder setTitleColor(int colorId) {
            mTitle.setTextColor(colorId);
            return this;
    }

    @Override
    public CustomDialogBuilder setIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    @Override
    public CustomDialogBuilder setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }
    
    @Override
    public CustomDialogBuilder setMessage(int textResId) {
        mMessage.setText(textResId);
        return this;
    }

    @Override
    public CustomDialogBuilder setMessage(CharSequence text) {
        mMessage.setText(text);
        return this;
    }
    
    @Override
    public CustomDialogBuilder setItems(CharSequence[] items, final OnClickListener listener) {
//    	mAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_list_item_1, items);
//    	mItems.setAdapter(mAdapter);
//    	mItems.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int position,
//					long id) {
//				listener.onClick(AlertDialog this, position);
//			}
//    		
//    	});
    	return this;
    }

    public CustomDialogBuilder setCustomView(int resId, Context context) {
            View customView = View.inflate(context, resId, null);
            ((FrameLayout)mView.findViewById(R.id.customPanel)).addView(customView);
            return this;
    }
    
    @Override
    public AlertDialog show() {
            if (mTitle.getText().equals("")) 
            	mView.findViewById(R.id.topPanel).setVisibility(View.GONE);
            return super.show();
    }

}