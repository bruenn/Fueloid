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

package biz.bruenn.fueloid.data;

import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Vehicle implements BaseColumns {
	public static final String TABLE_NAME = "vehicle";
	public static final String TITLE = "title";
	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " ("
			+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TITLE +" TEXT);";	
	
	//We assume there is no refuel tracked before 01.01.1900 ;-)
	private static final GregorianCalendar FIRST_DATE = new GregorianCalendar(1900, 0, 1);
	
	private FueloidDatabaseHelper mDBHelper;
	private long mId;
	public Vehicle(Context context){
		mDBHelper = new FueloidDatabaseHelper(context);
		mId = 1;
	}

	public void finalize() throws Throwable {
	}

	/**
	 * Add new fill-up to this vehicle
	 * @param newItem
	 */
	public void addFillUp(FillUp newItem) {
		VehicleFillupColumns.insert(mDBHelper, this, newItem);
	}

	/**
	 * Retrieve the distance between first and last fill-up of this vehicle
	 * @return 0 in case of an error
	 */
	public int getDistance() {
		//TODO implement this correctly
		return getDistance(Integer.MAX_VALUE);
	}
	
	public int getDistance(int numberOfRefuels) {
		final String[] args = new String[] {String.valueOf(mId), String.valueOf(numberOfRefuels)};		
		final String query = "SELECT " + FillUp.COLDISTANCE + " " +
			"FROM " + VehicleFillupColumns.FILLUPS_OF_VEHICLE_LIMITED;

		Cursor c = mDBHelper.protectedRawQuery(query, args);
		if(null == c || c.getCount() < 1 || c.getColumnCount() != 1) {
			return 0;
		}
		
		int upperDistance = c.getInt(0);
		c.moveToLast();
		return upperDistance - c.getInt(0);
	}

	/**
	 * Retrieve the distance recorded in the given time span
	 * @param start begin of the time span
	 * @param end end of the time span
	 * @return 0 in case of an error
	 */
	public int getDistance(GregorianCalendar start, GregorianCalendar end) {
		int startDistance = getDistance(start);
		int endDistance = getDistance(end);
		
		if(startDistance <= endDistance) {
			return endDistance - startDistance;
		}
		return 0;
	}

	/**
	 * Retrieve the distance of the last fill-up before the given date
	 * @param date of the latest fill-up
	 * @return 0 in case of an error
	 */
	public int getDistance(GregorianCalendar date) {
		final String[] args = new String[] {String.valueOf(mId), String.valueOf(date.getTimeInMillis())};		
		final String queryDistance = "SELECT MAX(" + FillUp.COLDISTANCE + ") " +
			"FROM" + VehicleFillupColumns.FILLUPS_AND_VEHICLE +
			"WHERE" + VehicleFillupColumns.VEHICLE_ID_EQUALS +
			"AND " + FillUp.COLFILLDATE + "<=?;";
		
		Cursor c = mDBHelper.protectedRawQuery(queryDistance, args);
		if(null != c && c.getColumnCount() == 1) {
			return c.getInt(0);
		}		
		return 0;
	}
	
	public Cursor getFillUpsCursor() {
		return VehicleFillupColumns.getFillUpsOfVehicle(mDBHelper, this);
	}
	
	public FillUp getLastFillUp() {
		final String[] args = new String[] { String.valueOf(mId) };
		final String queryMinMaxDistance = "SELECT " + FillUp.COLID + ", "
				+ "MAX(" + FillUp.COLDISTANCE + ") " + "FROM "
				+ VehicleFillupColumns.FILLUPS_OF_VEHICLE;

		Cursor c = mDBHelper.protectedRawQuery(queryMinMaxDistance, args);
		if (null != c && c.getCount() > 0 && c.getColumnCount() == 2) {
			int id = c.getInt(c.getColumnIndex(FillUp.COLID));
			return FillUp.getFillUp(mDBHelper, id);
		}
		return null;
	}
       
	public float getLiter() {
		return getLiter(FIRST_DATE, new GregorianCalendar());
	}
	
	public float getLiter(int numberOfFillups) {
		final String[] args = new String[] {String.valueOf(mId), String.valueOf(numberOfFillups)};		
		final String query = "SELECT " + FillUp.COLLITER + " " +
			"FROM " + VehicleFillupColumns.FILLUPS_OF_VEHICLE_LIMITED;

		Cursor c = mDBHelper.protectedRawQuery(query, args);
		if(null == c || c.getCount() < 1 || c.getColumnCount() != 1) {
			return 0f;
		}
		
		float liter = 0f;
		do {
			liter += c.getFloat(0);
		} while(c.moveToNext());
		return liter;
	}
       
	public float getLiter(GregorianCalendar start, GregorianCalendar end) {
		final String[] args = new String[] {String.valueOf(mId), String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis())};		
		final String queryMinMaxDistance = "SELECT SUM(" + FillUp.LITER + ") " +
			"FROM " + VehicleFillupColumns.FILLUPS_OF_VEHICLE_IN_TIMESPAN;
		
		Cursor c = mDBHelper.protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getCount() > 0 && c.getColumnCount() == 1) {
			return c.getFloat(0);
		}		
		return 0f;
	}
	
	public float getLiterPerDistance() {
		final int distance = getDistance();
		if(0 >= distance) {
			return 0f;
		}
		return getLiter()/distance;
	}
	
	public long getmId() {
		return mId;
	}
	
	public float getMoney() {
		return getMoney(FIRST_DATE, new GregorianCalendar());
	}
	
	public float getMoney(int numberOfFillups) {
		final String[] args = new String[] {String.valueOf(mId), String.valueOf(numberOfFillups)};		
		final String queryMoneyOfLatestFillups = "SELECT " + FillUp.COLMONEY + " " +
			"FROM " + VehicleFillupColumns.FILLUPS_OF_VEHICLE_LIMITED;

		Cursor c = mDBHelper.protectedRawQuery(queryMoneyOfLatestFillups, args);
		if(null == c || c.getCount() < 1 || c.getColumnCount() != 1) {
			return 0f;
		}
		
		float money = 0f;
		do {
			money += c.getFloat(0);
		} while(c.moveToNext());
		return money;
	}
	
	public float getMoney(GregorianCalendar start, GregorianCalendar end) {
		final String[] args = new String[] {String.valueOf(mId), String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis())};		
		final String queryMinMaxDistance = "SELECT SUM(" + FillUp.COLMONEY + ") " +
			"FROM " + VehicleFillupColumns.FILLUPS_OF_VEHICLE_IN_TIMESPAN;
		
		Cursor c = mDBHelper.protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getCount() > 0 && c.getColumnCount() == 1) {
			return c.getFloat(0);
		}		
		return 0f;
	}
	
	public float getMoneyPerLiter() {
		final float liter = getLiter();
		if(0 >= liter) {
			return 0f;
		}
		return getMoney()/liter;
	}

	public boolean removeFillUp(FillUp f) {
		VehicleFillupColumns.delete(mDBHelper, this, f);
		return true;
	}
}