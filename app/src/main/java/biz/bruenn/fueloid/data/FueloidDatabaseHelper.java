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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class helps open, create, and upgrade the database file.
 */
public class FueloidDatabaseHelper extends SQLiteOpenHelper {
	public Context mContext;
	private SQLiteDatabase mDatabase;
	
    static final String TAG = "FueloidDatabaseHelper";
    static final String DATABASE_NAME = "fueloid.db";
    static final int DATABASE_VERSION = 25;
	
    public FueloidDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mDatabase = getWritableDatabase();
    }

	public void finalize() throws Throwable {
		if((null != mDatabase) && mDatabase.isOpen()) {
			mDatabase.close();
		}
	}

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(FillUp.SQL_CREATE_TABLE);
    	db.execSQL(Vehicle.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + FillUp.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Vehicle.TABLE_NAME);
        onCreate(db);
    }
    
    private boolean isDBAvailable() {
    	if(null == mDatabase || !mDatabase.isOpen()) {
    		mDatabase = getWritableDatabase();
    	}
    	if(null == mDatabase) {
    		return false;
    	}
    	return mDatabase.isOpen();
    }
    
    /**
     * Wrapper to SQLiteDatabase.rawQuery()
     * If an exception occurs it would be catch inside and null will be returned
     * @param sql the SQL query. The SQL string must not be ; terminated
     * @param selectionArgs You may include ?s in where clause in the query,
     *        which will be replaced by the values from selectionArgs. The values
     *        will be bound as Strings.
     * @return null or a Cursor object, which is positioned at the first entry.
     * 		   Note that Cursors are not synchronized, see the documentation for
     * 		   more details.
     */
    public Cursor protectedRawQuery(String sql, String[] selectionArgs) {
    	if(!isDBAvailable()) {
    		return null;
    	}
		try {
			Cursor result = mDatabase.rawQuery(sql, selectionArgs);
			if(null != result) {
				result.moveToFirst();
			}
			return result;
	    } catch (Exception e) {
	    	System.out.print(e);
	    	return null;    		
	    }
	}
    
	public int queryDistanceForDate(long vehicleId, GregorianCalendar date) {
		final String[] args = new String[] {String.valueOf(vehicleId), String.valueOf(date.getTimeInMillis())};		
		final String queryMinMaxDistance = "SELECT MAX(" + FillUp.DISTANCE + ") " +
			"FROM " + FillUp.TABLE_NAME + " WHERE " +
			FillUp.COLVEHICLE_ID + "=? AND " +
			FillUp.COLFILLDATE + "<=?";
		
		int result = 0;
		Cursor c = protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getCount() > 0 && c.getColumnCount() == 1) {
			result = c.getInt(0);
		}		
		if(null != c) c.close();
		return result;
	}
    
	public int queryDistanceForLast(long vehicleId, int numFillups) {
		if(numFillups <= 0) {
			return 0;
		}
		/** increase numFillups by one to fulfill requirement distance(1)
		 * which would be distance(now) - distance(last)
		 */
		numFillups++;
		final String[] args = new String[] {String.valueOf(vehicleId), String.valueOf(numFillups)};		
		final String queryMinMaxDistance = "SELECT "+ FillUp.DISTANCE +" FROM " +
			FillUp.TABLE_NAME + " WHERE " +
			FillUp.VEHICLE_ID + "=? ORDER BY " +
			FillUp.DISTANCE + " DESC LIMIT ?";
		
		Cursor c = protectedRawQuery(queryMinMaxDistance, args);
		if(null == c || c.getCount() < 2 || c.getColumnCount() != 1) {
			if(null != c) c.close();
			return 0;
		}
		
		int distance = c.getInt(0);
		c.moveToLast();
		distance -= c.getInt(0);
		c.close();
		return distance;
	}

	public Cursor queryFillUps(long vehicleId, int numFillups) {
		final String[] args = new String[] {String.valueOf(vehicleId), String.valueOf(numFillups)};
		final String queryMinMaxDistance = "SELECT * FROM " +
				FillUp.TABLE_NAME + " WHERE " +
				FillUp.VEHICLE_ID + "=? ORDER BY " +
				FillUp.DISTANCE + " DESC LIMIT ?";

		Cursor c = protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getCount() >= 1) {
			c.moveToFirst();
			return c;
		}

		if(null != c) c.close();
		return null;
	}

	public Cursor queryVehicles() {
		final String queryMinMaxDistance = "SELECT * FROM " + Vehicle.TABLE_NAME;

		Cursor c = protectedRawQuery(queryMinMaxDistance, null);
		if(null != c && c.getCount() >= 1) {
			c.moveToFirst();
			return c;
		}

		if(null != c) c.close();
		return null;
	}
    
	public float querySumForLast(String columnName, long vehicleId, int numFillups) {
		if(numFillups <= 0) {
			return 0f;
		}
		final String[] args = new String[] {String.valueOf(vehicleId), String.valueOf(numFillups)};		
		final String queryMinMaxDistance = "SELECT "+ columnName +" FROM " +
			FillUp.TABLE_NAME + " WHERE " +
			FillUp.VEHICLE_ID + "=? ORDER BY " +
			FillUp.DISTANCE + " DESC LIMIT ?";
		
		Cursor c = protectedRawQuery(queryMinMaxDistance, args);
		if(null == c || c.getCount() < 1 || c.getColumnCount() != 1) {
			if(null != c) c.close();
			return 0f;
		}
		
		float sum = 0f;
		do {
			sum += c.getFloat(0);
		} while(c.moveToNext());
		c.close();
		return sum;
	}
    
	public float querySumInTimespan(String columnName, long vehicleId, GregorianCalendar start, GregorianCalendar end) {
		final String[] args = new String[] {String.valueOf(vehicleId), String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis())};		
		final String queryMinMaxDistance = "SELECT SUM(" + columnName + ") " +
			"FROM " + FillUp.TABLE_NAME + " WHERE " +
			FillUp.COLVEHICLE_ID + "=? AND " +
			FillUp.COLFILLDATE + ">=? AND " +
			FillUp.COLFILLDATE + "<=?";
		
		float result = 0f;
		Cursor c = protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getCount() > 0 && c.getColumnCount() == 1) {
			result = c.getFloat(0);
		}		
		if(null != c) c.close();
		return result;
	}
    
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
    	if(!isDBAvailable()) {
    		return 0;
    	}
    	return mDatabase.update(table, values, whereClause, whereArgs);
    }
}