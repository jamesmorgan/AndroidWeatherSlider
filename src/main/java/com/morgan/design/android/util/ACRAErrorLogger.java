package com.morgan.design.android.util;

import org.acra.ErrorReporter;

public class ACRAErrorLogger {

	public enum Type {
		HTTP_REQUEST_FAILURE,

		SQL_UPDATE_EXCEPTION,

		WEATHER_LOOKUP,

		WOEID_PARSER,

		YAHOO_GEOCODE,

		YAHOO_WEATHER_INFO,

		UNKNOWN_FLAG_CODE;
	}

	public static void logSlientExcpetion(final Throwable caughtException) {
		ErrorReporter.getInstance().handleSilentException(caughtException);
	}

	public static void recordUnknownIssue(Type type, String message) {
		ErrorReporter.getInstance().putCustomData(type.name(), message);
	}

}
