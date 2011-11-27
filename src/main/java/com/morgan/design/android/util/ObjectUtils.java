package com.morgan.design.android.util;

import java.util.List;

public final class ObjectUtils {

	public static final boolean isNull(final Object value) {
		return null == value;
	}

	public static final boolean isNotNull(final Object value) {
		return !isNull(value);
	}

	public static final boolean isEmpty(final List<?> value) {
		if (isNull(value)) {
			return false;
		}
		return value.isEmpty();
	}

	public static final boolean isNotEmpty(final List<?> value) {
		return !isEmpty(value);
	}

	public static final boolean isZero(final int value) {
		return 0 == value;
	}

	public static boolean isZero(final long value) {
		return value == 0L;
	}

	public static final boolean isNotZero(final int value) {
		return 0 != value;
	}

	public static final boolean stringHasValue(final String value) {
		return null != value && !"".equals(value);
	}
}
