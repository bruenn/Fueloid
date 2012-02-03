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

import java.util.Date;
import java.util.GregorianCalendar;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;

public class FillUp implements BaseColumns {
	public static final String TABLE_NAME = "fillups";
	public static final String VEHICLE_ID = "vehicle_id";
	public static final String DISTANCE = "distance";
	public static final String FILLDATE = "filldate";
	public static final String LITER = "liter";
	public static final String MONEY = "money";
	public static final String COLID = TABLE_NAME + "." + _ID;
	public static final String COLVEHICLE_ID = TABLE_NAME + "." + VEHICLE_ID;
	public static final String COLDISTANCE = TABLE_NAME + "." + DISTANCE;
	public static final String COLFILLDATE = TABLE_NAME + "." + FILLDATE;
	public static final String COLLITER = TABLE_NAME + "." + LITER;
	public static final String COLMONEY = TABLE_NAME + "." + MONEY;
	public static final int MAX_DISTANCE = 3000000;// TODO
	
	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " ("
			+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ VEHICLE_ID +" INTEGER, "
			+ DISTANCE +" INTEGER, "
			+ FILLDATE +" INTEGER, "
			+ LITER +" REAL, "
			+ MONEY + " REAL, "
			+ "CONSTRAINT uuDistance UNIQUE(" + VEHICLE_ID + "," + DISTANCE + "));";
	
	private FueloidDatabaseHelper mDBHelper;
	private long mId;
	private int mDistance;
	private GregorianCalendar mFillDate = new GregorianCalendar();
	private float mLiter;
	private float mMoney;
	private Vehicle mVehicle;
	
	/**
	 * Constructor for objects created from database
	 * @param id
	 * @param distance
	 * @param date
	 * @param liter
	 * @param money
	 */
	private FillUp(FueloidDatabaseHelper openHelper, long id, long vehicleId, int distance, Date date, float liter, float money) {
		mDBHelper = openHelper;
		mId = id;
		mVehicle = new Vehicle(openHelper, vehicleId);
		mDistance = distance;
		mFillDate.setTime(date);
		mLiter = liter;
		mMoney = money;
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
		FillUp other = (FillUp) obj;
		if (mDistance != other.mDistance)
			return false;
		if (mFillDate == null) {
			if (other.mFillDate != null)
				return false;
		} else if (!mFillDate.equals(other.mFillDate))
			return false;
		if (mId != other.mId)
			return false;
		if (Float.floatToIntBits(mLiter) != Float.floatToIntBits(other.mLiter))
			return false;
		if (Float.floatToIntBits(mMoney) != Float.floatToIntBits(other.mMoney))
			return false;
		if (mVehicle == null) {
			if (other.mVehicle != null)
				return false;
		} else if (!mVehicle.equals(other.mVehicle))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mDistance;
		result = prime * result
				+ ((mFillDate == null) ? 0 : mFillDate.hashCode());
		result = prime * result + (int) (mId ^ (mId >>> 32));
		result = prime * result + Float.floatToIntBits(mLiter);
		result = prime * result + Float.floatToIntBits(mMoney);
		result = prime * result
				+ ((mVehicle == null) ? 0 : mVehicle.hashCode());
		return result;
	}
	
	public final long getmId() {
		return mId;
	}
	
	public final long getTimeInMillis() {
		try {
			return mFillDate.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			return 0;
		}
	}
	
	public final int getDateDay() {
		try {
			return mFillDate.get(GregorianCalendar.DAY_OF_MONTH);
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	public final int getDateHours() {
		try {
			return mFillDate.get(GregorianCalendar.HOUR_OF_DAY);
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	public final int getDateMinutes() {
		try {
			return mFillDate.get(GregorianCalendar.MINUTE);
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	/**
	 * Get the month index of the FillUp date
	 * Example date: 2011-01-31
	 * @return 0 for January
	 */
	public final int getDateMonth() {
		try {
			return mFillDate.get(GregorianCalendar.MONTH);
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	/**
	 * 
	 * @return year of the FillUp (f.e. 2011)
	 */
	public final int getDateYear() {
		try {
			return mFillDate.get(GregorianCalendar.YEAR);
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}

	public final int getmDistance() {
		return mDistance;
	}

	public final float getmLiter() {
		return mLiter;
	}

	public final float getmMoney() {
		return mMoney;
	}
	
	/**
	 * @return distance of the next FillUp in time
	 */
	public int getNextDistance() {
		int result = MAX_DISTANCE;
		final String[] args = new String[] {String.valueOf(mFillDate.getTimeInMillis())};		
		// TODO refactor query use SQLiteQueryBuilder
		final String queryNextDistance = "SELECT " + DISTANCE +
			" FROM " + TABLE_NAME + " WHERE " + FILLDATE + "=(SELECT MIN(" + FILLDATE + ") FROM " + TABLE_NAME + " WHERE " + FILLDATE +"> ?)";
		Cursor c = mDBHelper.protectedRawQuery(queryNextDistance, args);
		if(null != c && c.moveToFirst()) {
			result = c.getInt(0);
		}	
		if(null != c) c.close();
		return result;
	}
	
	/**
	 * @return distance of the previous FillUp in time
	 */
	public int getPreviousDistance() {
		int result = 0;
		final String[] args = new String[] {String.valueOf(mFillDate.getTimeInMillis())};		
		// TODO refactor query use SQLiteQueryBuilder
		final String queryPreviousDistance = "SELECT " + DISTANCE +
			" FROM " + TABLE_NAME + " WHERE " + FILLDATE + "=(SELECT MAX(" + FILLDATE + ") FROM " + TABLE_NAME + " WHERE " + FILLDATE +"< ?)";
		Cursor c = mDBHelper.protectedRawQuery(queryPreviousDistance, args);
		if(null != c && c.moveToFirst()) {
			result = c.getInt(0);
		}		
		if(null != c) c.close();
		return result;
	}
	
	/**
	 * Set year, month and day of date to new values, database is NOT updated!
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setDate(int year, int month, int day) {
		mFillDate.set(year, month, day);
	}
	
	/**
	 * Set hours and minutes of date to new values, database is NOT updated!
	 * @param hours
	 * @param minutes
	 */
	public void setTime(int hours, int minutes) {
		mFillDate.set(GregorianCalendar.HOUR_OF_DAY, hours);
		mFillDate.set(GregorianCalendar.MINUTE, minutes);
	}

	/**
	 * Set distance to new value, database is NOT updated!
	 * @param newVal
	 */
	public void setmDistance(int newVal) {
		mDistance = newVal;
	}

	/**
	 * Set liter to new value, database is NOT updated!
	 * @param newVal
	 */
	public void setmLiter(float newVal) {
		mLiter = newVal;
	}

	/**
	 * Set money to new value, database is NOT updated!
	 * @param newVal
	 */
	public void setmMoney(float newVal) {
		mMoney = newVal;
	}
	
	/**
	 * Update database
	 * Write changed values to database
	 */
    public void update() {
    	ContentValues values = new ContentValues();
    	values.put(FillUp.DISTANCE, getmDistance());
    	values.put(FillUp.FILLDATE, getTimeInMillis());
    	values.put(FillUp.LITER, getmLiter());
    	values.put(FillUp.MONEY, getmMoney());
    	mDBHelper.update(FillUp.TABLE_NAME, values, FillUp._ID + "=" + getmId(), null);
    }
	
    /**
     * Create a new FillUp object in database
     * @deprecated move code to Vehcle.addFillUp()
     * @param openHelper database helper
     * @param distance initial value
     * @param date initial value
     * @param liter initial value
     * @param money initial value
     * @return newly created FillUp object representation or null if creation fails
     */
    public static FillUp create(FueloidDatabaseHelper openHelper, long vehicleId, int distance, Date date, float liter, float money) {
    	SQLiteDatabase db = null;
    	try {
    		db = openHelper.getWritableDatabase();
    		ContentValues values = new ContentValues();
    		values.put(VEHICLE_ID, vehicleId);
    		values.put(DISTANCE, distance);
    		values.put(FILLDATE, date.getTime());
    		values.put(LITER, liter);
    		values.put(MONEY, money);
    		long id = db.insert(TABLE_NAME, null, values);
    		if(-1 != id) {
    			return new FillUp(openHelper, id, vehicleId, distance, date, liter, money);
    		}
    		return null;
    	} catch (SQLiteException e) {
    		return null;    		
    	} finally {
    		if(null != db) {
    			db.close();	
    		}
    	}
    }
    
    /**
     * Delete FillUp object from database
     */
    public void delete() {
    	SQLiteDatabase db = null;
    	try {
    		db = this.mDBHelper.getWritableDatabase();
    		db.delete(FillUp.TABLE_NAME, FillUp._ID + "=" + mId, null);
    	} catch (Exception e) {
    		
    	} finally {
    		if(null != db) {
    			db.close();
    		}
    	}
    }
	
	/**
     * Read FillUp from database
     * @param id
     * @return FillUp object from database or null if <id> was not found
     */
    public static FillUp getFillUp(FueloidDatabaseHelper openHelper, long id) {
    	SQLiteDatabase db = null;
    	Cursor c = null;
    	try {
    		db = openHelper.getReadableDatabase();
    		c = db.query(FillUp.TABLE_NAME, new String[] {FillUp._ID, FillUp.VEHICLE_ID, FillUp.DISTANCE, FillUp.FILLDATE, FillUp.LITER, FillUp.MONEY}, FillUp._ID + "=" + id, null, null, null, null);
    		if(null != c && c.moveToFirst())
    		{
        		return FillUp.getFillUp(openHelper, c);
    		}
    		return null;
    	} catch (Exception e) {
    		return null;
    	} finally {
    		if(null != db) {
    			db.close();
    		}
    		if(null != c) {
    			c.close();
    		}
    	}
    }
    
    /**
     * Creates a fill-up object from a database cursor
     * @param context current context
     * @param cursor cursor to a database fill-up column
     * @return fill-up object representation or null if cursor was flawed
     */
	private static FillUp getFillUp(FueloidDatabaseHelper openHelper, Cursor cursor) {	
		if((null != cursor) && (cursor.getColumnCount() == 6)) {
			try {
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
				long vehicleId = cursor.getLong(cursor.getColumnIndexOrThrow(VEHICLE_ID));
				int distance = cursor.getInt(cursor.getColumnIndexOrThrow(DISTANCE));
				Date fillDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(FILLDATE)));
				float liter = cursor.getFloat(cursor.getColumnIndexOrThrow(LITER));
				float money = cursor.getFloat(cursor.getColumnIndexOrThrow(MONEY));
				return new FillUp(openHelper, id, vehicleId, distance, fillDate, liter, money);
			} catch (IllegalArgumentException e) {
				return null;
			} finally {
			}
		}
		return null;
	}
}