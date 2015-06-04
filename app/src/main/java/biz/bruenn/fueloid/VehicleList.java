package biz.bruenn.fueloid;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import biz.bruenn.fueloid.data.FillUp;
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


		TextView addButton = (TextView)findViewById(R.id.addVehicle);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Vehicle.create(mDBHelper, "BlaBla");
			}
		});

		mVehicleAdapter = new VehicleAdapter(this, mDBHelper.queryVehicles());
		setListAdapter(mVehicleAdapter);
		getListView().setOnItemClickListener(new VehicleListOnItemClickListener());
		registerForContextMenu(getListView());
	}

	private class VehicleAdapter extends CursorAdapter {

		public VehicleAdapter(Context context, Cursor c) {
			super(context, c, true);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			return LayoutInflater.from(context).inflate(R.layout.fillup_row, viewGroup, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView title = (TextView) view.findViewById(R.id.title);
			if(null != title) {
				title.setText(cursor.getString(cursor.getColumnIndex(Vehicle.TITLE))
				+ cursor.getLong(cursor.getColumnIndex(Vehicle._ID)));
			}
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
				mVehicleAdapter.changeCursor(mDBHelper.queryVehicles());
				return true;
			default:
				return super.onContextItemSelected(item);
		}
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
