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
	
    static final String TAG = "FueloidDatabaseHelper";
    static final String DATABASE_NAME = "fueloid.db";
    static final int DATABASE_VERSION = 18;
	
    public FueloidDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(FillUp.SQL_CREATE_TABLE);
    	db.execSQL(Vehicle.SQL_CREATE_TABLE);
    	db.execSQL(VehicleFillupColumns.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + FillUp.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Vehicle.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleFillupColumns.TABLE_NAME);
        onCreate(db);
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
		SQLiteDatabase db = null;
		Cursor result = null;
		try {
			db = getReadableDatabase();
			if(null != db) {
				result = db.rawQuery(sql, selectionArgs);
				if(null != result) {
					result.moveToFirst();
				}
			}
			return result;
	    } catch (Exception e) {
	    	return null;    		
	    } finally {
	    	if(null != db ) {
	    		db.close();
	    	}
	    }
	}
}