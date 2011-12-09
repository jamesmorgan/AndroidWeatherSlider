package com.morgan.design.android.domain.types;

import com.morgan.design.android.util.Logger;

public enum Speed {

	// mph or kph
	MPH, KPH;

	public static String fromPref(final String speed) {
		for (final Speed type : values()) {
			if (type.name().equalsIgnoreCase(speed)) {
				return type.name();
			}
		}
		Logger.e("Speed", "Unable to determine speed for [%s]", speed);
		return null;
	}

	public static Speed to(final String speed) {
		for (final Speed type : values()) {
			if (type.name().equalsIgnoreCase(speed)) {
				return type;
			}
		}
		Logger.e("Speed", "Unable to convert speed from [%s]", speed);
		return null;
	}
}
