package com.morgan.design.android.domain.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Based on http://developer.yahoo.com/weather/#codes
 * 
 * @author James Edward Morgan
 */
public class WeatherConditions {

	private static Map<Integer, Condition> CONDITIONS;
	static {
		CONDITIONS = new HashMap<Integer, Condition>();
		CONDITIONS.put(0, new Condition(0, "tornado"));
		CONDITIONS.put(1, new Condition(1, "tropical storm"));
		CONDITIONS.put(2, new Condition(2, "hurricane"));
		CONDITIONS.put(3, new Condition(3, "severe thunderstorms"));
		CONDITIONS.put(4, new Condition(4, "thunderstorms"));
		CONDITIONS.put(5, new Condition(5, "mixed rain and snow"));
		CONDITIONS.put(6, new Condition(6, "mixed rain and sleet"));
		CONDITIONS.put(7, new Condition(7, "mixed snow and sleet"));
		CONDITIONS.put(8, new Condition(8, "freezing drizzle"));
		CONDITIONS.put(9, new Condition(9, "drizzle"));
		CONDITIONS.put(10, new Condition(10, "freezing rain"));
		CONDITIONS.put(11, new Condition(11, "showers"));
		CONDITIONS.put(12, new Condition(12, "showers"));
		CONDITIONS.put(13, new Condition(13, "snow flurries"));
		CONDITIONS.put(14, new Condition(14, "light snow showers"));
		CONDITIONS.put(15, new Condition(15, "blowing snow"));
		CONDITIONS.put(16, new Condition(16, "snow"));
		CONDITIONS.put(17, new Condition(17, "hail"));
		CONDITIONS.put(18, new Condition(18, "sleet"));
		CONDITIONS.put(19, new Condition(19, "dust"));
		CONDITIONS.put(20, new Condition(20, "foggy"));
		CONDITIONS.put(21, new Condition(21, "haze"));
		CONDITIONS.put(22, new Condition(22, "smoky"));
		CONDITIONS.put(23, new Condition(23, "blustery"));
		CONDITIONS.put(24, new Condition(24, "windy"));
		CONDITIONS.put(25, new Condition(25, "cold"));
		CONDITIONS.put(26, new Condition(26, "cloudy"));
		CONDITIONS.put(27, new Condition(27, "mostly cloudy (night)"));
		CONDITIONS.put(28, new Condition(28, "mostly cloudy (day)"));
		CONDITIONS.put(29, new Condition(29, "partly cloudy (night)"));
		CONDITIONS.put(30, new Condition(30, "partly cloudy (day)"));
		CONDITIONS.put(31, new Condition(31, "clear (night)"));
		CONDITIONS.put(32, new Condition(32, "sunny"));
		CONDITIONS.put(33, new Condition(33, "fair (night)"));
		CONDITIONS.put(34, new Condition(34, "fair (day)"));
		CONDITIONS.put(35, new Condition(35, "mixed rain and hail"));
		CONDITIONS.put(36, new Condition(36, "hot"));
		CONDITIONS.put(37, new Condition(37, "isolated thunderstorms"));
		CONDITIONS.put(38, new Condition(38, "scattered thunderstorms"));
		CONDITIONS.put(39, new Condition(39, "scattered thunderstorms"));
		CONDITIONS.put(40, new Condition(40, "scattered showers"));
		CONDITIONS.put(41, new Condition(41, "heavy snow"));
		CONDITIONS.put(42, new Condition(42, "scattered snow showers"));
		CONDITIONS.put(43, new Condition(43, "heavy snow"));
		CONDITIONS.put(44, new Condition(44, "partly cloudy"));
		CONDITIONS.put(45, new Condition(45, "thundershowers"));
		CONDITIONS.put(46, new Condition(46, "snow showers"));
		CONDITIONS.put(47, new Condition(47, "isolated thundershowers"));
		CONDITIONS.put(3200, new Condition(3200, "not available"));
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
