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
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.FillUp;
import biz.bruenn.fueloid.data.Vehicle;

public class FillUpList extends ListActivity {
	FueloidDatabaseHelper mDBHelper;
	FillUpAdapter mFillUpAdapter;
	Vehicle mVehicle;
	long mId;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDBHelper = new FueloidDatabaseHelper(this);
        mVehicle = Vehicle.get(mDBHelper, getIntent().getLongExtra(Vehicle.TABLE_NAME, -1));
	   
        mFillUpAdapter = new FillUpAdapter(this, mVehicle.getFillUpsCursor());
	    
	    setListAdapter(mFillUpAdapter);
	    getListView().setOnItemClickListener(new FillUpListOnItemClickListener());
	    registerForContextMenu(getListView());
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_fillup, menu);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	getMenuInflater().inflate(R.menu.options_vehicle, menu);
    	return true;
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch (item.getItemId()) {
    	case R.id.delete:
    		FillUp f = FillUp.getFillUp(mDBHelper, info.id);
    		if(null != f) {
    			f.delete();
    		}
    		updateView();
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add:
				return addFillUp();
			case R.id.action_settings:
				return runVehicleActivity(VehicleSettings.class);
			case R.id.statistics:
				return runVehicleActivity(StatisticList.class);
			default:
				return super.onOptionsItemSelected(item);
		}
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    	updateView();
    }
    
    private void updateView() {
		mVehicle = Vehicle.get(mDBHelper, mVehicle.mId);
		mFillUpAdapter.changeCursor(mVehicle.getFillUpsCursor());
		setTitle(mVehicle.getName());
    	TextView mDistance = (TextView)this.findViewById(R.id.vehicleDistance);
    	//TODO use a statistic object here
    	int distance = mVehicle.getDistance(1);
    	float liter = mVehicle.getLiter(1);
    	float money = mVehicle.getMoney(1);
    	mDistance.setText(distance + "km|"
    					+ money + "€|"
    					+ liter + "l|"
    					+ liter/distance + "l/km|"
    					+ money/liter + "€/l");
	}

	private boolean runActivity(java.lang.Class<?> cls, String extra, long id) {
		Intent i = new Intent(this, cls);
		i.putExtra(extra, id);
		startActivityForResult(i, 0);
		return true;
	}

	private boolean runFillUpActivity(long id) {
		return runActivity(EditFillUp.class, FillUp.TABLE_NAME, id);
	}

	private boolean runVehicleActivity(java.lang.Class<?> cls) {
		return runActivity(cls, Vehicle.TABLE_NAME, mVehicle.mId);
	}

	private boolean addFillUp() {
		final FillUp newFillUp = mVehicle.addFillUp();
		if (null != newFillUp) {
			return runFillUpActivity(newFillUp.getmId());
		}
		return false;
	}
    
    private class FillUpListOnItemClickListener implements OnItemClickListener {
    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		Cursor c = ((FillUpAdapter)parent.getAdapter()).getCursor();
			runFillUpActivity(c.getLong(c.getColumnIndex(FillUp._ID)));
		}    	
    }
}