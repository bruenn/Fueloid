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

package biz.bruenn.fueloid.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.database.Cursor;
import android.os.Environment;
import android.test.AndroidTestCase;
import biz.bruenn.fueloid.data.FillUp;
import biz.bruenn.fueloid.data.FueloidDatabaseHelper;
import biz.bruenn.fueloid.data.Vehicle;

public class VehicleTest extends AndroidTestCase {
	static final GregorianCalendar EMPTY_START_DATE = new GregorianCalendar(2012, Calendar.JANUARY, 1);
	static final GregorianCalendar EMPTY_END_DATE = new GregorianCalendar(2012, Calendar.JANUARY, 31);
	static final GregorianCalendar ALL_START_DATE = new GregorianCalendar(2011, Calendar.JANUARY, 1);
	static final GregorianCalendar ALL_END_DATE = new GregorianCalendar(2011, Calendar.MAY, 1);
	static final int ALL_DISTANCE = 600;
	static final float ALL_LITER = ALL_DISTANCE / 10;
	static final float ALL_MONEY = ALL_DISTANCE / 100 * 14.96f;
	static final int LAST_DISTANCE = 100;
	static final float LAST_LITER = 10f;
	static final float LAST_MONEY = 14.96f;
	static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();
	static final String CSV_EXPORT_FILE = SD_CARD_PATH + "/export.csv";
	static final String CSV_IMPORT_FILE = SD_CARD_PATH + "/import.csv";
	
	private Vehicle mVehicle;


	protected void setUp() throws Exception {
		super.setUp();
		mVehicle = Vehicle.create(new FueloidDatabaseHelper(getContext()), "testee");
		/**
		 *  add some dummy fill-ups matching this table:
		 *  distance| date       |liter| money
		 *  ========|============|=====|=======
		 *  0       | 2011-01-01 |  0f |     0f
		 *  100	    | 2011-01-15 | 10f | 14.96f
		 *  300	    | 2011-01-31 | 20f | 29.92f
		 *  400     | 2011-04-01 | 10f | 14.96f
		 *  600     | 2011-05-01 | 10f | 14.96f
		 */
		/* use csv import */
		//prepare test csv
		FileWriter outputFile = new FileWriter(CSV_IMPORT_FILE);
		outputFile.write(Vehicle.CSV_HEADER);
		outputFile.write("0;2011-01-01 00:00:00;0.0;0.0\n");
		outputFile.write("100;2011-01-15 00:00:00;10.0;14.96\n");
		outputFile.write("300;2011-01-31 00:00:00;20.0;29.92\n");
		outputFile.write("400;2011-04-01 00:00:00;10.0;14.96\n");
		outputFile.write("500;2011-04-15 00:00:00;10.0;14.96\n");
		outputFile.write("600;2011-05-01 00:00:00;10.0;14.96\n");
		outputFile.close();
		mVehicle.importFromCsv(CSV_IMPORT_FILE);
		
		/* old implementation before csv import
		assertNotNull(mVehicle.addFillUp(0, ALL_START_DATE.getTime(), 0f, 0f));
		assertNotNull(mVehicle.addFillUp(100, new GregorianCalendar(2011, Calendar.JANUARY, 15).getTime(), 10f, 14.96f));
		assertNotNull(mVehicle.addFillUp(300, new GregorianCalendar(2011, Calendar.JANUARY, 31).getTime(), 20f, 14.96f * 2f));
		assertNotNull(mVehicle.addFillUp(400, new GregorianCalendar(2011, Calendar.APRIL,  1).getTime(), 10f, 14.96f));
		assertNotNull(mVehicle.addFillUp(ALL_DISTANCE - LAST_DISTANCE, new GregorianCalendar(2011, Calendar.APRIL, 15).getTime(), 10f, 14.96f));
		assertNotNull(mVehicle.addFillUp(ALL_DISTANCE, ALL_END_DATE.getTime(), LAST_LITER, LAST_MONEY));
		*/
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		FillUp next = mVehicle.getLastFillUp();
		while(next != null) {
			next.delete();
			next = mVehicle.getLastFillUp();
		}
		mVehicle = null;

		new File(CSV_EXPORT_FILE).delete();
		new File(CSV_IMPORT_FILE).delete();
	}
	
	public void testAddRemoveFillUp() throws Exception {	
		// create new fill-up
		FillUp testFillUp = mVehicle.addFillUp(10000000, new Date(), 0, 0);
		assertNotNull(testFillUp);
		
		// two fill-ups for the same distance are not allowed
		FillUp doubleFillUp = mVehicle.addFillUp(10000000, new Date(), 0, 0);		
		assertNull(doubleFillUp);
		
		// read the last fill-up back from database
		// -> this should be equal to our previously created fill-up
		FillUp readBack = mVehicle.getLastFillUp();
		assertEquals(testFillUp, readBack);
		readBack = null;
	}
	
	public void testCSVExport() throws Exception {
		mVehicle.exportToCsv(CSV_EXPORT_FILE);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(CSV_EXPORT_FILE), "UTF-8"));
		
		assertEquals(reader.readLine() + "\n", Vehicle.CSV_HEADER);
		assertEquals(reader.readLine(), "0;2011-01-01 00:00:00;0.0;0.0");
		assertEquals(reader.readLine(), "100;2011-01-15 00:00:00;10.0;14.96");
		assertEquals(reader.readLine(), "300;2011-01-31 00:00:00;20.0;29.92");
		assertEquals(reader.readLine(), "400;2011-04-01 00:00:00;10.0;14.96");
		assertEquals(reader.readLine(), "500;2011-04-15 00:00:00;10.0;14.96");
		assertEquals(reader.readLine(), "600;2011-05-01 00:00:00;10.0;14.96");
		reader.close();
	}
	
	public void testGetDistanceInTimespan() throws Exception {
		assertEquals(0, mVehicle.getDistance(EMPTY_START_DATE, EMPTY_END_DATE));
		assertEquals(0, mVehicle.getDistance(ALL_END_DATE, ALL_START_DATE));
		assertEquals(ALL_DISTANCE, mVehicle.getDistance(ALL_START_DATE, ALL_END_DATE));
	}
	
	public void testGetDistanceForLastFillUps() throws Exception {
		assertEquals(0, mVehicle.getDistance(0));
		assertEquals(0, mVehicle.getDistance(-1));
		assertEquals(ALL_DISTANCE, mVehicle.getDistance(Integer.MAX_VALUE));
		assertEquals(LAST_DISTANCE, mVehicle.getDistance(1));
	}
	
	public void testGetFillUpsCursor() throws Exception {
		Cursor c = mVehicle.getFillUpsCursor();
		// all columns present?
		assert(0 <= c.getColumnIndex(FillUp.FILLDATE));
		assert(0 <= c.getColumnIndex(FillUp.LITER));
		assert(0 <= c.getColumnIndex(FillUp.MONEY));
		assert(0 <= c.getColumnIndex(FillUp.VEHICLE_ID));
		assert(0 <= c.getColumnIndex(FillUp._ID));
		// keep index of distance column
		int distanceIndex = c.getColumnIndex(FillUp.DISTANCE);
		assert(0 <= distanceIndex);
		
		// test descending order previous
		int previousDistance = c.getInt(distanceIndex);
		while(c.moveToNext()) {
			int nextDistance = c.getInt(distanceIndex);
			assert(previousDistance > nextDistance);
			previousDistance = nextDistance;
		}
	}
	
	public void testLastFillUp() throws Exception {		
		//add some fill-ups, which are not ordered by distance
		Date sameDate = new Date();
		FillUp first = mVehicle.addFillUp(10000000, sameDate, 0f, 0f);
		assertNotNull(first);
		FillUp third = mVehicle.addFillUp(10000300, sameDate, 20.0f, 2.0f * 14.96f);
		assertNotNull(third);
		FillUp second= mVehicle.addFillUp(10000100, sameDate, 10.0f, 14.96f);
		assertNotNull(second);
		
		// read back in reverse
		FillUp readBack = mVehicle.getLastFillUp();
		assertEquals(readBack, third);
		third.delete();
		third = null;
		readBack = null;
		
		readBack = mVehicle.getLastFillUp();
		assertEquals(readBack, second);
		second.delete();
		second = null;
		readBack = null;
		
		readBack = mVehicle.getLastFillUp();
		assertEquals(readBack, first);
		first.delete();
		first = null;
		readBack = null;
	}
	
	public void testGetLiterInTimespan() throws Exception {
		assertEquals(0f, mVehicle.getLiter(EMPTY_START_DATE, EMPTY_END_DATE));
		assertEquals(0f, mVehicle.getLiter(ALL_END_DATE, ALL_START_DATE));
		assertEquals(ALL_LITER, mVehicle.getLiter(ALL_START_DATE, ALL_END_DATE));
	}
	
	public void testGetLiterForLastFillUps() throws Exception {
		assertEquals(0f, mVehicle.getLiter(0));
		assertEquals(0f, mVehicle.getLiter(-1));
		assertEquals(ALL_LITER, mVehicle.getLiter(Integer.MAX_VALUE));
		assertEquals(LAST_LITER, mVehicle.getLiter(1));
	}
	
	public void testGetMoneyInTimespan() throws Exception {
		assertEquals(0f, mVehicle.getMoney(EMPTY_START_DATE, EMPTY_END_DATE));
		assertEquals(0f, mVehicle.getMoney(ALL_END_DATE, ALL_START_DATE));
		assertEquals(ALL_MONEY, mVehicle.getMoney(ALL_START_DATE, ALL_END_DATE));
	}
	
	public void testGetMoneyForLastFillUps() throws Exception {
		assertEquals(0f, mVehicle.getMoney(0));
		assertEquals(0f, mVehicle.getMoney(-1));
		assertEquals(ALL_MONEY, mVehicle.getMoney(Integer.MAX_VALUE));
		assertEquals(LAST_MONEY, mVehicle.getMoney(1));
	}
}
