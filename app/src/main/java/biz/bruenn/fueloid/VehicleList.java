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

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.Vehicle;

public class VehicleList extends ListActivity {

	FueloidDatabaseHelper mDBHelper;
	VehicleAdapter mVehicleAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehicle_list);

		mDBHelper = new FueloidDatabaseHelper(this);

		mVehicleAdapter = new VehicleAdapter(this, mDBHelper.queryVehicles());
		setListAdapter(mVehicleAdapter);
		getListView().setOnItemClickListener(new VehicleListOnItemClickListener());
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_vehicle, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add:
				Vehicle.create(mDBHelper, getString(R.string.new_vehicle));
				updateView();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_fillup, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.delete:
				Vehicle v = Vehicle.get(mDBHelper, info.id);
				if(null != v) {
					v.delete();
				}
				updateView();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView();
	}

	private void updateView() {
		this.getResources();
		mVehicleAdapter.changeCursor(mDBHelper.queryVehicles());
	}

	private class VehicleListOnItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Cursor c = ((VehicleAdapter)parent.getAdapter()).getCursor();
			Intent i = new Intent(view.getContext(), FillUpList.class);
			i.putExtra(Vehicle.TABLE_NAME, c.getLong(c.getColumnIndex(Vehicle._ID)));
			startActivityForResult(i, 0);
		}
	}
}
