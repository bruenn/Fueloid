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
