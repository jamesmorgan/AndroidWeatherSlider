package com.morgan.design.android.util;

public class TimeUtils {

	public static String convertMinutesHumanReadableTime(final int mins) {
		if (60 > mins) {
			return mins + "minutes";
		}
		final int hours = mins / 60;
		final int minutes = mins % 60;
		return hours + ":" + minutes;
	}

}
