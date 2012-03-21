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

import android.database.Cursor;
import android.provider.BaseColumns;

public class Vehicle implements BaseColumns {
	public static final String CSV_HEADER = "DISTANCE;DATE;LITER;MONEY\n";
	public static final int UNIQUE_VEHICLE_ID = 1; //TODO add support for multiple vehicle
	public static final String TABLE_NAME = "vehicle";
	public static final String TITLE = "title";
	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " ("
			+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TITLE +" TEXT);";
	
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
	 * @param distance for the new fill-up
	 * @param date of the new fill-up
	 * @param liter amount of fuel filled up
	 * @param money amount of money payed at the fill-up
	 * @return the created fill-up or null if something went wrong
	 */
	public FillUp addFillUp(int distance, Date date, float liter, float money) {
		return FillUp.create(mDBHelper, mId, distance, date, liter, money);
	}
	
	public boolean exportToCsv(String filename) {
		FileWriter outputFile = null;
		try {
			outputFile = new FileWriter(filename);
			// write table header
			outputFile.write(CSV_HEADER);
			
			/** getFillUpsCursor() returns descending order, but for *.csv we
			 *  need ascending order -> iterate in reverse
			 */
			Cursor c = getFillUpsCursor();
			c.moveToLast();				
			while(!c.isBeforeFirst()) {
				FillUp f = FillUp.getFillUp(mDBHelper, c);
				if(null != f) {
					outputFile.write(f.toString());
				}
				c.moveToPrevious();
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if(null != outputFile) {
				try {
					outputFile.close();
				} catch (IOException e) {
					//ignore this exception
				}
			}
		}
	}
	
	/**
	 * Retrieve the distance for the last "numberOfFillups" fill-ups. If there
	 * are less than "numberOfFillups" recorded for this vehicle, the overall
	 * recorded distance is returned.
	 * @param numberOfFillups has to be greater or equal to 1
	 * @return	the distance for the last "numberOfFillups"
	 * <br>		the vehicles overall distance if "numberOfFillups" exceed the number
	 * 			of recorded fill-ups
	 * <br>		0 in case of an error
	 */
	public int getDistance(int numberOfFillups) {
		return mDBHelper.queryDistanceForLast(mId, numberOfFillups);
	}

	/**
	 * Retrieve the distance in a given time interval
	 * @param startDate date of the first fill-up in interval
	 * @param endDate date of the last fill-up in interval
	 * @return	- the distance between latest fill-up <= startDate and latest fill-up <= endDate
	 * <br>		- 0 in case of an error
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
	 * Call this method to get a cursor for iteration over all fill-ups of
	 * this vehicle. The result set is sorted descending by distance
	 * @return	- a Cursor to the fill-ups of this vehicle
	 * <br>		- null in case of an error
	 */
	public Cursor getFillUpsCursor() {
		return mDBHelper.queryFillUps(mId, Integer.MAX_VALUE);
	}
	
	/**
	 * Call this method to get the latest fill-up for this vehicle
	 * @return	- the latest fill-up in time
	 * <br>		- null in case of an error
	 */
	public FillUp getLastFillUp() {
		Cursor c = mDBHelper.queryFillUps(mId, 1);
		FillUp result = null;
		if(null != c) {
			result = FillUp.getFillUp(mDBHelper, c);
			c.close();
		}
		return result;
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
}