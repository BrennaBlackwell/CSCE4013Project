package edu.uark.spARK.fragment;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.FrameLayout.LayoutParams;
import edu.uark.spARK.R;

public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		CustomTimePickerDialog newFragment = new CustomTimePickerDialog(
				getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity())) {

		};
		newFragment.setTitle("");

		// Divider changing:
		TimePicker tpView = (TimePicker) newFragment.getTimePicker();
		LinearLayout llFirst = (LinearLayout) tpView.getChildAt(0);
		// change am/pm color
		NumberPicker mpicker = (NumberPicker) llFirst.getChildAt(1); // Numberpickers
																		// in
																		// llSecond
		// reflection - picker.setDividerDrawable(divider); << didn't seem to
		// work.
		Field[] mpickerFields = NumberPicker.class.getDeclaredFields();
		for (Field pf : mpickerFields) {
			if (pf.getName().equals("mSelectionDivider")) {
				pf.setAccessible(true);
				try {
					pf.set(mpicker,
							getResources().getDrawable(
									R.drawable.selection_divider_red));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		LinearLayout llSecond = (LinearLayout) llFirst.getChildAt(0);
		for (int i = 0; i < llSecond.getChildCount(); i++) {
			if (llSecond.getChildAt(i).getClass().equals(NumberPicker.class)) {
				NumberPicker picker = (NumberPicker) llSecond.getChildAt(i); // Numberpickers
																				// in
																				// llSecond
				// reflection - picker.setDividerDrawable(divider); << didn't
				// seem to work.
				Field[] pickerFields = NumberPicker.class.getDeclaredFields();
				for (Field pf : pickerFields) {
					if (pf.getName().equals("mSelectionDivider")) {
						pf.setAccessible(true);
						try {
							pf.set(picker,
									getResources().getDrawable(
											R.drawable.selection_divider_red));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (NotFoundException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}
		// Container:
		LinearLayout llTitleBar = new LinearLayout(getActivity());
		llTitleBar.setOrientation(LinearLayout.VERTICAL);
		llTitleBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		// TextView Title:
		TextView tvTitle = new TextView(getActivity());
		tvTitle.setText("Select a time");
		tvTitle.setGravity(Gravity.CENTER);
		tvTitle.setPadding(10, 20, 10, 20);
		tvTitle.setTextSize(24);
		tvTitle.setTextColor(Color.BLACK);
		tvTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		llTitleBar.addView(tvTitle);

		// View line:
		View vTitleDivider = new View(getActivity());
		final float scale = getActivity().getResources().getDisplayMetrics().density;
		int pixels = (int) (2 * scale + 0.5f);
		vTitleDivider.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, pixels));
		vTitleDivider.setBackgroundColor(getResources().getColor(R.color.red));
		llTitleBar.addView(vTitleDivider);

		tpView.addView(llTitleBar);
		FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) llFirst
				.getLayoutParams();
		lp.setMargins(0, 90, 0, 0);
		return newFragment;
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
	}

	public class CustomTimePickerDialog extends TimePickerDialog {

		private TimePicker mTimePicker;

		public CustomTimePickerDialog(Context context,
				OnTimeSetListener callBack, int hourOfDay, int minute,
				boolean is24HourView) {
			super(context, callBack, hourOfDay, minute, is24HourView);
			mTimePicker = new TimePicker(getActivity());
			setView(mTimePicker);
		}

		public TimePicker getTimePicker() {
			return mTimePicker;
		}

	}
}
