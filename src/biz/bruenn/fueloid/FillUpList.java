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

import java.text.DateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.TextView;
import biz.bruenn.fueloid.EditFillUp;
import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.FillUp;
import biz.bruenn.fueloid.data.Vehicle;
import biz.bruenn.fueloid.data.VehicleFillupColumns;

public class FillUpList extends ListActivity {
	FueloidDatabaseHelper mDBHelper;
	FillUpAdapter mFillUpAdapter;
	Vehicle mVehicle;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDBHelper = new FueloidDatabaseHelper(this);
        mVehicle = new Vehicle(mDBHelper, Vehicle.UNIQUE_VEHICLE_ID);
        
        TextView addButton = (TextView)findViewById(R.id.addRefuel);
        addButton.setOnClickListener(mOnClickListener);      
        
	    mFillUpAdapter = new FillUpAdapter(this, VehicleFillupColumns.getFillUpsOfVehicle(mDBHelper, mVehicle));
	    
	    this.setListAdapter(mFillUpAdapter);
	    this.getListView().setOnItemClickListener(new FillUpListOnItemClickListener());
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
    		mVehicle.removeFillUp(f);
    		f.delete();
    		mFillUpAdapter.changeCursor(mVehicle.getFillUpsCursor());
    		updateText();
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.statistics:
			Intent i = new Intent(this, StatisticList.class);
			startActivityForResult(i, 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    @Override
    public void onResume() {
    	super.onResume();    	
    	updateText();
    }
    
    private void updateText() {
    	//reread fillup list from database, by updating the list adapters cursor
    	((FillUpAdapter)this.getListAdapter()).changeCursor(VehicleFillupColumns.getFillUpsOfVehicle(mDBHelper, mVehicle));
    	TextView mDistance = (TextView)this.findViewById(R.id.vehicleDistance);
    	mDistance.setText(mVehicle.getDistance() + "km|"
    					+ mVehicle.getMoney() + "€|"
    					+ mVehicle.getLiter() + "l|"
    					+ mVehicle.getLiterPerDistance() + "l/km|"
    					+ mVehicle.getMoneyPerLiter() + "€/l");
	}

	private class FillUpAdapter extends CursorAdapter {

		public FillUpAdapter(Context context, Cursor c) {
			super(context, c, true);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView titel = (TextView) view.findViewById(R.id.title);
			if(null != titel) {
				titel.setText(DateFormat.getDateInstance().format(cursor.getLong(cursor.getColumnIndex(FillUp.FILLDATE)))
							+ " | " + cursor.getInt(cursor.getColumnIndex(FillUp.DISTANCE)) + "km");
			}
			TextView text  = (TextView) view.findViewById(R.id.text);
			if(null != text) {
				text.setText(cursor.getFloat(cursor.getColumnIndex(FillUp.LITER)) + "l; " + cursor.getFloat(cursor.getColumnIndex(FillUp.MONEY)) + "€");
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(R.layout.fillup_row, parent, false);
			return view;
		}
    }
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
    	@Override
		public void onClick(View v) {
    	FillUp lastFillUp = mVehicle.getLastFillUp();
    	FillUp newFillUp = null;
    	if(null != lastFillUp) {
    		newFillUp = mVehicle.addFillUp(lastFillUp.getmDistance() + 1, new Date(), lastFillUp.getmLiter(), lastFillUp.getmMoney());
    	} else {
    		newFillUp = mVehicle.addFillUp(0, new Date(), 0f, 0f);
    	}
		Intent i = new Intent(v.getContext(), EditFillUp.class);
		i.putExtra(FillUp.TABLE_NAME, newFillUp.getmId());
		startActivityForResult(i, 0);
    	}
	};
    
    private class FillUpListOnItemClickListener implements OnItemClickListener {
    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		Cursor c = ((FillUpAdapter)parent.getAdapter()).getCursor();
    		
    		Intent i = new Intent(view.getContext(), EditFillUp.class);
			i.putExtra(FillUp.TABLE_NAME, c.getLong(c.getColumnIndex(FillUp._ID)));
			startActivityForResult(i, 0);			
		}    	
    }
}