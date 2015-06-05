package biz.bruenn.fueloid;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
				title.setText(cursor.getString(cursor.getColumnIndex(Vehicle.TITLE)));
			}
		}
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