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
	public FillUp(long id, int distance, Date date, float liter, float money) {
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
}