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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
/**
 * @deprecated useless! move one fillup is connected to exactly one vehicle! -> use vehicle id as attribute of fillup
 * @author gpb
 *
 */
public class VehicleFillupColumns implements BaseColumns {
	public static final String TABLE_NAME = "ltvf";//"linktable_vehicle_fillups";
	public static final String VID = "vid"; //vehicle id
	public static final String FID = "fid"; //fill-up id	
	public static final String COLSID = TABLE_NAME + "." + VID;
	public static final String COLFID = TABLE_NAME + "." + FID;
	
	public static final String FILLUPS_AND_VEHICLE = " " + FillUp.TABLE_NAME + ", " + TABLE_NAME + " ";
	public static final String VEHICLE_ID_EQUALS = " " + COLFID + "=" + FillUp.COLID + " AND " + COLSID + "= ?";
	
	public static final String FILLUPS_OF_VEHICLE = FillUp.TABLE_NAME + ", " + TABLE_NAME + " " +
	"WHERE " + COLFID + "=" + FillUp.COLID + " AND " + COLSID + "= ?;";

	public static final String FILLUPS_OF_VEHICLE_LIMITED = FillUp.TABLE_NAME + ", " + TABLE_NAME + " " +
	"WHERE " + COLFID + "=" + FillUp.COLID + " AND " + COLSID + "= ? ORDER BY " + FillUp.COLFILLDATE + " DESC LIMIT ?;";

	public static final String FILLUPS_OF_VEHICLE_IN_TIMESPAN = FillUp.TABLE_NAME + ", " + TABLE_NAME + " " +
	"WHERE " + COLFID + "=" + FillUp.COLID + " AND " + COLSID + "= ? AND " +
	FillUp.COLFILLDATE + ">= ? AND " + FillUp.COLFILLDATE + "<= ?;";

	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " ("
			+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ VID +" INTEGER, "
			+ FID +" INTEGER);";

	public static boolean delete(FueloidDatabaseHelper dh, Vehicle s, FillUp f) {
		SQLiteDatabase db = null;
		try {			
			db = dh.getWritableDatabase();
			if(null != db) {
				db.delete(TABLE_NAME, VID + "=" + s.getmId() + " AND " + FID + "=" + f.getmId(), null);
			}
			return false;
		} catch (Exception e) {
			return false;
		} finally {
    		if(null != db ) {
    			db.close();
    		}
    	}
	}
	
	/**
	 * 
	 * @param dh database helper
	 * @param vehicle of which fillups we request
	 * @return a Cursor to the fillups of the given vehicle or null in case of an error
	 */
	public static Cursor getFillUpsOfVehicle(FueloidDatabaseHelper dh, Vehicle v) {
		SQLiteDatabase db = null;
		try {
			db = dh.getReadableDatabase();
			if(null != db) {
				Cursor c = db.rawQuery(					
					"SELECT " + FillUp.COLID + ", " +
								FillUp.COLDISTANCE + ", " +
								FillUp.FILLDATE + ", " +
								FillUp.COLLITER + ", " +
								FillUp.COLMONEY + 
					" FROM " + FillUp.TABLE_NAME + ", " + TABLE_NAME +
					" WHERE " + COLFID + "=" + FillUp.COLID + " AND " + COLSID + "=?"+
					" ORDER BY " + FillUp.COLFILLDATE + " DESC", new String[] {String.valueOf(v.getmId())});
				if(null != c) {
					c.moveToFirst();
				}
				return c;
			}
			return null;
    	} catch (Exception e) {
    		return null;    		
    	} finally {
    		if(null != db ) {
    			db.close();
    		}
    	}
	}
	
	public static final boolean insert(FueloidDatabaseHelper dh, Vehicle s, FillUp f) {
		SQLiteDatabase db = null;
		try {
			db = dh.getWritableDatabase();
			if(null != db) {
				ContentValues values = new ContentValues();
				values.put(VID, s.getmId());
				values.put(FID, f.getmId());
				db.insert(TABLE_NAME, null, values);
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		} finally {
			if(null != db ) {
    			db.close();
			}
    	}
	}
}
