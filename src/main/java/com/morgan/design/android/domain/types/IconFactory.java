package com.morgan.design.android.domain.types;

import java.util.HashMap;
import java.util.Map;

import com.weatherslider.morgan.design.R;

public class IconFactory {
	public static final Map<Integer, Integer> WEATHER_MAP;
	static {
		WEATHER_MAP = new HashMap<Integer, Integer>();
		WEATHER_MAP.put(1, R.drawable.a2);
		WEATHER_MAP.put(2, R.drawable.a2);
		WEATHER_MAP.put(3, R.drawable.a2);
		WEATHER_MAP.put(4, R.drawable.a2);
		WEATHER_MAP.put(5, R.drawable.a5);
		WEATHER_MAP.put(6, R.drawable.a5);
		WEATHER_MAP.put(7, R.drawable.a5);
		WEATHER_MAP.put(8, R.drawable.a8);
		WEATHER_MAP.put(9, R.drawable.a9);
		WEATHER_MAP.put(10, R.drawable.a9);
		WEATHER_MAP.put(11, R.drawable.a8);
		WEATHER_MAP.put(12, R.drawable.a8);
		WEATHER_MAP.put(13, R.drawable.a13);
		WEATHER_MAP.put(14, R.drawable.a13);
		WEATHER_MAP.put(15, R.drawable.a13);
		WEATHER_MAP.put(16, R.drawable.a13);
		WEATHER_MAP.put(17, R.drawable.a19);
		WEATHER_MAP.put(18, R.drawable.a19);
		WEATHER_MAP.put(19, R.drawable.a19);
		WEATHER_MAP.put(20, R.drawable.a19);
		WEATHER_MAP.put(21, R.drawable.a19);
		WEATHER_MAP.put(22, R.drawable.a19);
		WEATHER_MAP.put(23, R.drawable.a19);
		WEATHER_MAP.put(24, R.drawable.a24);
		WEATHER_MAP.put(25, R.drawable.a25);
		WEATHER_MAP.put(26, R.drawable.a26);
		WEATHER_MAP.put(27, R.drawable.a27);
		WEATHER_MAP.put(28, R.drawable.a28);
		WEATHER_MAP.put(29, R.drawable.a29);
		WEATHER_MAP.put(30, R.drawable.a30);
		WEATHER_MAP.put(31, R.drawable.a31);
		WEATHER_MAP.put(32, R.drawable.a32);
		WEATHER_MAP.put(33, R.drawable.a33);
		WEATHER_MAP.put(34, R.drawable.a34);
		WEATHER_MAP.put(35, R.drawable.a35);
		WEATHER_MAP.put(36, R.drawable.a36);
		WEATHER_MAP.put(37, R.drawable.a2);
		WEATHER_MAP.put(38, R.drawable.a2);
		WEATHER_MAP.put(39, R.drawable.a2);
		WEATHER_MAP.put(40, R.drawable.a2);
		WEATHER_MAP.put(41, R.drawable.a41);
		WEATHER_MAP.put(42, R.drawable.a41);
		WEATHER_MAP.put(43, R.drawable.a41);
		WEATHER_MAP.put(44, R.drawable.a44);
		WEATHER_MAP.put(45, R.drawable.a45);
		WEATHER_MAP.put(46, R.drawable.a46);
		WEATHER_MAP.put(47, R.drawable.a46);
		WEATHER_MAP.put(3200, R.drawable.a3200);
	}

	public static int getImageResourceFromCode(final int code) {
		final Integer imageRes = WEATHER_MAP.get(code);
		return null != imageRes
				? imageRes
				: R.drawable.a0;
	}
}
