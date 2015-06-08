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
