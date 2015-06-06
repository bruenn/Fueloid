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

import biz.bruenn.fueloid.common.DatePickerFragement;
import biz.bruenn.fueloid.common.TimePickerFragment;
import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.FillUp;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditFillUp extends Activity {
	private FueloidDatabaseHelper mDBHelper;
	private FillUp mFillUp;
	private TextView mDate;
	private TextView mDistance;
	private TextView mLiter;
	private TextView mMoney;
	private TextView mTime;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBHelper = new FueloidDatabaseHelper(this);
        
        Intent i = getIntent();
        
        mFillUp = FillUp.getFillUp(mDBHelper, i.getLongExtra(FillUp.TABLE_NAME, 0));
        if(null == mFillUp) {
        	TextView tv = new TextView(this);
        	tv.setText("Error when reading database");
        	setContentView(tv);
        	return;
        }
        setContentView(R.layout.edit_fillup);
        
        mDate = (TextView)findViewById(R.id.fillupDate);
        mDate.setOnClickListener(mOnClickListener);
        
        mDistance = (TextView)findViewById(R.id.fillupDistance);
        mDistance.setOnClickListener(mOnClickListener);
        
        mLiter = (TextView)findViewById(R.id.fillupLiter);
        mLiter.setOnClickListener(mOnClickListener);
        
        mMoney = (TextView)findViewById(R.id.fillupMoney);
        mMoney.setOnClickListener(mOnClickListener);
        
        mTime = (TextView)findViewById(R.id.fillupTime);
        mTime.setOnClickListener(mOnClickListener);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mFillUp.update();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	//use listener callbacks to refresh view
    	mDateSetListener.onDateSet(null, mFillUp.getDateYear(), mFillUp.getDateMonth(), mFillUp.getDateDay());
    	mTimeSetListener.onTimeSet(null, mFillUp.getDateHours(), mFillUp.getDateMinutes());
		mDistanceListener.valueChanged(String.valueOf(mFillUp.getmDistance()));
		mLiterListener.valueChanged(String.valueOf(mFillUp.getmLiter()));
		mMoneyListener.valueChanged(String.valueOf(mFillUp.getmMoney()));
    }

	private <T> void showDialog(String title, T value, SetValueDialog.OnValueChangedListener listener, int input) {
		SetValueDialog d = new SetValueDialog();
		d.setListener(listener);
		Bundle args = new Bundle();
		args.putString("TITLE", title);
		args.putString("VALUE", String.valueOf(value));
		args.putInt("INPUT_TYPE", input);
		d.setArguments(args);
		d.show(getFragmentManager(), title);
	}

	private <T> void showDialog(String title, T value, SetValueDialog.OnValueChangedListener listener) {
		showDialog(title, value, listener, InputType.TYPE_NUMBER_FLAG_DECIMAL);
	}
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.fillupDate:
				DatePickerFragement datePicker = new DatePickerFragement();
				datePicker.setArguments(mFillUp.getDate(), mDateSetListener);
				datePicker.show(getFragmentManager(), "datePicker");
				break;
			case R.id.fillupDistance:
				showDialog("Distance", mFillUp.getmDistance(), mDistanceListener, 0);
				break;
			case R.id.fillupLiter:
				showDialog("Liter", mFillUp.getmLiter(), mLiterListener);
				break;
			case R.id.fillupMoney:
				showDialog("Money", mFillUp.getmMoney(), mMoneyListener);
				break;
			case R.id.fillupTime:
				TimePickerFragment timePicker = new TimePickerFragment();
				timePicker.setArguments(mFillUp.getDate(), mTimeSetListener);
				timePicker.show(getFragmentManager(), "timePicker");
				break;
			}
		}
	};
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mFillUp.setDate(year, monthOfYear, dayOfMonth);
			mDate.setText(String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
		}
	};

	private SetValueDialog.OnValueChangedListener mDistanceListener = new SetValueDialog.OnValueChangedListener() {
		@Override
		public void valueChanged(String newVal) {
			mFillUp.setmDistance(Integer.valueOf(newVal));
			mDistance.setText(newVal + " km");
		}
	};

	private SetValueDialog.OnValueChangedListener mLiterListener = new SetValueDialog.OnValueChangedListener() {
		@Override
		public void valueChanged(String newVal) {
			mFillUp.setmLiter(Float.valueOf(newVal));
			mLiter.setText(newVal + " l");
		}
	};

	private SetValueDialog.OnValueChangedListener mMoneyListener = new SetValueDialog.OnValueChangedListener() {
		@Override
		public void valueChanged(String newVal) {
			mFillUp.setmMoney(Float.valueOf(newVal));
			mMoney.setText(newVal + " €");
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mFillUp.setTime(hourOfDay, minute);
			mTime.setText(String.format("%02d:%02d", hourOfDay, minute));
		}
	};
}
