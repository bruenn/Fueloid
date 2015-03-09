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

public class TimeStatistic extends Statistic {
	private final GregorianCalendar mStartDate;
	private final GregorianCalendar mEndDate;
	private final CharSequence mTitle;
	
	public TimeStatistic(Vehicle vehicle, int days) {
		super(vehicle);
		mTitle = "Last " + days + " days";
		mStartDate = new GregorianCalendar();
		mStartDate.add(GregorianCalendar.DAY_OF_MONTH, -days);
		mEndDate = new GregorianCalendar();
	}

	@Override
	public int getDistance() {
		return mVehicle.getDistance(mStartDate, mEndDate);
	}

	@Override
	public float getLiter() {
		return mVehicle.getLiter(mStartDate, mEndDate);
	}

	@Override
	public float getMoney() {
		return mVehicle.getMoney(mStartDate, mEndDate);
	}
	
	@Override
	public CharSequence getTitle() {
		return mTitle;
	}
}
