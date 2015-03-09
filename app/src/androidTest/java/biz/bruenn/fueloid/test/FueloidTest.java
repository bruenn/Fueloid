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

package biz.bruenn.fueloid.test;

import biz.bruenn.fueloid.FillUpList;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class FueloidTest extends ActivityInstrumentationTestCase2<FillUpList> {
	private FillUpList mActivity;
	private TextView mView;
	private String resourceString;

	public FueloidTest() {
		super("biz.bruenn.fueloid", FillUpList.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mView = (TextView) mActivity.findViewById(biz.bruenn.fueloid.R.id.addRefuel);
		resourceString = mActivity.getString(biz.bruenn.fueloid.R.id.title);
	}
	
	public void testPreconditions() {
		assertNotNull(mView);
	}
	
	public void testText() {
		assertEquals(resourceString, mView.getText());
	
	}
}
