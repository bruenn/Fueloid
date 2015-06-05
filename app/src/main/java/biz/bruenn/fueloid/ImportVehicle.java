package biz.bruenn.fueloid;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;

import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.Vehicle;


public class ImportVehicle extends Activity {

	private Uri mUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_vehicle);

		mUri = getIntent().getData();

		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				importVehicleAndFinish();
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_import_vehicle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_accept:
				importVehicleAndFinish();
			case R.id.action_cancel:
				finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void importVehicleAndFinish()
	{
		final String title = ((EditText)findViewById(R.id.name)).getText().toString();
		Vehicle v = Vehicle.create(new FueloidDatabaseHelper(this), title);

		try {
			InputStream is = getContentResolver().openInputStream(mUri);
			v.importFromCsv(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finish();
	}
}
