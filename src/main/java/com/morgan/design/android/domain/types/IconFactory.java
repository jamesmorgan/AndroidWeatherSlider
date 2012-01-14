package com.morgan.design.android.domain.types;

import java.util.HashMap;
import java.util.Map;

import com.morgan.design.android.dao.orm.WeatherChoice;
import com.weatherslider.morgan.design.R;

public class IconFactory {

	public static int NOTE_FOUND = 3200;

	public static final Map<Integer, Integer> WEATHER_MAP;

	public static final int NA = R.drawable.weather_channel_44;

	static {
		WEATHER_MAP = new HashMap<Integer, Integer>();

		// Original Images
		// WEATHER_MAP.put(1, R.drawable.a2);
		// WEATHER_MAP.put(2, R.drawable.a2);
		// WEATHER_MAP.put(3, R.drawable.a2);
		// WEATHER_MAP.put(4, R.drawable.a2);
		// WEATHER_MAP.put(5, R.drawable.a5);
		// WEATHER_MAP.put(6, R.drawable.a5);
		// WEATHER_MAP.put(7, R.drawable.a5);
		// WEATHER_MAP.put(8, R.drawable.a8);
		// WEATHER_MAP.put(9, R.drawable.a9);
		// WEATHER_MAP.put(10, R.drawable.a9);
		// WEATHER_MAP.put(11, R.drawable.a8);
		// WEATHER_MAP.put(12, R.drawable.a8);
		// WEATHER_MAP.put(13, R.drawable.a13);
		// WEATHER_MAP.put(14, R.drawable.a13);
		// WEATHER_MAP.put(15, R.drawable.a13);
		// WEATHER_MAP.put(16, R.drawable.a13);
		// WEATHER_MAP.put(17, R.drawable.a19);
		// WEATHER_MAP.put(18, R.drawable.a19);
		// WEATHER_MAP.put(19, R.drawable.a19);
		// WEATHER_MAP.put(20, R.drawable.a19);
		// WEATHER_MAP.put(21, R.drawable.a19);
		// WEATHER_MAP.put(22, R.drawable.a19);
		// WEATHER_MAP.put(23, R.drawable.a19);
		// WEATHER_MAP.put(24, R.drawable.a24);
		// WEATHER_MAP.put(25, R.drawable.a25);
		// WEATHER_MAP.put(26, R.drawable.a26);
		// WEATHER_MAP.put(27, R.drawable.a27);
		// WEATHER_MAP.put(28, R.drawable.a28);
		// WEATHER_MAP.put(29, R.drawable.a29);
		// WEATHER_MAP.put(30, R.drawable.a30);
		// WEATHER_MAP.put(31, R.drawable.a31);
		// WEATHER_MAP.put(32, R.drawable.a32);
		// WEATHER_MAP.put(33, R.drawable.a33);
		// WEATHER_MAP.put(34, R.drawable.a34);
		// WEATHER_MAP.put(35, R.drawable.a35);
		// WEATHER_MAP.put(36, R.drawable.a36);
		// WEATHER_MAP.put(37, R.drawable.a2);
		// WEATHER_MAP.put(38, R.drawable.a2);
		// WEATHER_MAP.put(39, R.drawable.a2);
		// WEATHER_MAP.put(40, R.drawable.a2);
		// WEATHER_MAP.put(41, R.drawable.a41);
		// WEATHER_MAP.put(42, R.drawable.a41);
		// WEATHER_MAP.put(43, R.drawable.a41);
		// WEATHER_MAP.put(44, R.drawable.a44);
		// WEATHER_MAP.put(45, R.drawable.a45);
		// WEATHER_MAP.put(46, R.drawable.a46);
		// WEATHER_MAP.put(47, R.drawable.a46);
		// WEATHER_MAP.put(3200, R.drawable.a3200);

		// Weather Channel

		// 0 tornado
		WEATHER_MAP.put(0, R.drawable.weather_channel_00);
		// 1 tropical storm
		WEATHER_MAP.put(1, R.drawable.weather_channel_01);
		// 2 hurricane
		WEATHER_MAP.put(2, R.drawable.weather_channel_02);
		// 3 severe thunderstorms
		WEATHER_MAP.put(3, R.drawable.weather_channel_03);
		// 4 thunderstorms
		WEATHER_MAP.put(4, R.drawable.weather_channel_04);
		// 5 mixed rain and snow
		WEATHER_MAP.put(5, R.drawable.weather_channel_05);
		// 6 mixed rain and sleet
		WEATHER_MAP.put(6, R.drawable.weather_channel_06);
		// 7 mixed snow and sleet
		WEATHER_MAP.put(7, R.drawable.weather_channel_07);
		// 8 freezing drizzle
		WEATHER_MAP.put(8, R.drawable.weather_channel_08);
		// 9 drizzle
		WEATHER_MAP.put(9, R.drawable.weather_channel_09);
		// 10 freezing rain
		WEATHER_MAP.put(10, R.drawable.weather_channel_10);
		// 11 showers
		WEATHER_MAP.put(11, R.drawable.weather_channel_11);
		// 12 showers
		WEATHER_MAP.put(12, R.drawable.weather_channel_12);
		// 13 snow flurries
		WEATHER_MAP.put(13, R.drawable.weather_channel_13);
		// 14 light snow showers
		WEATHER_MAP.put(14, R.drawable.weather_channel_14);
		// 15 blowing snow
		WEATHER_MAP.put(15, R.drawable.weather_channel_15);
		// 16 snow
		WEATHER_MAP.put(16, R.drawable.weather_channel_16);
		// 17 hail
		WEATHER_MAP.put(17, R.drawable.weather_channel_17);
		// 18 sleet
		WEATHER_MAP.put(18, R.drawable.weather_channel_18);
		// 19 dust
		WEATHER_MAP.put(19, R.drawable.weather_channel_19);
		// 20 foggy
		WEATHER_MAP.put(20, R.drawable.weather_channel_20);
		// 21 haze
		WEATHER_MAP.put(21, R.drawable.weather_channel_21);
		// 22 smoky
		WEATHER_MAP.put(22, R.drawable.weather_channel_22);
		// 23 blustery
		WEATHER_MAP.put(23, R.drawable.weather_channel_23);
		// 24 windy
		WEATHER_MAP.put(24, R.drawable.weather_channel_24);
		// 25 cold
		WEATHER_MAP.put(25, R.drawable.default_cold);
		// 26 cloudy
		WEATHER_MAP.put(26, R.drawable.weather_channel_26);
		// 27 mostly cloudy (night)
		WEATHER_MAP.put(27, R.drawable.weather_channel_27);
		// 28 mostly cloudy (day)
		WEATHER_MAP.put(28, R.drawable.weather_channel_28);
		// 29 partly cloudy (night)
		WEATHER_MAP.put(29, R.drawable.weather_channel_29);
		// 30 partly cloudy (day)
		WEATHER_MAP.put(30, R.drawable.weather_channel_30);
		// 31 clear (night)
		WEATHER_MAP.put(31, R.drawable.weather_channel_31);
		// 32 sunny
		WEATHER_MAP.put(32, R.drawable.weather_channel_32);
		// 33 fair (night)
		WEATHER_MAP.put(33, R.drawable.weather_channel_33);
		// 34 fair (day)
		WEATHER_MAP.put(34, R.drawable.weather_channel_34);
		// 35 mixed rain and hail
		WEATHER_MAP.put(35, R.drawable.weather_channel_40);
		// 36 hot
		WEATHER_MAP.put(36, R.drawable.weather_channel_36);
		// 37 isolated thunderstorms
		WEATHER_MAP.put(37, R.drawable.weather_channel_37);
		// 38 scattered thunderstorms
		WEATHER_MAP.put(38, R.drawable.weather_channel_38);
		// 39 scattered thunderstorms
		WEATHER_MAP.put(39, R.drawable.weather_channel_38);
		// 40 scattered showers
		WEATHER_MAP.put(40, R.drawable.weather_channel_40);
		// 41 heavy snow
		WEATHER_MAP.put(41, R.drawable.weather_channel_42);
		// 42 scattered snow showers
		WEATHER_MAP.put(42, R.drawable.weather_channel_13);
		// 43 heavy snow
		WEATHER_MAP.put(43, R.drawable.weather_channel_42);
		// 44 partly cloudy
		WEATHER_MAP.put(44, R.drawable.weather_channel_26);
		// 45 thundershowers
		WEATHER_MAP.put(45, R.drawable.weather_channel_45);
		// 46 snow showers
		WEATHER_MAP.put(46, R.drawable.weather_channel_46);
		// 47 isolated thundershowers
		WEATHER_MAP.put(47, R.drawable.weather_channel_47);

		// NOT FOUND
		WEATHER_MAP.put(3200, R.drawable.weather_channel_44);
	}

	public static int getImageResourceFromCode(final int code) {
		final Integer imageRes = WEATHER_MAP.get(code);
		return null != imageRes
				? imageRes
				: R.drawable.weather_channel_44;
	}

	public static int getStatus(final WeatherChoice woeidChoice) {
		if (woeidChoice.isActive()) {
			return R.drawable.active_circle_green;
		}
		return R.drawable.inactive_circle_red;
	}
}
