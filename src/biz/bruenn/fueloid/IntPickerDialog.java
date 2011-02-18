package biz.bruenn.fueloid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class IntPickerDialog extends Dialog {
	
	public interface OnIntSetListener {
		public void onIntSet(int value);
	}

	private int mValue;
	private int mMin;
	private int mMax;
	private SeekBar mSeeker;
	private Handler mDragHandler = new Handler();
	public boolean mIsDragging = false;
	private OnIntSetListener mListener;
	
	private class DragRepeater implements Runnable {

		private static final long mDelayMillis = 50;

		@Override
		public void run() {
			if(mIsDragging ) {
				doDragEvent();
				mDragHandler.postDelayed(new DragRepeater(), mDelayMillis);
			}
		}
		
	}

	public IntPickerDialog(Context context, OnIntSetListener listener, int value) {
		super(context);
		
		mListener = listener;
		mValue = value;
	}
	
	private void doDragEvent() {
		int diff = mSeeker.getProgress() - (mSeeker.getMax() / 2);
		
		//if diff > 10% 
		if(Math.abs(diff) > mSeeker.getMax() / 20) {
			incValueBy(diff/10);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.int_picker_dialog);		
		initOkCancel();
		initSeekBar();
	}

	private void incValueBy(int value) {
		mValue += value;
		if(mValue < mMin) {
			mValue = mMin;
		} else if(mValue > mMax) {
			mValue = mMax;
		}		
		setTitle(mMin + " < " + mValue + " < " + mMax);
	}
	
	public void update(int value, int min, int max) {
		if((min <= value) && (value  <= max)) {
			mValue = value;
			mMin = min;
			mMax = max;
			mSeeker.setProgress(50);
			mSeeker.setEnabled(true);
		} else {
			//invalid values -> disable seeker;
			mSeeker.setEnabled(false);
			mValue = mMin = mMax = value;
		}
		setTitle(mMin + " < " + mValue + " < " + mMax);
	}

	private void initOkCancel() {
		Button ok = (Button)findViewById(R.id.ok);
		if(null != ok) {
			ok.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mListener.onIntSet(mValue);
					dismiss();
				}
			});
		}
		
		Button cancel = (Button)findViewById(R.id.cancel);
		if(null != cancel) {
			cancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}

	private void initSeekBar() {
		mSeeker = (SeekBar)findViewById(R.id.intSeeker);
        if(null != mSeeker) {
        	mSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					mIsDragging = false;					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					mIsDragging = true;
					mDragHandler.post(new DragRepeater());					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO
				}
			});
        }
	}
}
