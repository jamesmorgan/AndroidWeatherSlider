package com.morgan.design.android.domain;

import com.morgan.design.android.util.Logger;

public enum Temperature {

	// f: Fahrenheit
	// c: Celsius

	FAHRENHEIT("f", "Fahrenheit"),

	CELSIUS("c", "Celsius");

	private String queryValue;
	private String value;

	private Temperature(final String queryValue, final String value) {
		this.queryValue = queryValue;
		this.value = value;
	}

	public static Temperature to(final String temp) {
		for (final Temperature type : values()) {
			if (type.getQueryValue().equalsIgnoreCase(temp)) {
				return type;
			}
		}
		Logger.e("Temperature", "Unable to convert Temperature from [%s]", temp);
		return Temperature.CELSIUS;
	}

	public String getQueryValue() {
		return this.queryValue;
	}

	public String getValue() {
		return this.value;
	}

}
