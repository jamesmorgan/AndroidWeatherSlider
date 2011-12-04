package com.morgan.design.android.util;

public class TempUtils {

	// f: Fahrenheit
	// c: Celsius

	static final Character DEGREE = new Character('\u00b0');

	public static String fromAbrevToFull(final String value) {
		if ("c".equalsIgnoreCase(value)) {
			return "Celsius";
		}
		if ("f".equalsIgnoreCase(value)) {
			return "Fahrenheit";
		}
		return value;
	}

	public static String fromSingleToDegree(final String value) {
		if ("c".equalsIgnoreCase(value)) {
			return DEGREE + "C";
		}
		if ("f".equalsIgnoreCase(value)) {
			return DEGREE + "F";
		}
		return value;
	}
}
