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

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

import biz.bruenn.fueloid.data.FillUp;

class FillUpAdapter extends CursorAdapter {

	public FillUpAdapter(Context context, Cursor c) {
		super(context, c, true);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(DateFormat.getDateInstance().format(cursor.getLong(cursor.getColumnIndex(FillUp.FILLDATE)))
				+ " | " + cursor.getInt(cursor.getColumnIndex(FillUp.DISTANCE)) + "km");

		TextView text  = (TextView) view.findViewById(R.id.text);
		text.setText(cursor.getFloat(cursor.getColumnIndex(FillUp.LITER)) + "l; " + cursor.getFloat(cursor.getColumnIndex(FillUp.MONEY)) + "€");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.icon_list_item, parent, false);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		icon.setImageDrawable(context.getResources().getDrawable(R.drawable.can));
		return view;
	}
}
