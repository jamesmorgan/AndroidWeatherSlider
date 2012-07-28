package com.morgan.design.android.domain.types;

import android.util.SparseArray;

/**
 * Based on http://developer.yahoo.com/weather/#codes
 * 
 * @author James Edward Morgan
 */
public class WeatherConditions {

	private static SparseArray<Condition> CONDITIONS;
	static {
		CONDITIONS = new SparseArray<WeatherConditions.Condition>();
		CONDITIONS.append(0, new Condition(0, "tornado"));
		CONDITIONS.append(1, new Condition(1, "tropical storm"));
		CONDITIONS.append(2, new Condition(2, "hurricane"));
		CONDITIONS.append(3, new Condition(3, "severe thunderstorms"));
		CONDITIONS.append(4, new Condition(4, "thunderstorms"));
		CONDITIONS.append(5, new Condition(5, "mixed rain and snow"));
		CONDITIONS.append(6, new Condition(6, "mixed rain and sleet"));
		CONDITIONS.append(7, new Condition(7, "mixed snow and sleet"));
		CONDITIONS.append(8, new Condition(8, "freezing drizzle"));
		CONDITIONS.append(9, new Condition(9, "drizzle"));
		CONDITIONS.append(10, new Condition(10, "freezing rain"));
		CONDITIONS.append(11, new Condition(11, "showers"));
		CONDITIONS.append(12, new Condition(12, "showers"));
		CONDITIONS.append(13, new Condition(13, "snow flurries"));
		CONDITIONS.append(14, new Condition(14, "light snow showers"));
		CONDITIONS.append(15, new Condition(15, "blowing snow"));
		CONDITIONS.append(16, new Condition(16, "snow"));
		CONDITIONS.append(17, new Condition(17, "hail"));
		CONDITIONS.append(18, new Condition(18, "sleet"));
		CONDITIONS.append(19, new Condition(19, "dust"));
		CONDITIONS.append(20, new Condition(20, "foggy"));
		CONDITIONS.append(21, new Condition(21, "haze"));
		CONDITIONS.append(22, new Condition(22, "smoky"));
		CONDITIONS.append(23, new Condition(23, "blustery"));
		CONDITIONS.append(24, new Condition(24, "windy"));
		CONDITIONS.append(25, new Condition(25, "cold"));
		CONDITIONS.append(26, new Condition(26, "cloudy"));
		CONDITIONS.append(27, new Condition(27, "mostly cloudy (night)"));
		CONDITIONS.append(28, new Condition(28, "mostly cloudy (day)"));
		CONDITIONS.append(29, new Condition(29, "partly cloudy (night)"));
		CONDITIONS.append(30, new Condition(30, "partly cloudy (day)"));
		CONDITIONS.append(31, new Condition(31, "clear (night)"));
		CONDITIONS.append(32, new Condition(32, "sunny"));
		CONDITIONS.append(33, new Condition(33, "fair (night)"));
		CONDITIONS.append(34, new Condition(34, "fair (day)"));
		CONDITIONS.append(35, new Condition(35, "mixed rain and hail"));
		CONDITIONS.append(36, new Condition(36, "hot"));
		CONDITIONS.append(37, new Condition(37, "isolated thunderstorms"));
		CONDITIONS.append(38, new Condition(38, "scattered thunderstorms"));
		CONDITIONS.append(39, new Condition(39, "scattered thunderstorms"));
		CONDITIONS.append(40, new Condition(40, "scattered showers"));
		CONDITIONS.append(41, new Condition(41, "heavy snow"));
		CONDITIONS.append(42, new Condition(42, "scattered snow showers"));
		CONDITIONS.append(43, new Condition(43, "heavy snow"));
		CONDITIONS.append(44, new Condition(44, "partly cloudy"));
		CONDITIONS.append(45, new Condition(45, "thundershowers"));
		CONDITIONS.append(46, new Condition(46, "snow showers"));
		CONDITIONS.append(47, new Condition(47, "isolated thundershowers"));
		CONDITIONS.append(3200, new Condition(3200, "not available"));
	}

	public static final Condition getCondition(final int ConditionCode) {
		return CONDITIONS.get(ConditionCode);
	}

	public static class Condition {

		private final int code;
		private final String description;

		public Condition(final int code, final String description) {
			this.code = code;
			this.description = description;
		}

		public int getCode() {
			return this.code;
		}

		public String getDescription() {
			return this.description;
		}

		@Override
		public String toString() {
			return this.description;
		}
	}
}
