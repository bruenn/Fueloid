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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import biz.bruenn.fueloid.data.Statistic;

class StatisticAdapter extends ArrayAdapter<Statistic> {

	public StatisticAdapter(Context context, int textViewResourceId, Statistic[] statistics) {
		super(context, textViewResourceId, statistics);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.icon_list_item, parent, false);
		Statistic item = getItem(position);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(item.getTitle());

		TextView text  = (TextView) view.findViewById(R.id.text);
		text.setText(item.getText());
		return view;
	}
}
