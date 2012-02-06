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
	 * @param numberOfFillups
	 * @return the overall distance for the number of refuels specified
	 */
	public int getDistance(int numberOfFillups) {
		return mDBHelper.queryDistanceForLast(mId, numberOfFillups);
	}

	/**
	 * Retrieve the distance of the last fill-up before the given date
	 * @param date of the latest fill-up
	 * @return 0 in case of an error
	 */
	public int getDistance(GregorianCalendar startDate, GregorianCalendar endDate) {
		
		int startDistance = mDBHelper.queryDistanceForDate(mId, startDate);
		int endDistance = mDBHelper.queryDistanceForDate(mId, endDate);
		
		if(endDistance > startDistance) {
			return endDistance - startDistance;
		}
		return 0;
	}

	/**
	 * @deprecated
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
	 * Call this function to get a cursor for iteration over all fillups of
	 * this vehicle. The result set is sorted by distance descending
	 * @return a Cursor to the fillups of this vehicle or null in case of an error
	 */
	public Cursor getFillUpsCursor() {
		return mDBHelper.queryFillUps(mId, Integer.MAX_VALUE);
	}
	
	/**
	 * 
	 * @return the last refuel in time
	 */
	public FillUp getLastFillUp() {
		Cursor c = mDBHelper.queryFillUps(mId, 1);
		if(null == c) return null;
		
		FillUp result = FillUp.getFillUp(mDBHelper, c);
		
		c.close();
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
		return mDBHelper.querySumForLast(FillUp.LITER, mId, numberOfFillups);
	}
	
	/**
	 * 
	 * @param start date of the interval
	 * @param end date of the interval
	 * @return the number of liter refueled during the interval [start, end]
	 */
	public float getLiter(GregorianCalendar start, GregorianCalendar end) {
		return mDBHelper.querySumInTimespan(FillUp.LITER, mId, start, end);
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
		return mDBHelper.querySumForLast(FillUp.MONEY, mId, numberOfFillups);
	}
	
	/**
	 * 
	 * @param start date of the interval
	 * @param end date of the interval
	 * @return sum of money spend in the date interval [start, end]
	 */
	public float getMoney(GregorianCalendar start, GregorianCalendar end) {
		return mDBHelper.querySumInTimespan(FillUp.MONEY, mId, start, end);
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