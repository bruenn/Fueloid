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

import biz.bruenn.fueloid.data.FillUp;
import biz.bruenn.fueloid.data.FueloidDBProxy;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditFillUp extends Activity {
	protected static final int DATE_PICKER_DIALOG = 0;
	protected static final int DISTANCE_PICKER_DIALOG = 1;
	protected static final int LITER_PICKER_DIALOG = 2;
	protected static final int MONEY_PICKER_DIALOG = 3;
	protected static final int TIME_PICKER_DIALOG = 4;
	private FueloidDBProxy mDBProxy;
	private FillUp mFillUp;
	private TextView mDate;
	private TextView mDistance;
	private TextView mLiter;
	private TextView mMoney;
	private TextView mTime;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBProxy = new FueloidDBProxy(this);
        
        Intent i = getIntent();
        
        mFillUp = FillUp.getFillUp(mDBProxy.mOpenHelper, i.getLongExtra(FillUp.TABLE_NAME, 0));
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
    		return new DatePickerDialog(this, mDateSetListener, mFillUp.getDateYear(), mFillUp.getDateMonth()-1, mFillUp.getDateDay());
    	case DISTANCE_PICKER_DIALOG:
    		return new IntPickerDialog(this, mDistanceSetListener, mFillUp.getmDistance());
    	case LITER_PICKER_DIALOG:
    		return new FloatPickerDialog(this, mLiterSetListener, mFillUp.getmLiter());
    	case MONEY_PICKER_DIALOG:
       		return new FloatPickerDialog(this, mMoneySetListener, mFillUp.getmMoney());
    	case TIME_PICKER_DIALOG:
    		return new TimePickerDialog(this, mTimeSetListener, mFillUp.getDateHours(), mFillUp.getDateMinutes(), true);
    	}
    	return null;
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mDBProxy.updateFillUp(mFillUp);
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
    	super.onPrepareDialog(id, dialog, args);
    	
    	if(null != dialog) {
    		switch(id) {
	    	case DATE_PICKER_DIALOG:
	    		((DatePickerDialog)dialog).onDateChanged(null, mFillUp.getDateYear(), mFillUp.getDateMonth(), mFillUp.getDateDay());
	    		break;
	    	case DISTANCE_PICKER_DIALOG:
	    		((IntPickerDialog)dialog).update(mFillUp.getmDistance(), mFillUp.getPreviousDistance(), mFillUp.getNextDistance());
	    		break;
	    	}
    	}
    }    
    
    @Override
    public void onResume() {
    	super.onResume();
    	mDateSetListener.onDateSet(null, mFillUp.getDateYear(), mFillUp.getDateMonth(), mFillUp.getDateDay());
    	mDistanceSetListener.onIntSet(mFillUp.getmDistance());
    	mTimeSetListener.onTimeSet(null, mFillUp.getDateHours(), mFillUp.getDateMinutes());
    	mLiterSetListener.onFloatSet(mFillUp.getmLiter());
    	mMoneySetListener.onFloatSet(mFillUp.getmMoney());
    }
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.fillupDate:
				showDialog(DATE_PICKER_DIALOG);
				break;
			case R.id.fillupDistance:
				showDialog(DISTANCE_PICKER_DIALOG);
				break;
			case R.id.fillupLiter:
				showDialog(LITER_PICKER_DIALOG);
				break;
			case R.id.fillupMoney:
				showDialog(MONEY_PICKER_DIALOG);
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
			mFillUp.setDate(year - 1900, monthOfYear, dayOfMonth);
			mDate.setText(mFillUp.getDateYear() + "-" + mFillUp.getDateMonth() + "-" + mFillUp.getDateDay());
		}
	};
	
	private IntPickerDialog.OnIntSetListener mDistanceSetListener = new IntPickerDialog.OnIntSetListener() {
		
		@Override
		public void onIntSet(int value) {
			mFillUp.setmDistance(value);
			mDistance.setText(value + " km");
		}
	};
	
	private FloatPickerDialog.OnFloatSetListener mLiterSetListener = new FloatPickerDialog.OnFloatSetListener() {
		
		@Override
		public void onFloatSet(float value) {
			mFillUp.setmLiter(value);
			mLiter.setText(value + " l");
		}
	};
	
	private FloatPickerDialog.OnFloatSetListener mMoneySetListener = new FloatPickerDialog.OnFloatSetListener() {
		
		@Override
		public void onFloatSet(float value) {
			mFillUp.setmMoney(value);
			mMoney.setText(value + " €");
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
