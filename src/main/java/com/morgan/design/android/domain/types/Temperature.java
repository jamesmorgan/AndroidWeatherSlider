package com.morgan.design.android.domain.types;

import com.morgan.design.android.util.Logger;

public enum Temperature implements Abrev {

	// f: Fahrenheit
	// c: Celsius

	FAHRENHEIT("f", "Fahrenheit"),

	CELSIUS("c", "Celsius");

	static final Character DEGREE = new Character('\u00b0');

	private String abrev;
	private String full;

	private Temperature(final String abrev, final String full) {
		this.abrev = abrev;
		this.full = full;
	}

	public static Temperature to(final String temp) {
		for (final Temperature type : values()) {
			if (type.abrev()
				.equalsIgnoreCase(temp)) {
				return type;
			}
		}
		Logger.e("Temperature", "Unable to convert Temperature from [%s]", temp);
		return Temperature.CELSIUS;
	}

	@Override
	public String abrev() {
		return this.abrev;
	}

	public String getFull() {
		return this.full;
	}

	public static String withDegree(final String abrev2) {
		if (null == abrev2) {
			return "";
		}
		return DEGREE + abrev2;
	}

}
