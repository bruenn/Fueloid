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

import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.NumberStatistic;
import biz.bruenn.fueloid.data.Statistic;
import biz.bruenn.fueloid.data.Vehicle;

class VehicleAdapter extends CursorAdapter {

	public VehicleAdapter(Context context, Cursor c) {
		super(context, c, true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View view = LayoutInflater.from(context).inflate(R.layout.icon_list_item, viewGroup, false);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		icon.setImageDrawable(context.getResources().getDrawable(R.drawable.distance));
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Vehicle vehicle = Vehicle.get(new FueloidDatabaseHelper(context), cursor);
		Statistic stats = new NumberStatistic(vehicle, vehicle.countFillups() - 1);

		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(vehicle.getName());

		TextView text = (TextView) view.findViewById(R.id.text);
		text.setText(stats.getText());
	}
}
