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
import android.provider.BaseColumns;

/**
 * @author adbrpa2
 * @version 1.0
 * @created 17-Jan-2011 10:35:33
 */
public class Statistic implements BaseColumns {
	public static final String TABLE_NAME = "statistics";
	public static final String TITLE = "title";
	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " ("
			+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TITLE +" TEXT);";	
	
	
	private FueloidDBProxy mDBProxy;
	private long mId;
	public Statistic(Context context){
		mDBProxy = new FueloidDBProxy(context);
		mId = 1;
	}

	public void finalize() throws Throwable {

	}

	/**
	 * Add new fill-up to this statistic
	 * @param newItem
	 */
	public void addFillUp(FillUp newItem) {
		StatisticFillupColumns.insert(mDBProxy.mOpenHelper, this, newItem);
	}

	/**
	 * Retrieve the distance between first and last fill-up in this statistic
	 * @return 0 in case of an error
	 */
	public int getDistance() {
		final String[] args = new String[] {String.valueOf(mId)};		
		final String queryMinMaxDistance = "SELECT MIN(" + FillUp.COLDISTANCE + "), " +
			"MAX(" + FillUp.COLDISTANCE + ") " +
			"FROM " + StatisticFillupColumns.FILLUPS_OF_STATISTIC;
		
		Cursor c = this.protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getColumnCount() == 2) {
			return c.getInt(1) - c.getInt(0);
		}		
		return 0;
	}
	
	public Cursor getFillUpsCursor() {
		return StatisticFillupColumns.getFillUpsForStatistic(mDBProxy.mOpenHelper, this);
	}
	
	public FillUp getLastFillUp() {
		final String[] args = new String[] {String.valueOf(mId)};		
		final String queryMinMaxDistance = "SELECT " + FillUp.COLID + ", " +
			"MAX(" + FillUp.COLDISTANCE + ") " +
			"FROM " + StatisticFillupColumns.FILLUPS_OF_STATISTIC;
		
		Cursor c = this.protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getColumnCount() == 2) {
			int id = c.getInt(c.getColumnIndex(FillUp.COLID));
			return mDBProxy.getFillUp(id);
		}		
		return null;		
	}
	
	public float getLiter() {
		final String[] args = new String[] {String.valueOf(mId)};		
		final String queryMinMaxDistance = "SELECT SUM(" + FillUp.LITER + ") " +
			"FROM " + StatisticFillupColumns.FILLUPS_OF_STATISTIC;
		
		Cursor c = this.protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getColumnCount() == 1) {
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
		final String[] args = new String[] {String.valueOf(mId)};		
		final String queryMinMaxDistance = "SELECT SUM(" + FillUp.COLMONEY + ") " +
			"FROM " + StatisticFillupColumns.FILLUPS_OF_STATISTIC;
		
		Cursor c = this.protectedRawQuery(queryMinMaxDistance, args);
		if(null != c && c.getColumnCount() == 1) {
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
		return StatisticFillupColumns.delete(mDBProxy.mOpenHelper, this, f);
	}
	
	private Cursor protectedRawQuery(String query, String[] selectionArgs) {
		SQLiteDatabase db = null;
		Cursor result = null;
		try {
			db = mDBProxy.mOpenHelper.getReadableDatabase();
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