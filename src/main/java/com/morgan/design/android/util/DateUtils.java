package com.morgan.design.android.util;

import static com.morgan.design.android.util.ObjectUtils.isBlank;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	private static final String LOG_TAG = "DateUtils";

	/**
	 * COnvert date to string
	 * 
	 * @param date the {@link Date}
	 * @return String in the format of <code>Wed, 4 Jul 2011 12:08 am</code>
	 */
	public static final String dateToSimpleDateFormat(final Date date) {
		if (isNull(date)) {
			return "";
		}
		final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
		return dateFormat.format(date);
	}

	// Fri, 09 Dec 2011 12:18 pm GMT
	public static Date fromRFC822(final String date) {
		if (isBlank(date)) {
			return null;
		}
		try {
			final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a z");
			return dateFormat.parse(date);
		}
		catch (final ParseException e) {
			Logger.e(LOG_TAG, "ParseException: ", e);
			e.printStackTrace();
		}
		return null;
	}

	public static final SimpleDateFormat rfc822DateFormats[] = new SimpleDateFormat[] { new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z"),
			new SimpleDateFormat("EEE, d MMM yy HH:mm z"), new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"),
			new SimpleDateFormat("EEE, d MMM yyyy HH:mm z"), new SimpleDateFormat("d MMM yy HH:mm z"),
			new SimpleDateFormat("d MMM yy HH:mm:ss z"), new SimpleDateFormat("d MMM yyyy HH:mm z"),
			new SimpleDateFormat("d MMM yyyy HH:mm:ss z"), };

	public static Date fromSimpleDate(final String date) {
		if (isBlank(date)) {
			return null;
		}
		try {
			final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
			return dateFormat.parse(date);
		}
		catch (final ParseException e) {
			Logger.e(LOG_TAG, "ParseException: ", e);
			e.printStackTrace();
		}
		return null;
	}

	public static String toSimpleDate(final Date date) {
		if (isNull(date)) {
			return "";
		}
		final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
		return dateFormat.format(date);
	}

	public static String dateToTime(final Date currentDate) {
		if (isNull(currentDate)) {
			return "";
		}
		final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm a");
		return dateFormat.format(currentDate);
	}

	// Mon Tue Wed Thu Fri Sat Sun
	// TODO convert from abreviation to full
}
