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

import net.technologichron.android.control.NumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FloatPickerDialog extends Dialog {
	
	public interface OnFloatSetListener {
		public void onFloatSet(float value);
	}
	
	private Button mOK;
	private Button mCancel;
	private OnFloatSetListener mOnFloatSetListener;
	
	private final int mNumPlaces = 4;
	// stores the initial values f.e. 12.345 would be stored as {12, 3, 4, 5} 
	private int mInitialValues[] = new int[mNumPlaces];
	private int mPickerIds[] = new int[] {R.id.pickFull, R.id.pickTenth, R.id.pickHundredth, R.id.pickThousandth};
	private NumberPicker mPickers[];
	
	static int ConvertAsciiCharToInt(char c) throws NumberFormatException {
		if(('0' <= c) && (c <= '9')) {
			return c - '0';
		}
		throw new NumberFormatException("Invalid character");
	}
	
	public FloatPickerDialog(Context context, OnFloatSetListener listener, float value) {
		super(context);
		// initialize members
		mOnFloatSetListener = listener;
		
		//parse initial values for decimal place, first split 
		String s[] = String.valueOf(value).split("\\.");
		if(2 == s.length) {
			try {
				mInitialValues[0] = Integer.parseInt(s[0]);
				for(int i = 1; (i < mNumPlaces) && (i <= s[1].length()); i++) {
					mInitialValues[i] = ConvertAsciiCharToInt(s[1].charAt(i-1));
				}
			} catch (NumberFormatException e) {
				for(int i = 0; i < mNumPlaces; i++) {
					mInitialValues[i] = 0;
				}				
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.float_picker_dialog);
		mOK = (Button)findViewById(R.id.ok);
		mOK.setOnClickListener(mOnClickListener);
		
		mCancel = (Button)findViewById(R.id.cancel);
		mCancel.setOnClickListener(mOnClickListener);
		
		
		mPickers = new NumberPicker[mNumPlaces];
		mPickers[0] = (NumberPicker)findViewById(mPickerIds[0]);
		mPickers[0].setMaximum(99);
		mPickers[0].setValue(mInitialValues[0]);
		
		initDecimalPickers();
		
		updateTitle();
	}
	
	private float getFloat() {
		try {
			return Float.parseFloat(getString());			
		} catch (NumberFormatException e) {
			return 0f;
		}
	}
	
	private String getString() {
		String newTitle = mPickers[0].getValue() + ".";
		for(int i = 1; i < mNumPlaces; i++) {
			newTitle += mPickers[i].getValue();
		}
		return newTitle;
	}
	
	private void initDecimalPickers() {
		for(int i = 1; i < mNumPlaces; i++) {
			mPickers[i] = (NumberPicker)findViewById(mPickerIds[i]);
			mPickers[i].setValue(mInitialValues[i]);
		}
	}
	
	private void updateTitle() {
		setTitle(getString());
	}
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.ok:
				mOnFloatSetListener.onFloatSet(getFloat());
				FloatPickerDialog.this.dismiss();
				break;
			case R.id.cancel:
				FloatPickerDialog.this.dismiss();
				break;
			}
		}
	};
}
