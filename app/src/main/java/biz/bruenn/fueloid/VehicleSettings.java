package biz.bruenn.fueloid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.Vehicle;


public class VehicleSettings extends Activity {

	Vehicle mVehicle;
	EditText mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_settings);

		final long id = getIntent().getLongExtra(Vehicle.TABLE_NAME, -1);
		mVehicle = Vehicle.get(new FueloidDatabaseHelper(this), id);

		mName = (EditText)findViewById(R.id.name);
		mName.setText(mVehicle.getName());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_vehicle_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_accept:
				mVehicle.setName(mName.getText().toString());
				finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
