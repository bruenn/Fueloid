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
import java.util.Vector;

/**
 * Provides access to a fueloid database.
 */
public class FueloidDBProxy {

    private static final String TAG = "FueloidDBAdapter";

    private static final String DATABASE_NAME = "fueloid3.db";
    private static final int DATABASE_VERSION = 12;
    //public static final String FILLUPS_TABLE_NAME = "fillups";

    public DatabaseHelper mOpenHelper;
    
    public FueloidDBProxy(Context context) {
    	mOpenHelper = new DatabaseHelper(context);
    }
    
    public void deleteFillUp(long id) {
    	SQLiteDatabase db = null;
    	try {
    		db = mOpenHelper.getWritableDatabase();
    		db.delete(FillUp.TABLE_NAME, FillUp._ID + "=" + id, null);
    	} catch (Exception e) {
    		
    	} finally {
    		if(null != db) {
    			db.close();
    		}
    	}
    }
    
    public Cursor getFillUpsCursor() {
    	SQLiteDatabase db = null;
    	try {
    		db = this.mOpenHelper.getReadableDatabase();
    		Cursor c = db.query(FillUp.TABLE_NAME, null, null, null, null, null, null);
    		if(null != c) {
    			c.moveToFirst();
    		}
    		return c;
    	} catch (Exception e) {
    		return null;
    	} finally {
    		db.close();
    	}
    }
    
    public Vector<FillUp> getFillUpsVector() {
    	Vector<FillUp> result = new Vector<FillUp>();
    	Cursor c = getFillUpsCursor();
    	while(c.moveToNext()) {
    		result.add(new FillUp(c.getLong(0), c.getInt(1), new Date(c.getLong(2)), c.getFloat(3), c.getFloat(4)));
    	}
    	return result;
    }
    
    /**
     * Read FillUp from database
     * @param id
     * @return FillUp object from database or null if <id> was not found
     */
    public FillUp getFillUp(long id) {
    	SQLiteDatabase db = null;
    	try {
    		db = mOpenHelper.getReadableDatabase();
    		Cursor c = db.query(FillUp.TABLE_NAME, new String[] {FillUp.DISTANCE, FillUp.FILLDATE, FillUp.LITER, FillUp.MONEY}, FillUp._ID + "=" + id, null, null, null, null);
    		if(c.moveToFirst())
    		{
        		return new FillUp(id, c.getInt(0), new Date(c.getLong(1)), c.getFloat(2), c.getFloat(3));
    		}
    		return null;
    	} catch (Exception e) {
    		return null;
    		
    	} finally {
    		if(null != db) {
    			db.close();
    		}
    	}
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
    			return new FillUp(id, distance, date, liter, money);
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

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
    }
}