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

public abstract class Statistic {

	protected final Vehicle mVehicle;

	public static final Statistic[] getStatistics(Vehicle vehicle) {
		return new Statistic[] {
				new NumberStatistic(vehicle, vehicle.countFillups() - 1),
				new TimeStatistic(vehicle, 31),
				new TimeStatistic(vehicle, 7),
				new NumberStatistic(vehicle, 3),
		};
	}

	public Statistic(Vehicle vehicle) {
		mVehicle = vehicle;
	}

	abstract public int getDistance();
	abstract public float getLiter();
	abstract public float getMoney();
	abstract public CharSequence getTitle();

	public String getText() {
		return String.format("%.01f Ø | %d km | %.02f l | %.02f €",
				getLiterPerDistance(),
				getDistance(),
				getLiter(),
				getMoney());
	}

	public float getLiterPerDistance() {
		int distance = getDistance();
		if(distance > 0) {
			return distance / getLiter();
		}
		return 0f;
	}

	public float getMoneyPerLiter() {
		float money = getMoney();
		if(money > 0f) {
			return money / getLiter();
		}
		return 0f;
	}
}