/**
 Copyright (C) 2011-2015 Patrick Brünn.

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

public class NumberStatistic extends Statistic {
	
	private final int mNumber;
	
	public NumberStatistic(Vehicle vehicle, int number) {
		super(vehicle);
		mNumber = number;
	}

	@Override
	public int getDistance() {
		//Our start distance is the distance of the x+1 refuel in the past!
		return mVehicle.getDistance(mNumber+1);
	}

	@Override
	public float getLiter() {
		return mVehicle.getLiter(mNumber);
	}

	@Override
	public float getMoney() {
		return mVehicle.getMoney(mNumber);
	}

	@Override
	public CharSequence getTitle() {
		return "Last " + mNumber + " refuels";
	}

}
