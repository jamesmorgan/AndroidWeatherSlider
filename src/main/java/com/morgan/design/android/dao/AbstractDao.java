package com.morgan.design.android.dao;

import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.morgan.design.Logger;
import com.morgan.design.android.repository.DatabaseHelper;

public abstract class AbstractDao<T, ID extends Serializable> {

	private static final String LOG_TAG = "AbstractDao";

	public final Dao<T, ID> dao;
	public final DatabaseHelper databaseHelper;

	public AbstractDao(final DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.dao = getDao();
	}

	@SuppressWarnings("unchecked")
	public Dao<T, ID> getDao() {
		try {
			if (isNull(this.dao)) {
				return this.databaseHelper.getDao((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
			}
			return null;
		}
		catch (final SQLException sqlException) {
			logError(sqlException);
		}
		return null;
	}

	public static void logError(final Exception e) {
		Logger.e(LOG_TAG, "SQLException ", e);
		e.printStackTrace();
	}
}
