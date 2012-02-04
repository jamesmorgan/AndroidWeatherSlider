package com.morgan.design.android.util;

import org.acra.ErrorReporter;

public class ACRAErrorLogger {

	public enum Type {
		HTTP_REQUEST_FAILURE,

		SQL_EXCEPTION,

		SQL_UPDATE_EXCEPTION,

		WEATHER_LOOKUP,

		WOEID_PARDER,

		YAHOO_GEOCODE,

		YAHOO_WEATHER_INFO,

		UNKNOWN_FLAG_CODE;
	}

	public static void logSlientExcpetion(final Exception caughtException) {
		ErrorReporter.getInstance().handleSilentException(caughtException);
	}

	public static void logExcpetion(final Exception caughtException) {
		ErrorReporter.getInstance().handleException(caughtException);
	}

	// FIXME -> Create google documents ACRA tracker, catch oddities in
	// application.
	// FIXME -> ** Unknown country flags codes - done
	// FIXME -> ** XML conversion errors - done
	// FIXME -> ** Weather lookup failures - done
	// FIXME -> ** HTTP request failures - done
	// FIXME -> ** DB failures, including update - done

	public static void logUnknownExcpeiton(Type type, Throwable throwable, String message) {

	}

	public static void logUnknownExcpeiton(Type type, Throwable throwable) {

	}

	public static void logUnknownIssue(Type type, String issue) {

	}

}
