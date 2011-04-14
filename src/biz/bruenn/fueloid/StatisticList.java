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

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import biz.bruenn.fueloid.data.Statistic;
import biz.bruenn.fueloid.data.Vehicle;

public class StatisticList extends ListActivity {
	StatisticAdapter mStatisticAdapter;
	Vehicle mVehicle = new Vehicle(this); //TODO
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_list);
        Statistic[] statistics = Statistic.getStatistics(mVehicle);
        mStatisticAdapter = new StatisticAdapter(this, R.layout.statistic_list_item, statistics);
	    
	    this.setListAdapter(mStatisticAdapter);
	}

	private class StatisticAdapter extends ArrayAdapter<Statistic> {

		public StatisticAdapter(Context context, int textViewResourceId, Statistic[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(getContext()).inflate(R.layout.statistic_list_item, parent, false);
			
			Statistic item = getItem(position);
			TextView title = (TextView) view.findViewById(R.id.title);
			if(null != title) {
				title.setText(item.getDistance() + "km");
			}
			TextView text  = (TextView) view.findViewById(R.id.text);
			if(null != text) {
				text.setText(item.getMoney() + "€");
			}
			return view;
		}
    }
}