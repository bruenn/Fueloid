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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class Vehicle implements BaseColumns {
	public static final int UNIQUE_VEHICLE_ID = 1; //TODO add support for multiple vehicle
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
	
	public Vehicle(FueloidDatabaseHelper dbHelper, long id) {
		mDBHelper = dbHelper;
		mId = id;		
	}

	public void finalize() throws Throwable {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		if (mId != other.mId)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (mId ^ (mId >>> 32));
		return result;
	}


	/**
	 * Create a new fill-up for this vehicle
	 * @param openHelper
	 * @param vehicleId
	 * @param distance
	 * @param date
	 * @param liter
	 * @param money
	 * @return the created fill-up or null if something went wrong
	 */
	public FillUp addFillUp(int distance, Date date, float liter, float money) {
		return FillUp.create(mDBHelper, mId, distance, date, liter, money);
	}
	
	//TODO finish implementation
	public boolean exportToCsv(String filename) {
		try {
			FileWriter f = new FileWriter(filename);

			Cursor c = this.getFillUpsCursor();
			int indexDistance = c.getColumnIndex(FillUp.COLDISTANCE);
			int indexDate = c.getColumnIndex(FillUp.COLFILLDATE);
			int indexLiter = c.getColumnIndex(FillUp.COLLITER);
			int indexMoney = c.getColumnIndex(FillUp.COLMONEY);
			
			while(!c.isAfterLast()) {
				f.append(c.getString(indexDistance) + ";"
						+ c.getString(indexDate) + ";"
						+ c.getString(indexLiter) + ";"
						+ c.getString(indexMoney) +"\n");
				c.moveToNext();
			}
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * @deprecated
	 * Retrieve the distance between first and last fill-up of this vehicle
	 * @return 0 in case of an error
	 */
	public int getDistance() {
		//TODO implement this correctly
		return getDistance(Integer.MAX_VALUE);
	}
	
	/**
	 * 
	 * @param numberOfRefuels
	 * @return the overall distance for the number of refuels specified
	 */
	public int getDistance(int numberOfRefuels) {
		final String[] args = new String[] {String.valueOf(mId), String.valueOf(numberOfRefuels)};		
		final String query = "SELECT " + FillUp.COLDISTANCE + " " +
			"FROM " + VehicleFillupColumns.FILLUPS_OF_VEHICLE_LIMITED;

		Cursor c = mDBHelper.protectedRawQuery(query, args);
		if(null == c || c.getCount() < 1 || c.getColumnCount() != 1) {
			if(null != c) c.close();
			return 0;
		}
		
		int upperDistance = c.getInt(0);
		c.moveToLast();
		int result = upperDistance - c.getInt(0);
		c.close();
		return result;
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
		int result = 0;
		if(null != c && c.getColumnCount() == 1) {
			result = c.getInt(0);
		}		
		if(null != c) c.close();
		return result;
	}
	
	/**
	 * 
	 * @return a Cursor to the fillups of this vehicle or null in case of an error
	 */
	public Cursor getFillUpsCursor() {
		return VehicleFillupColumns.getFillUpsOfVehicle(mDBHelper, this);
	}
	
	/**
	 * 
	 * @return the last refuel in time
	 */
	public FillUp getLastFillUp() {
		final String[] args = new String[] { String.valueOf(mId) };
		final String queryLastFillupId =
				"SELECT " + FillUp.COLID + ", MAX(" + FillUp.COLDISTANCE + ") " 
				+ "FROM fillups WHERE "
				+ FillUp.COLVEHICLE_ID + "=?;";

		FillUp result = null;
		Cursor c = mDBHelper.protectedRawQuery(queryLastFillupId, args);
		if (null != c && c.getCount() > 0 && c.getColumnCount() == 2) {
			long id = c.getLong(c.getColumnIndex(FillUp._ID));
			result = FillUp.getFillUp(mDBHelper, id);
		}
		if(null != c) c.close();
		return result;
	}
    
	/**
	 * @deprecated
	 * @return
	 */
	public float getLiter() {
		return getLiter(FIRST_DATE, new GregorianCalendar());
	}
	
	/**
	 * 
	 * @param numberOfFillups
	 * @return sum of liters refueled during the last 'numberOfFillups'
	 */
	public float getLiter(int numberOfFillups) {
		return mDBHelper.queryFillUpSumForLast(FillUp.LITER, mId, numberOfFillups);
	}
	
	/**
	 * 
	 * @param start date of the interval
	 * @param end date of the interval
	 * @return the number of liter refueled during the interval [start, end]
	 */
	public float getLiter(GregorianCalendar start, GregorianCalendar end) {
		return mDBHelper.queryFillUpSumInTimespan(FillUp.LITER, mId, start, end);
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public float getLiterPerDistance() {
		final int distance = getDistance();
		if(0 >= distance) {
			return 0f;
		}
		return getLiter()/distance;
	}
	
	/**
	 * @return
	 */
	public long getmId() {
		return mId;
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public float getMoney() {
		return getMoney(FIRST_DATE, new GregorianCalendar());
	}
	
	/**
	 * 
	 * @param numberOfFillups
	 * @return sum of money spend for the last 'numberOfFillups'
	 */
	public float getMoney(int numberOfFillups) {
		return mDBHelper.queryFillUpSumForLast(FillUp.MONEY, mId, numberOfFillups);
	}
	
	/**
	 * 
	 * @param start date of the interval
	 * @param end date of the interval
	 * @return sum of money spend in the date interval [start, end]
	 */
	public float getMoney(GregorianCalendar start, GregorianCalendar end) {
		return mDBHelper.queryFillUpSumInTimespan(FillUp.MONEY, mId, start, end);
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public float getMoneyPerLiter() {
		final float liter = getLiter();
		if(0 >= liter) {
			return 0f;
		}
		return getMoney()/liter;
	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	public boolean removeFillUp(FillUp f) {
		if(null == f) {
			return false;
		}
		VehicleFillupColumns.delete(mDBHelper, this, f);
		f.delete();
		return true;
	}
}