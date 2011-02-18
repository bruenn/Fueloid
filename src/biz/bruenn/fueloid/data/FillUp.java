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
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * @author adbrpa2
 * @version 1.0
 * @created 17-Jan-2011 10:23:13
 */
public class FillUp implements BaseColumns {
	public static final String TABLE_NAME = "fillups";
	public static final String DISTANCE = "distance";
	public static final String FILLDATE = "filldate";
	public static final String LITER = "liter";
	public static final String MONEY = "money";
	public static final String COLID = TABLE_NAME + "." + _ID;
	public static final String COLDISTANCE = TABLE_NAME + "." + DISTANCE;
	public static final String COLFILLDATE = TABLE_NAME + "." + FILLDATE;
	public static final String COLLITER = TABLE_NAME + "." + LITER;
	public static final String COLMONEY = TABLE_NAME + "." + MONEY;
	
	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " ("
			+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DISTANCE +" INTEGER, "
			+ FILLDATE +" INTEGER, "
			+ LITER +" REAL, "
			+ MONEY + " REAL);";
	
	private static final int MAX_DISTANCE = 300000; // TODO 
	
	private FueloidDBProxy.DatabaseHelper mDBHelper;
	private long mId;
	private int mDistance;
	private Date mFillDate;
	private float mLiter;
	private float mMoney;
	
	/**
	 * new object is created from database
	 * @param id
	 * @param distance
	 * @param date
	 * @param liter
	 * @param money
	 */
	public FillUp(Context context, long id, int distance, Date date, float liter, float money) {
		mDBHelper = new FueloidDBProxy.DatabaseHelper(context);
		mId = id;
		mDistance = distance;
		mFillDate = date;
		mLiter = liter;
		mMoney = money;
	}

	public void finalize() throws Throwable {

	}
	
	public final long getmId() {
		return mId;
	}
	
	public final Date getmDate() {
		return mFillDate;
	}
	
	public final int getDateDay() {
		return mFillDate.getDate();
	}
	
	public final int getDateHours() {
		return mFillDate.getHours();
	}
	
	public final int getDateMinutes() {
		return mFillDate.getMinutes();
	}
	
	public final int getDateMonth() {
		return mFillDate.getMonth() + 1;
	}
	
	public final int getDateYear() {
		return mFillDate.getYear() + 1900;
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
	 * @return the previous distance
	 */
	public int getNextDistance() {
		final String[] args = new String[] {String.valueOf(mFillDate.getTime())};		
		// TODO refactor query use SQLiteQueryBuilder
		final String queryNextDistance = "SELECT " + DISTANCE +
			" FROM " + TABLE_NAME + " WHERE " + FILLDATE + "=(SELECT MIN(" + FILLDATE + ") FROM " + TABLE_NAME + " WHERE " + FILLDATE +"> ?)";
		Cursor c = mDBHelper.protectedRawQuery(queryNextDistance, args);
		if(null != c && c.moveToFirst()) {
			return c.getInt(0);
		}		
		return MAX_DISTANCE;
	}
	
	/**
	 * @return the previous distance
	 */
	public int getPreviousDistance() {
		final String[] args = new String[] {String.valueOf(mFillDate.getTime())};		
		// TODO refactor query use SQLiteQueryBuilder
		final String queryPreviousDistance = "SELECT " + DISTANCE +
			" FROM " + TABLE_NAME + " WHERE " + FILLDATE + "=(SELECT MAX(" + FILLDATE + ") FROM " + TABLE_NAME + " WHERE " + FILLDATE +"< ?)";
		Cursor c = mDBHelper.protectedRawQuery(queryPreviousDistance, args);
		if(null != c && c.moveToFirst()) {
			return c.getInt(0);
		}		
		return 0;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setmDate(Date newVal) {
		mFillDate = newVal;
	}
	
	public void setDate(int year, int month, int day) {
		mFillDate.setYear(year);
		mFillDate.setMonth(month);
		mFillDate.setDate(day);
	}
	
	public void setTime(int hours, int minutes) {
		mFillDate.setHours(hours);
		mFillDate.setMinutes(minutes);
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setmDistance(int newVal) {
		mDistance = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setmLiter(float newVal) {
		mLiter = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setmMoney(float newVal) {
		mMoney = newVal;
	}
	

    
    public static void deleteFillUp(FueloidDBProxy.DatabaseHelper openHelper, long id) {
    	SQLiteDatabase db = null;
    	try {
    		db = openHelper.getWritableDatabase();
    		db.delete(FillUp.TABLE_NAME, FillUp._ID + "=" + id, null);
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
    public static FillUp getFillUp(FueloidDBProxy.DatabaseHelper openHelper, long id) {
    	SQLiteDatabase db = null;
    	try {
    		db = openHelper.getReadableDatabase();
    		Cursor c = db.query(FillUp.TABLE_NAME, new String[] {FillUp._ID, FillUp.DISTANCE, FillUp.FILLDATE, FillUp.LITER, FillUp.MONEY}, FillUp._ID + "=" + id, null, null, null, null);
    		if(null != c && c.moveToFirst())
    		{
        		return FillUp.getFillUp(openHelper.mContext, c);
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
     * Builds an fill-up object from a database cursor
     * @param context current context
     * @param cursor cursor to a database fill-up column
     * @return fill-up object representation or null if cursor was flawed
     */
	public static FillUp getFillUp(Context context, Cursor cursor) {	
		if((null != cursor) && (cursor.getColumnCount() == 5)) {
			try {
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
				int distance = cursor.getInt(cursor.getColumnIndexOrThrow(DISTANCE));
				Date fillDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(FILLDATE)));
				float liter = cursor.getFloat(cursor.getColumnIndexOrThrow(LITER));
				float money = cursor.getFloat(cursor.getColumnIndexOrThrow(MONEY));
				return new FillUp(context, id, distance, fillDate, liter, money);
			} catch (IllegalArgumentException e) {
				return null;
			} finally {
			}
		}
		return null;
	}
}