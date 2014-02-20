package edu.uark.spARK.fragment;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import edu.uark.spARK.R;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog newFragment = new DatePickerDialog(getActivity(),
				this, year, month, day);
		// Create a new instance of DatePickerDialog and return it
		newFragment.setTitle("");

		// Divider changing:
		DatePicker dpView = newFragment.getDatePicker();
		LinearLayout llFirst = (LinearLayout) dpView.getChildAt(0);
		LinearLayout llSecond = (LinearLayout) llFirst.getChildAt(0);
		for (int i = 0; i < llSecond.getChildCount(); i++) {
			NumberPicker picker = (NumberPicker) llSecond.getChildAt(i); // Numberpickers
																			// in
																			// llSecond
			// reflection - picker.setDividerDrawable(divider); << didn't seem
			// to work.
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
		// Container:
		LinearLayout llTitleBar = new LinearLayout(getActivity());
		llTitleBar.setOrientation(LinearLayout.VERTICAL);
		llTitleBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		// TextView Title:
		TextView tvTitle = new TextView(getActivity());
		tvTitle.setText("Select a date");
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

		dpView.addView(llTitleBar);
		FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) llFirst
				.getLayoutParams();
		lp.setMargins(0, 90, 0, 0);
		return newFragment;
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
	}

}