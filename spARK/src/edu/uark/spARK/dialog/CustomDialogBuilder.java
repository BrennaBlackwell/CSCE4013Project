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

		private Context mContext;
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
        mContext = context;
        mView = View.inflate(context, R.layout.alert_dialog_holo, null);
        setView(mView);

        mTitle = (TextView) mView.findViewById(R.id.alertTitle);
        mIcon = (ImageView) mView.findViewById(R.id.icon);
        mDivider = mView.findViewById(R.id.titleDivider);
        mMessage = (TextView) mView.findViewById(R.id.message);

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
    
    public View getView() {
    	return mView;
    }

    public CustomDialogBuilder setCustomView(int resId, Context context) {
            View customView = View.inflate(context, resId, null);
            ((FrameLayout)mView.findViewById(R.id.customPanel)).addView(customView);
            return this;
    }
    
    public CustomDialogBuilder setCustomView(View v, Context context) {
        ((FrameLayout)mView.findViewById(R.id.customPanel)).addView(v);
        return this;
    }
    
    public FrameLayout getCustomView() {     
        return ((FrameLayout)mView.findViewById(R.id.customPanel));
    }
    
    @Override
    public AlertDialog show() {
            if (mTitle.getText().equals("")) 
            	mView.findViewById(R.id.topPanel).setVisibility(View.GONE);
            return super.show();
    }
    
    

}