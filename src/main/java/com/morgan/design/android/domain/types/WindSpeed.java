package com.morgan.design.android.domain.types;

import static com.morgan.design.android.util.ObjectUtils.isBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;

import java.text.DecimalFormat;

import android.content.Context;

import com.morgan.design.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public enum WindSpeed {

	// mph or kph
	MPH("mph", "mp/h"), KMH("kmh", "km/h");

	private final String name;
	private final String yahoo;

	private WindSpeed(final String name, final String yahoo) {
		this.name = name;
		this.yahoo = yahoo;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static WindSpeed fromPref(final String speed) {
		for (final WindSpeed type : values()) {
			if (type.name.equalsIgnoreCase(speed)) {
				return type;
			}
		}
		Logger.e("Speed", "Unable to determine WindSpeed preference for [%s], returning default", speed);
		return MPH;
	}

	public static WindSpeed fromYahoo(final String speed) {
		for (final WindSpeed type : values()) {
			if (type.yahoo.equalsIgnoreCase(speed)) {
				return type;
			}
		}
		Logger.e("Speed", "Unable to convert yahoo speed from [%s]", speed);
		return null;
	}

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private static final double MILES_IN_A_KILOMETER = 0.621371192;
	private static final double KILOMETERS_IN_A_MILE = 1.609344;

	public static String fromSpeedAndUnit(final Context context, final String windSpeed, final WindSpeed windSpeedUnit) {
		if (isBlank(windSpeed)) {
			return "--";
		}

		final WindSpeed windSpeedMode = PreferenceUtils.getWindSpeedMode(context);
		final double convertSpeed = convertSpeed(Double.parseDouble(windSpeed), windSpeedUnit, windSpeedMode);

		return DECIMAL_FORMAT.format(convertSpeed) + (isNotNull(windSpeedUnit)
				? windSpeedMode.toString().toLowerCase()
				: "");
	}

	private static double convertSpeed(final double originalSpeed, final WindSpeed originalUnits, final WindSpeed convertedUnits) {
		if (originalUnits == convertedUnits) {
			return originalSpeed;
		}
		if (originalUnits == KMH && convertedUnits == MPH) {
			return originalSpeed * MILES_IN_A_KILOMETER;
		}
		if (originalUnits == MPH && convertedUnits == KMH) {
			return originalSpeed * KILOMETERS_IN_A_MILE;
		}
		return originalSpeed;
	}
}
