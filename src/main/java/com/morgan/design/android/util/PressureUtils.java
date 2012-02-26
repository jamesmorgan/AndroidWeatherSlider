package com.morgan.design.android.util;

import com.morgan.design.weatherslider.R;

public class PressureUtils {

	// barometric pressure, in the units specified by the pressure attribute of the yweather:units element (in or mb). (float).
	// rising: state of the barometric pressure: steady (0), rising (1), or falling (2). (integer: 0, 1, 2)

	public static String getPressureState(final String rising) {
		if ("0".equalsIgnoreCase(rising)) {
			return "steady";
		}
		if ("1".equalsIgnoreCase(rising)) {
			return "rising";
		}
		if ("2".equalsIgnoreCase(rising)) {
			return "falling";
		}
		return "--";
	}

	public static int getPressureStateImage(final String rising) {
		if ("0".equalsIgnoreCase(rising)) {
			return R.drawable.pressure_steady;
		}
		if ("1".equalsIgnoreCase(rising)) {
			return R.drawable.pressure_rising;
		}
		if ("2".equalsIgnoreCase(rising)) {
			return R.drawable.pressure_falling;
		}
		return R.drawable.pressure_steady;
	}
}
