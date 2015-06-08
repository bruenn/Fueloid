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
import android.os.Bundle;

import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.Statistic;
import biz.bruenn.fueloid.data.Vehicle;

public class StatisticList extends ListActivity {
	StatisticAdapter mStatisticAdapter;
	Vehicle mVehicle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_list);
        mVehicle = Vehicle.get(new FueloidDatabaseHelper(this), getIntent().getLongExtra(Vehicle.TABLE_NAME, -1));
        Statistic[] statistics = Statistic.getStatistics(mVehicle);
        mStatisticAdapter = new StatisticAdapter(this, R.layout.icon_list_item, statistics);
	    
	    this.setListAdapter(mStatisticAdapter);
	}

}