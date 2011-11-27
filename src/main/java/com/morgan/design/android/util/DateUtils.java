package com.morgan.design.android.util;

import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

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
}
