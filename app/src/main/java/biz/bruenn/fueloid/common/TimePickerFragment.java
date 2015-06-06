package biz.bruenn.fueloid.common;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
	private TimePickerDialog.OnTimeSetListener mListener = null;
	private Calendar mCalendar = Calendar.getInstance();

	public void setArguments(Calendar calendar, TimePickerDialog.OnTimeSetListener listener) {
		mCalendar = calendar;
		mListener = listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		final int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		final int minute = mCalendar.get(Calendar.MINUTE);
		return new TimePickerDialog(getActivity(), mListener, hour, minute, true);
	}
}
