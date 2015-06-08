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
