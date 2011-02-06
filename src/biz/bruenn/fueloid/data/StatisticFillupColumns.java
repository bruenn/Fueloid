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

import java.util.Vector;

import biz.bruenn.fueloid.data.FueloidDBProxy.DatabaseHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class StatisticFillupColumns implements BaseColumns {
	public static final String TABLE_NAME = "ltsf";//"linktable_statistics_fillups";
	public static final String SID = "sid"; //statistic id
	public static final String FID = "fid"; //fill-up id
	public static final String COLSID = TABLE_NAME + "." + SID;
	public static final String COLFID = TABLE_NAME + "." + FID;
	
	public static final String FILLUPS_OF_STATISTIC = FillUp.TABLE_NAME + ", " + TABLE_NAME + " " +
		"WHERE " + COLFID + "=" + FillUp.COLID + " AND " + COLSID + "= ?;";
	
	public static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " ("
			+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SID +" INTEGER, "
			+ FID +" INTEGER);";
	
	public static void cleanUpFillUps(DatabaseHelper openHelper) {
		Vector<Integer> liveIds = new Vector<Integer>();
		String sqlGetFillUpIds = "SELECT _id FROM fillups";
		String sqlGetLivingFillUpIds = "SELECT DISTINC SID FROM ltsf";
		Cursor allIds = FueloidDBProxy.protectedRawQuery(openHelper, sqlGetFillUpIds, null);
		Cursor cursorLiveIds = FueloidDBProxy.protectedRawQuery(openHelper, sqlGetLivingFillUpIds, null);
		
		while(cursorLiveIds.moveToNext()) {
			liveIds.add(cursorLiveIds.getInt(0));
		}
		
		while(allIds.moveToNext()) {
			Integer i = allIds.getInt(0);
			if(!liveIds.contains(i)) {
				FillUp.deleteFillUp(openHelper, i.intValue());
			}
		}
	}

	public static boolean delete(DatabaseHelper dh, Statistic s, FillUp f) {
		SQLiteDatabase db = null;
		try {
			db = dh.getWritableDatabase();
			if(null != db) {
				db.delete(TABLE_NAME, SID + "=" + s.getmId() + " AND " + FID + "=" + f.getmId(), null);
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
	
	public static Cursor getFillUpsForStatistic(DatabaseHelper dh, Statistic s) {
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
					" ORDER BY " + FillUp.COLDISTANCE + " DESC", new String[] {String.valueOf(s.getmId())});
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
	
	public static final boolean insert(DatabaseHelper dh, Statistic s, FillUp f) {
		SQLiteDatabase db = null;
		try {
			db = dh.getWritableDatabase();
			if(null != db) {
				ContentValues values = new ContentValues();
				values.put(SID, s.getmId());
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
