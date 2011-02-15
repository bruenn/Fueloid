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

import biz.bruenn.fueloid.FloatPickerDialog.OnFloatSetListener;
import biz.bruenn.fueloid.data.FillUp;
import biz.bruenn.fueloid.data.FueloidDBProxy;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
	protected static final int FLOAT_PICKER_DIALOG = 2;
	private FueloidDBProxy mDBProxy;
	private FillUp mFillUp;
	private TextView mDate;
	private TextView mError;
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
        
        mDistance = (EditText)findViewById(R.id.fillupDistance);
        if(null != mDistance) {
        	mDistance.addTextChangedListener(mDistanceChangedListener);
        }
        mDistanceSeeker = (SeekBar)findViewById(R.id.fillupDistanceSeeker);
        if(null != mDistanceSeeker) {
        	mDistanceSeeker.setOnSeekBarChangeListener(mSeekBarChangeListener);
        }
        
        mDistanceDec = (Button)findViewById(R.id.fillupDistanceDec);
        if(null != mDistanceDec) {
        	mDistanceDec.setOnClickListener(mOnClickListener);
        }
        
        mDistanceInc = (Button)findViewById(R.id.fillupDistanceInc);
        if(null != mDistanceInc) {
        	mDistanceInc.setOnClickListener(mOnClickListener);
        }
        
        mError = (TextView)findViewById(R.id.error);
        
        mLiter = (EditText)findViewById(R.id.fillupLiter);
        if(null != mLiter) {
        	mLiter.addTextChangedListener(mLiterChangedListener);
        	mLiter.setSelectAllOnFocus(true);
        }
        
        mMoney = (EditText)findViewById(R.id.fillupMoney);
        if(null != mMoney) {
        	mMoney.addTextChangedListener(mMoneyChangedListener);
        	mMoney.setSelectAllOnFocus(true);
        	mMoney.setOnClickListener(mOnClickListener);
        }
        
        mTime = (TextView)findViewById(R.id.fillupTime);
        if(null != mTime) {
        	mTime.setOnClickListener(mOnClickListener);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case DATE_PICKER_DIALOG:
    		return new DatePickerDialog(this, mDateSetListener, mFillUp.getDateYear(), mFillUp.getDateMonth()-1, mFillUp.getDateDay());
    	case TIME_PICKER_DIALOG:
    		return new TimePickerDialog(this, mTimeSetListener, mFillUp.getDateHours(), mFillUp.getDateMinutes(), true);
    	case FLOAT_PICKER_DIALOG:
    		return new FloatPickerDialog(this, mFillUp.getmMoney(), mMoneySetListener);
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
    	if(null != mFillUp) {
	    	mDate.setText(mFillUp.getDateYear() + "-" + mFillUp.getDateMonth() + "-" + mFillUp.getDateDay());
	    	mDistance.setText(String.valueOf(mFillUp.getmDistance()));
	    	mLiter.setText(String.valueOf(mFillUp.getmLiter()));
	    	mMoney.setText(String.valueOf(mFillUp.getmMoney()));
	    	mTime.setText(mFillUp.getDateHours() + ":" + mFillUp.getDateMinutes());
	    	
	    	updateDistance();
    	}
    }

	private void updateDistance() {
		int previousDistance = mFillUp.getPreviousDistance();
		this.mDistanceSeeker.setMax(mFillUp.getNextDistance() - previousDistance);
		if(mFillUp.getmDistance() < previousDistance) {
			mFillUp.setmDistance(previousDistance);
		}
		this.mDistanceSeeker.setProgress(mFillUp.getmDistance() - previousDistance);
		
		mTime.setText(":" + mFillUp.getPreviousDistance() + "-" + mFillUp.getNextDistance());
	}
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.fillupDate:
				showDialog(DATE_PICKER_DIALOG);
				break;
			case R.id.fillupTime:
				showDialog(TIME_PICKER_DIALOG);
				break;
			case R.id.fillupDistanceDec:

				showDialog(FLOAT_PICKER_DIALOG);
//				final int oldDistance = mFillUp.getmDistance();
//				if(oldDistance > 0) {
//					mFillUp.setmDistance(oldDistance - 1);
//				}
				break;
			case R.id.fillupDistanceInc:
				mFillUp.setmDistance(mFillUp.getmDistance() + 1);
				break;
			case R.id.fillupMoney:
				showDialog(FLOAT_PICKER_DIALOG);
				break;
			}
			updateText();
		}
	};

	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		
		private int mOldDistance;
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			updateDistance();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			updateDistance();
			mOldDistance = mFillUp.getmDistance() - seekBar.getProgress();
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser) {
				mFillUp.setmDistance(mOldDistance+progress);
				mDistance.setText(String.valueOf(mFillUp.getmDistance()));
				//updateDistance();
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
	
	private TextWatcher mDistanceChangedListener = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			if(s.length() > 0) {
				mFillUp.setmDistance(Integer.parseInt(s.toString()));
			} else {
				mFillUp.setmDistance(0);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// we don't need this
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// we don't need this
		}
		
	};
	
	private TextWatcher mLiterChangedListener = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			if(s.length() > 0) {
				mFillUp.setmLiter(Float.parseFloat(s.toString()));
			} else {
				mFillUp.setmLiter(0f);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// we don't need this			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// we don't need this	
		}
	};
	
	private TextWatcher mMoneyChangedListener = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			if(s.length() > 0) {
				mFillUp.setmMoney(Float.parseFloat(s.toString()));
			} else {
				mFillUp.setmMoney(0f);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// we don't need this			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// we don't need this	
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mFillUp.setTime(hourOfDay, minute);
			updateText();			
		}
	};
	
	private FloatPickerDialog.OnFloatSetListener mMoneySetListener = new FloatPickerDialog.OnFloatSetListener() {
		
		@Override
		public void onFloatSet(float value) {
			mFillUp.setmMoney(value);
			updateText();
		}
	};
}
