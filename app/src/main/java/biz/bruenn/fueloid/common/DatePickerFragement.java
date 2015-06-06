package biz.bruenn.fueloid.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragement extends DialogFragment {

	private DatePickerDialog.OnDateSetListener mListener = null;
	private Calendar mCalendar = Calendar.getInstance();

	public void setDate(final Calendar calendar) {
		mCalendar = calendar;
	}

	public void setListener(DatePickerDialog.OnDateSetListener listener) {
		mListener = listener;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		final int year = mCalendar.get(Calendar.YEAR);
		final int month = mCalendar.get(Calendar.MONTH);
		final int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), mListener, year, month, day);
	}
}
