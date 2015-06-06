package biz.bruenn.fueloid;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SetValueDialog extends DialogFragment {

	public interface OnValueChangedListener {
		void valueChanged(String newVal);
	}

	private OnValueChangedListener mListener = null;

	public SetValueDialog() {
		//empty
	}

	public void setListener(OnValueChangedListener listener) {
		mListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
		View v = inflater.inflate(R.layout.dialog_set_value, container);

		final EditText value = (EditText)v.findViewById(R.id.value);
		Button accept = (Button)v.findViewById(R.id.action_accept);
		accept.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				if (null != mListener) {
					mListener.valueChanged(value.getText().toString());
				}
				dismiss();
			}
		});

		Bundle args = getArguments();
		getDialog().setTitle(args.getString("TITLE", "unknown"));
		value.setText(args.getString("VALUE", ""));
		value.setInputType(InputType.TYPE_CLASS_NUMBER | args.getInt("INPUT_TYPE", 0));
		return v;
	}
}
