/**
 Copyright (C) 2011-2015 Patrick Brünn.

 This file is part of Fueloid.

 Fueloid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Fueloid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Fueloid.  If not, see <http://www.gnu.org/licenses/>. */

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

	public void setArguments(Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
		mCalendar = calendar;
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
