package com.morgan.design.android.util;

import org.acra.ErrorReporter;

public class ACRAErrorLogger {

	private static final String LOG_TAG = "ACRAErrorLogger";

	public enum Type {
		//@formatter:off
		HTTP_REQUEST_FAILURE(""),
		SQL_EXCEPTION(""),
		SQL_UPDATE_EXCEPTION(""),
		WEATHER_LOOKUP(""),
		WOEID_PARDER(""),
		YAHOO_GEOCODE(""),
		YAHOO_WEATHER_INFO(""),
		UNKNOWN_FLAG_CODE("");
		//@formatter:on

		private final String message;

		private Type(String value) {
			this.message = value;
		}

		public String format(String value) {
			return null != value ? String.format(this.message, value) : this.message;
		}
	}

	public static void handleSilentException(final Exception caughtException) {
		// Always warn to console
		Logger.w(LOG_TAG, caughtException.getMessage());
		// Silently send exception to default Google handler
		ErrorReporter.getInstance().handleSilentException(caughtException);
	}

	public static void handleException(final Exception caughtException) {
		// Always warn to console
		Logger.w(LOG_TAG, caughtException.getMessage());
		// Loudly send exception to default Google handler, force user knowledge
		ErrorReporter.getInstance().handleException(caughtException);
	}

	public static void logErrorType(Type type, String message) {
		Logger.w(LOG_TAG, type.name() + " : " + type.format(message));

		ErrorReporter.getInstance().putCustomData(CustomReportSender.CUSTOME_ERROR_KEY,
				type.name() + " : " + type.format(message));
	}

}
