package com.morgan.design.android.domain.types;

import com.morgan.design.android.util.Logger;

public enum OverviewMode {

	WEB, OVERVIEW, POPUP;

	public static String fromPref(final String overview) {
		for (final OverviewMode type : values()) {
			if (type.name().equalsIgnoreCase(overview)) {
				return type.name();
			}
		}
		Logger.e("OverviewMode", "Unable to determine overview preference for [%s]", overview);
		return null;
	}

	public static OverviewMode to(final String overview) {
		for (final OverviewMode type : values()) {
			if (type.name().equalsIgnoreCase(overview)) {
				return type;
			}
		}
		Logger.e("OverviewMode", "Unable to convert overview from [%s]", overview);
		return null;
	}
}
