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

import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.FillUp;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditFillUp extends Activity {
	protected static final int DATE_PICKER_DIALOG = 0;
	protected static final int TIME_PICKER_DIALOG = 4;
	private FueloidDatabaseHelper mDBHelper;
	private FillUp mFillUp;
	private TextView mDate;
	private TextView mDistance;
	private TextView mLiter;
	private TextView mMoney;
	private TextView mTime;

	protected SetValueDialog.OnValueChangedListener mDistanceListener = new SetValueDialog.OnValueChangedListener() {
		@Override
		public void valueChanged(String newVal) {
			mFillUp.setmDistance(Integer.valueOf(newVal));
			mDistance.setText(newVal + " km");
		}
	};

	protected SetValueDialog.OnValueChangedListener mLiterListener = new SetValueDialog.OnValueChangedListener() {
		@Override
		public void valueChanged(String newVal) {
			mFillUp.setmLiter(Float.valueOf(newVal));
			mLiter.setText(newVal + " l");
		}
	};

	protected SetValueDialog.OnValueChangedListener mMoneyListener = new SetValueDialog.OnValueChangedListener() {
		@Override
		public void valueChanged(String newVal) {
			mFillUp.setmMoney(Float.valueOf(newVal));
			mMoney.setText(newVal + " €");
		}
	};
	
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
        if(null != mDate) {
        	mDate.setOnClickListener(mOnClickListener);	
        }
        
        mDistance = (TextView)findViewById(R.id.fillupDistance);
        if(null != mDistance) {
        	mDistance.setOnClickListener(mOnClickListener);
        }
        
        mLiter = (TextView)findViewById(R.id.fillupLiter);
        if(null != mLiter) {
        	mLiter.setOnClickListener(mOnClickListener);
        }
        
        mMoney = (TextView)findViewById(R.id.fillupMoney);
        if(null != mMoney) {
        	mMoney.setOnClickListener(mOnClickListener);
        }
        
        mTime = (TextView)findViewById(R.id.fillupTime);
        if(null != mTime) {
        	mTime.setOnClickListener(mOnClickListener);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
    	switch(id) {
    	case DATE_PICKER_DIALOG:
    		return new DatePickerDialog(this, mDateSetListener, mFillUp.getDateYear(), mFillUp.getDateMonth(), mFillUp.getDateDay());
    	case TIME_PICKER_DIALOG:
    		return new TimePickerDialog(this, mTimeSetListener, mFillUp.getDateHours(), mFillUp.getDateMinutes(), true);
    	}
    	return null;
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mFillUp.update();
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
    	super.onPrepareDialog(id, dialog, args);
    	
    	if(null != dialog) {
    		switch(id) {
	    	case DATE_PICKER_DIALOG:
	    		((DatePickerDialog)dialog).onDateChanged(null, mFillUp.getDateYear(), mFillUp.getDateMonth(), mFillUp.getDateDay());
	    		break;
	    	}
    	}
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

	private void showDialog(String title, String value, int input, SetValueDialog.OnValueChangedListener listener) {
		SetValueDialog d = new SetValueDialog();
		d.setListener(listener);
		Bundle args = new Bundle();
		args.putString("TITLE", title);
		args.putString("VALUE", value);
		args.putInt("INPUT_TYPE", input);
		d.setArguments(args);
		d.show(getFragmentManager(), title);
	}
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.fillupDate:
				showDialog(DATE_PICKER_DIALOG);
				break;
			case R.id.fillupDistance:
				showDialog("Distance", String.valueOf(mFillUp.getmDistance()), 0, mDistanceListener);
				break;
			case R.id.fillupLiter:
				showDialog("Liter", String.valueOf(mFillUp.getmLiter()), InputType.TYPE_NUMBER_FLAG_DECIMAL, mLiterListener);
				break;
			case R.id.fillupMoney:
				showDialog("Money", String.valueOf(mFillUp.getmMoney()), InputType.TYPE_NUMBER_FLAG_DECIMAL, mMoneyListener);
				break;
			case R.id.fillupTime:
				showDialog(TIME_PICKER_DIALOG);
				break;
			}
		}
	};
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mFillUp.setDate(year, monthOfYear, dayOfMonth);
			mDate.setText(mFillUp.getDateYear() + "-" + (mFillUp.getDateMonth()+1) + "-" + mFillUp.getDateDay());
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mFillUp.setTime(hourOfDay, minute);
			
			if(minute < 10) {
				mTime.setText(hourOfDay + ":0" + minute);
			} else {
				mTime.setText(hourOfDay + ":" + minute);
			}
		}
	};
}
