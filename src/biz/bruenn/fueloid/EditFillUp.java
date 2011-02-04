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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditFillUp extends Activity {
	protected static final int DATE_PICKER_DIALOG = 0;
	protected static final int TIME_PICKER_DIALOG = 1;
	private FueloidDBProxy mDBProxy;
	private FillUp mFillUp;
	private TextView mDate;
	private TextView mTime;
	private EditText mDistance;
	private EditText mLiter;
	private EditText mMoney;
	private Button mDistanceDec;
	private Button mDistanceInc;
	private SeekBar mDistanceSeeker;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBProxy = new FueloidDBProxy(this);
        
        Intent i = getIntent();
        
        mFillUp = mDBProxy.getFillUp(i.getLongExtra(FillUp.TABLE_NAME, 0));
        setContentView(R.layout.edit_fillup);
        
        mDate = (TextView)findViewById(R.id.fillupDate);
        if(null != mDate) {
        	mDate.setOnClickListener(mDateClickListener);	
        }
        
        mDistance = (EditText)findViewById(R.id.fillupDistance);
        if(null != mDistance) {
        	mDistance.setOnFocusChangeListener(mTextChangedListener);
        }
        
        mDistanceSeeker = (SeekBar)findViewById(R.id.fillupDistanceSeeker);
        mDistanceSeeker.setOnSeekBarChangeListener(mSeekBarChangeListener);
        
        mDistanceDec = (Button)findViewById(R.id.fillupDistanceDec);
        mDistanceDec.setOnClickListener(mOnClickListener);
        
        mDistanceInc = (Button)findViewById(R.id.fillupDistanceInc);
        mDistanceInc.setOnClickListener(mOnClickListener);
        
        mLiter = (EditText)findViewById(R.id.fillupLiter);
        if(null != mLiter) {
        	mLiter.setOnFocusChangeListener(mTextChangedListener);
        }
        
        mMoney = (EditText)findViewById(R.id.fillupMoney);
        if(null != mMoney) {
        	mMoney.setOnFocusChangeListener(mTextChangedListener);
        }
        
        mTime = (TextView)findViewById(R.id.fillupTime);
        if(null != mTime) {
        	mTime.setOnClickListener(mTimeClickListener);
        }
        
        
        if(null == mFillUp) {
        	mDate.setText("object not found!");
        } else {
        	mDate.setOnClickListener(mDateClickListener);
        	
        	mDate.setText(mFillUp.getmId() + " - " + mFillUp.getmDate()+ ": "
        			+ mFillUp.getmDistance() + "km, "
        			+ mFillUp.getmMoney() + "€, "
        			+ mFillUp.getmLiter() + "l");
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case DATE_PICKER_DIALOG:
    		return new DatePickerDialog(this, mDateSetListener, mFillUp.getDateYear(), mFillUp.getDateMonth()-1, mFillUp.getDateDay());
    	case TIME_PICKER_DIALOG:
    		return new TimePickerDialog(this, mTimeSetListener, mFillUp.getDateHours(), mFillUp.getDateMinutes(), true);
    	}
    	return null;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	mDBProxy.updateFillUp(mFillUp);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	updateText();
    }
    
    private void updateText() {
    	mDate.setText(mFillUp.getDateYear() + "-" + mFillUp.getDateMonth() + "-" + mFillUp.getDateDay());
    	mDistance.setText(String.valueOf(mFillUp.getmDistance()));
    	mLiter.setText(String.valueOf(mFillUp.getmLiter()));
    	mMoney.setText(String.valueOf(mFillUp.getmMoney()));
    	mTime.setText(mFillUp.getDateHours() + ":" + mFillUp.getDateMinutes());
    }
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.fillupDistanceDec:
				final int oldDistance = mFillUp.getmDistance();
				if(oldDistance > 0) {
					mFillUp.setmDistance(oldDistance - 1);
				}
				break;
			case R.id.fillupDistanceInc:
				mFillUp.setmDistance(mFillUp.getmDistance() + 1);
				break;
			}
			updateText();
		}
	};
    
    private View.OnClickListener mDateClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(DATE_PICKER_DIALOG);
		}
	};
	
	private View.OnClickListener mTimeClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(TIME_PICKER_DIALOG);
		}
	};
	
	private View.OnFocusChangeListener mTextChangedListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus) {
				//we do nothing, when getting in focus
			} else {
				//we are leaving focus
				switch(v.getId()) {
				case R.id.fillupDistance:
					mFillUp.setmDistance(Integer.parseInt(mDistance.getText().toString()));
					break;
				case R.id.fillupLiter:
					mFillUp.setmLiter(Float.parseFloat(mLiter.getText().toString()));
					break;
				case R.id.fillupMoney:
					mFillUp.setmMoney(Float.parseFloat(mMoney.getText().toString()));
					break;
				}
			}
		}
	};

	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		
		private int mOldDistance;
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mOldDistance = mFillUp.getmDistance() - seekBar.getProgress();
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser) {
				mFillUp.setmDistance(mOldDistance+progress);
				updateText();
			}
		}
	};
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mFillUp.setDate(year - 1900, monthOfYear, dayOfMonth);
			updateText();
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mFillUp.setTime(hourOfDay, minute);
			updateText();			
		}
	};
}
