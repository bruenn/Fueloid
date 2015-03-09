/**
		Copyright (C) 2011 Patrick Brünn.

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

package biz.bruenn.fueloid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.technologichron.android.control.NumberPicker;

public class DistancePickerDialog extends Dialog {
	
	public interface OnDistanceSetListener {
		public void onDistanceSet(int value);
	}
	
	private static final int MAX_DECIMALS = 4;
	private int mInitialValue;
	private int mPickerIds[] = new int[] {R.id.pickD1, R.id.pickD2, R.id.pickD3, R.id.pickD4};
	private NumberPicker mPickers[];
	
	protected OnDistanceSetListener mOnDistanceSetListener;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.ok:
				mOnDistanceSetListener.onDistanceSet(getValue());
				DistancePickerDialog.this.dismiss();
				break;
			case R.id.cancel:
				DistancePickerDialog.this.dismiss();
				break;
			}
		}
	};

	public DistancePickerDialog(Context context, OnDistanceSetListener listener, int value) {
		super(context);
		
		mOnDistanceSetListener = listener;
		mInitialValue = value;
		mPickers = new NumberPicker[MAX_DECIMALS];
	}
	
	protected int getValue() {
		int value = 0;		
		for(int i = mPickers.length - 1; i > 0; i--) {
			value += mPickers[i].getValue();
			value *= 10;
		}
		return value + mPickers[0].getValue();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.distance_picker_dialog);
		Button btnOK = (Button)findViewById(R.id.ok);
		if(null != btnOK) {
			btnOK.setOnClickListener(mOnClickListener);
		}
		
		Button btnCancel = (Button)findViewById(R.id.cancel);
		if(null != btnCancel) {
			btnCancel.setOnClickListener(mOnClickListener);
		}
		
		for(int i = 0; i < mPickers.length; i++) {
			mPickers[i] = (NumberPicker)findViewById(mPickerIds[i]);
			if(i == mPickers.length - 1) {
				mPickers[i].setMaximum(999);
			}
		}
		setValue(mInitialValue);
	}
	
	private void setValue(int value) {
		for(int i = 0; i < mPickers.length - 1; i++) {
			mPickers[i].setValue(value % 10);
			value = value / 10;
		}
		mPickers[mPickers.length - 1].setValue(value);		
	}
	
	public void onDistanceChanged(int value, int min, int max) {
		setValue(value);		
	}
}
