package com.morgan.design.android.domain.types;

import com.morgan.design.android.util.Logger;

public enum DayOfWeek {

	// Mon Tue Wed Thu Fri Sat Sun

	NA("", ""),

	MONDAY("Monday", "Mon"),

	TUESDAY("Tuesday", "Tue"),

	WEDNESDAY("Wednesday", "Wed"),

	THURSDAY("Thursday", "Thu"),

	FRIDAY("Friday", "Fri"),

	SATURDAY("Saturday", "Sat"),

	SUNDAY("Sunday", "Sun");

	private String full;
	private String abrev;

	private DayOfWeek(final String full, final String abrev) {
		this.full = full;
		this.abrev = abrev;
	}

	public static DayOfWeek from(final String temp) {
		for (final DayOfWeek type : values()) {
			if (type.abrev.equalsIgnoreCase(temp)) {
				return type;
			}
		}
		Logger.e("DayOfWeek", "Unable to convert DayOfWeek from [%s]", temp);
		return NA;
	}

	public String full() {
		return this.full;
	}

	public String abrev() {
		return this.abrev;
	}

}
