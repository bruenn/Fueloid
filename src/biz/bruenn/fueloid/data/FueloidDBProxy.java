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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/**
 * Provides access to a fueloid database.
 */
public class FueloidDBProxy {

    private static final String TAG = "FueloidDBAdapter";

    private static final String DATABASE_NAME = "fueloid3.db";
    private static final int DATABASE_VERSION = 14;
    //public static final String FILLUPS_TABLE_NAME = "fillups";

    public DatabaseHelper mOpenHelper;
    
    public FueloidDBProxy(Context context) {
    	mOpenHelper = new DatabaseHelper(context);
    }
    
    /**
     * Add a new fillup to database
     * @param distance
     * @param date
     * @param liter
     * @param money
     * @return
     */
    public FillUp insertFillUp(int distance, Date date, float liter, float money) {
    	SQLiteDatabase db = null;
    	try {
    		db = mOpenHelper.getWritableDatabase();
    		ContentValues values = new ContentValues();
    		values.put(FillUp.DISTANCE, distance);
    		values.put(FillUp.FILLDATE, date.getTime());
    		values.put(FillUp.LITER, liter);
    		values.put(FillUp.MONEY, money);
    		long id = db.insert(FillUp.TABLE_NAME, null, values);
    		if(-1 != id) {
    			return new FillUp(mOpenHelper.mContext, id, distance, date, liter, money);
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
    
    public void updateFillUp(FillUp f) {
    	SQLiteDatabase db = null;
    	try {
    		db = mOpenHelper.getWritableDatabase();
    		ContentValues values = new ContentValues();
    		values.put(FillUp.DISTANCE, f.getmDistance());
    		values.put(FillUp.FILLDATE, f.getmDate().getTime());
    		values.put(FillUp.LITER, f.getmLiter());
    		values.put(FillUp.MONEY, f.getmMoney());
    		db.update(FillUp.TABLE_NAME, values, FillUp._ID + "=" + f.getmId(), null);
    	} catch (Exception e) {
    		
    	} finally {
    		if(null != db) {
    			db.close();
    		}
    	}
    }

	/**
     * This class helps open, create, and upgrade the database file.
     */
    public static class DatabaseHelper extends SQLiteOpenHelper {
    	public Context mContext;
    	
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(FillUp.SQL_CREATE_TABLE);
        	db.execSQL(Statistic.SQL_CREATE_TABLE);
        	db.execSQL(StatisticFillupColumns.SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FillUp.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Statistic.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + StatisticFillupColumns.TABLE_NAME);
            onCreate(db);
        }
        
        /**
         * Wrapper to SQLiteDatabase.rawQuery()
         * If an exception occurs it would be catch inside and null
         * will be returned
         * @param query
         * @param selectionArgs
         * @return null if query failed
         */
        public Cursor protectedRawQuery(String query, String[] selectionArgs) {
    		SQLiteDatabase db = null;
    		Cursor result = null;
    		try {
    			db = getReadableDatabase();
    			if(null != db) {
    				result = db.rawQuery(query, selectionArgs);
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
}